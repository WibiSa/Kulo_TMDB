package me.wibisa.kulotmdb.ui.videos

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.wibisa.kulotmdb.R
import me.wibisa.kulotmdb.adapter.VideoAdapter
import me.wibisa.kulotmdb.adapter.VideoListener
import me.wibisa.kulotmdb.core.util.ResultWrapping
import me.wibisa.kulotmdb.core.util.getYoutubeVideoPath
import me.wibisa.kulotmdb.core.util.gone
import me.wibisa.kulotmdb.core.util.isNetworkAvailable
import me.wibisa.kulotmdb.core.util.showToast
import me.wibisa.kulotmdb.core.util.visible
import me.wibisa.kulotmdb.databinding.FragmentVideosMovieBinding
import me.wibisa.kulotmdb.viewmodel.VideoMovieViewModel

@AndroidEntryPoint
class VideosMovieFragment : Fragment() {
    private lateinit var binding: FragmentVideosMovieBinding
    private lateinit var adapter: VideoAdapter
    private val viewModel: VideoMovieViewModel by viewModels()
    private val mainNavController: NavController? by lazy { view?.findNavController() }
    private val navArgs: VideosMovieFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideosMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        componentUiSetup()

        getVideos()

        observeVideoUiState()
    }

    private fun getVideos() {
        if (requireContext().isNetworkAvailable()) {
            viewModel.getVideos(navArgs.movieId)
        } else {
            requireContext().showToast(getString(R.string.internet_is_not_available))
        }
    }

    private fun observeVideoUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.videoUiState.collect { uiState ->
                    when (uiState) {
                        is ResultWrapping.Success -> {
                            binding.loadingIndicator.gone()
                            adapter.submitList(uiState.data)
                            viewModel.getVideosCompleted()
                        }

                        is ResultWrapping.Loading -> {
                            binding.loadingIndicator.visible()
                        }

                        is ResultWrapping.Error -> {
                            binding.loadingIndicator.gone()
                            requireContext().showToast(getString(R.string.something_went_wrong_try_again_later))
                            viewModel.getVideosCompleted()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun componentUiSetup() {
        binding.topAppbar.setNavigationOnClickListener { mainNavController?.popBackStack() }

        adapter = VideoAdapter(VideoListener {
            try {
                val playVideoIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(it.key.getYoutubeVideoPath()))
                startActivity(playVideoIntent)
            } catch (e: Exception) {
                requireContext().showToast(getString(R.string.something_went_wrong))
            }
        })
        binding.rvReviews.adapter = adapter
    }
}