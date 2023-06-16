package me.wibisa.kulotmdb.ui.reviews

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
import me.wibisa.kulotmdb.adapter.ReviewAdapter
import me.wibisa.kulotmdb.core.util.isNetworkAvailable
import me.wibisa.kulotmdb.core.util.showToast
import me.wibisa.kulotmdb.databinding.FragmentReviewListDialogBinding
import me.wibisa.kulotmdb.viewmodel.ReviewListViewModel

@AndroidEntryPoint
class ReviewListDialogFragment : Fragment() {
    private lateinit var binding: FragmentReviewListDialogBinding
    private lateinit var adapter: ReviewAdapter
    private val viewModel: ReviewListViewModel by viewModels()
    private val mainNavController: NavController? by lazy { view?.findNavController() }
    private val navArgs: ReviewListDialogFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getReviewList()

        componentUiSetup()
    }

    private fun componentUiSetup() {
        binding.topAppbar.setNavigationOnClickListener { mainNavController?.popBackStack() }

        adapter = ReviewAdapter()
        binding.rvReviews.adapter = adapter
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

    private fun getReviewList() {
        if (requireContext().isNetworkAvailable()) {
            viewModel.getReviews(navArgs.movieId).observe(viewLifecycleOwner) { reviews ->
                adapter.submitData(lifecycle, reviews)
            }
        } else {
            requireContext().showToast(getString(R.string.internet_is_not_available))
        }
    }
}