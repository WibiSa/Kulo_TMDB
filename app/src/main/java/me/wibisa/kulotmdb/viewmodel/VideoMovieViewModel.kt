package me.wibisa.kulotmdb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wibisa.kulotmdb.core.data.remote.response.Video
import me.wibisa.kulotmdb.core.data.repository.MovieDetailsRepository
import me.wibisa.kulotmdb.core.util.ResultWrapping
import javax.inject.Inject

@HiltViewModel
class VideoMovieViewModel @Inject constructor(private val movieDetailsRepository: MovieDetailsRepository) :
    ViewModel() {

    private val _videoUiState = MutableStateFlow<ResultWrapping<List<Video>>>(ResultWrapping.Empty)
    val videoUiState: StateFlow<ResultWrapping<List<Video>>> = _videoUiState.asStateFlow()

    fun getVideos(movieId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _videoUiState.value = ResultWrapping.Loading
            val result = movieDetailsRepository.getVideosMovie(movieId)
            _videoUiState.value = result
        }
    }

    fun getVideosCompleted() {
        _videoUiState.value = ResultWrapping.Empty
    }
}