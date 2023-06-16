package me.wibisa.kulotmdb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import me.wibisa.kulotmdb.core.data.repository.MovieDetailsRepository
import javax.inject.Inject

@HiltViewModel
class ReviewListViewModel @Inject constructor(private val movieDetailsRepository: MovieDetailsRepository) :
    ViewModel() {

    fun getReviews(movieId: Int) =
        movieDetailsRepository.getReviewList(movieId).cachedIn(viewModelScope)
}