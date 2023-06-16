package me.wibisa.kulotmdb.ui.genre

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wibisa.kulotmdb.R
import me.wibisa.kulotmdb.adapter.GenreAdapter
import me.wibisa.kulotmdb.adapter.GenreListener
import me.wibisa.kulotmdb.core.util.ResultWrapping
import me.wibisa.kulotmdb.core.util.gone
import me.wibisa.kulotmdb.core.util.isNetworkAvailable
import me.wibisa.kulotmdb.core.util.showToast
import me.wibisa.kulotmdb.core.util.visible
import me.wibisa.kulotmdb.databinding.FragmentGenreBinding
import me.wibisa.kulotmdb.viewmodel.GenreViewModel

@AndroidEntryPoint
class GenreFragment : Fragment() {
    private lateinit var binding: FragmentGenreBinding
    private lateinit var adapter: GenreAdapter
    private val viewModel: GenreViewModel by viewModels()
    private val mainNavController: NavController? by lazy { view?.findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGenreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        componentUiSetup()

        getGenres()

        observeGenresUiState()
    }

    private fun getGenres() {
        if (requireContext().isNetworkAvailable()) {
            viewModel.getGenres()
        } else {
            requireContext().showToast(getString(R.string.internet_is_not_available))
        }
    }

    private fun componentUiSetup() {
        adapter = GenreAdapter(GenreListener {
            val destination = GenreFragmentDirections.actionGenreFragmentToMoviesByGenreFragment(it)
            mainNavController?.navigate(destination)
        })
        binding.rvGenre.adapter = adapter
    }

    private fun observeGenresUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.genresUiState.collect { uiState ->
                    when (uiState) {
                        is ResultWrapping.Success -> {
                            binding.loadingIndicator.gone()
                            adapter.submitList(uiState.data)
                            viewModel.getGenresCompleted()
                        }

                        is ResultWrapping.Loading -> {
                            binding.loadingIndicator.visible()
                        }

                        is ResultWrapping.Error -> {
                            binding.loadingIndicator.gone()
                            requireContext().showToast(
                                getString(R.string.something_went_wrong_try_again_later),
                                length = Toast.LENGTH_LONG
                            )
                            viewModel.getGenresCompleted()
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}