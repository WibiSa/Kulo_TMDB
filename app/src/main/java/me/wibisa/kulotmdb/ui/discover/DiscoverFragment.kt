package me.wibisa.kulotmdb.ui.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import me.wibisa.kulotmdb.R
import me.wibisa.kulotmdb.adapter.MovieAdapter
import me.wibisa.kulotmdb.adapter.MovieListener
import me.wibisa.kulotmdb.core.util.isNetworkAvailable
import me.wibisa.kulotmdb.core.util.showToast
import me.wibisa.kulotmdb.databinding.FragmentDiscoverBinding
import me.wibisa.kulotmdb.viewmodel.DiscoverViewModel

@AndroidEntryPoint
class DiscoverFragment : Fragment() {
    private lateinit var binding: FragmentDiscoverBinding
    private lateinit var adapter: MovieAdapter
    private val viewModel: DiscoverViewModel by viewModels()
    private val mainNavController: NavController? by lazy { view?.findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        discoverMovie()

        componentUiSetup()
    }

    private fun componentUiSetup() {
        adapter = MovieAdapter(MovieListener {
            val destination =
                DiscoverFragmentDirections.actionDiscoverFragmentToMovieDetailsFragment(it.id)
            mainNavController?.navigate(destination)
        })
        binding.rvDiscoverMovie.adapter = adapter
        pagingLoadState()

        binding.swipeRefresh.setOnRefreshListener {
            discoverMovie()
        }
    }

    private fun pagingLoadState() {
        adapter.addLoadStateListener { loadState ->

            when (loadState.refresh) {
                is LoadState.NotLoading -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.loadingIndicator.hide()
                }

                is LoadState.Loading -> {
                    binding.loadingIndicator.show()
                }

                is LoadState.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    binding.loadingIndicator.hide()
                    requireContext().showToast(getString(R.string.something_went_wrong_try_again_later),
                        length = Toast.LENGTH_LONG
                    )
                }
            }
        }
    }

    private fun discoverMovie() {
        if (requireContext().isNetworkAvailable()) {
            viewModel.discoverMovie(NO_GENRE).observe(viewLifecycleOwner) { movies ->
                adapter.submitData(lifecycle, movies)
            }
        } else {
            requireContext().showToast(getString(R.string.internet_is_not_available))
        }
    }

    companion object {
        private val NO_GENRE = null
    }
}