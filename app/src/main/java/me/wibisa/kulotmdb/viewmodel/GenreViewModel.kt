package me.wibisa.kulotmdb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.wibisa.kulotmdb.core.data.remote.response.Genre
import me.wibisa.kulotmdb.core.data.repository.DiscoverRepository
import me.wibisa.kulotmdb.core.util.ResultWrapping
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(private val discoverRepository: DiscoverRepository) :
    ViewModel() {

    private val _genresUiState = MutableStateFlow<ResultWrapping<List<Genre>>>(ResultWrapping.Empty)
    val genresUiState: StateFlow<ResultWrapping<List<Genre>>> = _genresUiState.asStateFlow()

    fun getGenres() {
        viewModelScope.launch(Dispatchers.IO) {
            _genresUiState.value = ResultWrapping.Loading
            val result = discoverRepository.getGenres()
            _genresUiState.value = result
        }
    }

    fun getGenresCompleted() {
        _genresUiState.value = ResultWrapping.Empty
    }
}