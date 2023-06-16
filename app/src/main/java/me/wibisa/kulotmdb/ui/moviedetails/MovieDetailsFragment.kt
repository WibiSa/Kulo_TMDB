package me.wibisa.kulotmdb.ui.moviedetails

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
import me.wibisa.kulotmdb.core.data.remote.response.MovieDetailsResponse
import me.wibisa.kulotmdb.core.util.ResultWrapping
import me.wibisa.kulotmdb.core.util.convertDateFormat
import me.wibisa.kulotmdb.core.util.convertDecimal
import me.wibisa.kulotmdb.core.util.convertGenresToString
import me.wibisa.kulotmdb.core.util.convertMinutesToString
import me.wibisa.kulotmdb.core.util.convertToCurrencyString
import me.wibisa.kulotmdb.core.util.getBackdropPath
import me.wibisa.kulotmdb.core.util.gone
import me.wibisa.kulotmdb.core.util.isNetworkAvailable
import me.wibisa.kulotmdb.core.util.loadBackdrop
import me.wibisa.kulotmdb.core.util.showToast
import me.wibisa.kulotmdb.core.util.visible
import me.wibisa.kulotmdb.databinding.FragmentMovieDetailsBinding
import me.wibisa.kulotmdb.viewmodel.MovieDetailsViewModel

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private lateinit var binding: FragmentMovieDetailsBinding
    private val viewModel: MovieDetailsViewModel by viewModels()
    private val mainNavController: NavController? by lazy { view?.findNavController() }
    private val navArgs: MovieDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        componentUiSetup()

        getMovieDetails()

        observeMovieDetailsUiState()
    }

    private fun getMovieDetails() {
        if (requireContext().isNetworkAvailable()) {
            viewModel.getMovieDetails(navArgs.movieId)
        } else {
            requireContext().showToast(getString(R.string.internet_is_not_available))
        }
    }

    private fun componentUiSetup() {
        binding.topAppbar.setNavigationOnClickListener { mainNavController?.popBackStack() }

        binding.btnPlayTrailer.setOnClickListener {
            val destination =
                MovieDetailsFragmentDirections.actionMovieDetailsFragmentToVideosMovieFragment(
                    navArgs.movieId
                )
            mainNavController?.navigate(destination)
        }

        binding.btnShowReview.setOnClickListener {
            val destination =
                MovieDetailsFragmentDirections.actionMovieDetailsFragmentToReviewListDialogFragment(
                    navArgs.movieId
                )
            mainNavController?.navigate(destination)
        }
    }

    private fun bindingMovieDetailsToViews(movieDetails: MovieDetailsResponse) {
        binding.apply {
            imgBackdrop.loadBackdrop(movieDetails.backdropPath.getBackdropPath())
            tvCombine.text = getString(
                R.string.movie_details_combine_content,
                convertDateFormat(movieDetails.releaseDate),
                movieDetails.originalLanguage,
                convertDecimal(movieDetails.voteAverage)
            )
            tvTitle.text = movieDetails.title
            tvOverview.text = movieDetails.overview
            tvGenres.text = convertGenresToString(movieDetails.genres)
            tvStatus.text = movieDetails.status
            tvRuntime.text = convertMinutesToString(movieDetails.runtime)
            tvRevenue.text = convertToCurrencyString(movieDetails.revenue)
            tvPopularity.text = movieDetails.popularity.toInt().toString()
        }
    }

    private fun observeMovieDetailsUiState() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.movieDetailsUiState.collect { uiState ->
                    when (uiState) {
                        is ResultWrapping.Success -> {
                            binding.loadingIndicator.gone()
                            bindingMovieDetailsToViews(uiState.data)
                            viewModel.getMovieDetailsCompleted()
                        }

                        is ResultWrapping.Loading -> {
                            binding.loadingIndicator.visible()
                        }

                        is ResultWrapping.Error -> {
                            binding.loadingIndicator.gone()
                            requireContext().showToast(getString(R.string.something_went_wrong_try_again_later))
                            viewModel.getMovieDetailsCompleted()
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}