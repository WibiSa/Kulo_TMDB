package me.wibisa.kulotmdb.ui.moviesbygenre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import me.wibisa.kulotmdb.R
import me.wibisa.kulotmdb.adapter.MovieAdapter
import me.wibisa.kulotmdb.adapter.MovieListener
import me.wibisa.kulotmdb.core.util.isNetworkAvailable
import me.wibisa.kulotmdb.core.util.showToast
import me.wibisa.kulotmdb.databinding.FragmentMoviesByGenreBinding
import me.wibisa.kulotmdb.viewmodel.MovieByGenreViewModel

@AndroidEntryPoint
class MoviesByGenreFragment : Fragment() {
    private lateinit var binding: FragmentMoviesByGenreBinding
    private lateinit var adapter: MovieAdapter
    private val viewModel: MovieByGenreViewModel by viewModels()
    private val mainNavController: NavController? by lazy { view?.findNavController() }
    private val navArgs: MoviesByGenreFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoviesByGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        discoverMovie()

        componentUiSetup()
    }

    private fun componentUiSetup() {
        val genreName = navArgs.genre.name
        binding.topAppbar.title = genreName
        binding.topAppbar.setNavigationOnClickListener { mainNavController?.popBackStack() }

        adapter = MovieAdapter(MovieListener {
            val destination =
                MoviesByGenreFragmentDirections.actionMoviesByGenreFragmentToMovieDetailsFragment(it.id)
            mainNavController?.navigate(destination)
        })
        binding.rvMovieByGenre.adapter = adapter
        pagingLoadState()
    }

    private fun pagingLoadState() {
        adapter.addLoadStateListener { loadState ->

            when (loadState.refresh) {
                is LoadState.NotLoading -> {
                    binding.loadingIndicator.hide()
                }

                is LoadState.Loading -> {
                    binding.loadingIndicator.show()
                }

                is LoadState.Error -> {
                    binding.loadingIndicator.hide()
                    requireContext().showToast(getString(R.string.something_went_wrong_try_again_later))
                }
            }
        }
    }

    private fun discoverMovie() {
        if (requireContext().isNetworkAvailable()) {
            val genreId = navArgs.genre.id
            viewModel.discoverMovie(genreId).observe(viewLifecycleOwner) { movies ->
                adapter.submitData(lifecycle, movies)
            }
        } else {
            requireContext().showToast(getString(R.string.internet_is_not_available))
        }
    }
}