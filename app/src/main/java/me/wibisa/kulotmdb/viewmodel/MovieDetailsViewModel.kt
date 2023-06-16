package me.wibisa.kulotmdb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wibisa.kulotmdb.core.data.remote.response.MovieDetailsResponse
import me.wibisa.kulotmdb.core.data.repository.MovieDetailsRepository
import me.wibisa.kulotmdb.core.util.ResultWrapping
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(private val movieDetailsRepository: MovieDetailsRepository) :
    ViewModel() {

    private val _movieDetailsUiState =
        MutableStateFlow<ResultWrapping<MovieDetailsResponse>>(ResultWrapping.Empty)
    val movieDetailsUiState: StateFlow<ResultWrapping<MovieDetailsResponse>> =
        _movieDetailsUiState.asStateFlow()

    fun getMovieDetails(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _movieDetailsUiState.value = ResultWrapping.Loading
            val result = movieDetailsRepository.getMovieDetails(movieId)
            _movieDetailsUiState.value = result
        }
    }

    fun getMovieDetailsCompleted() {
        _movieDetailsUiState.value = ResultWrapping.Empty
    }
}