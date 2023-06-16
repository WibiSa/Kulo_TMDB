package me.wibisa.kulotmdb.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import me.wibisa.kulotmdb.core.data.repository.DiscoverRepository
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(private val discoverRepository: DiscoverRepository) :
    ViewModel() {

    fun discoverMovie(genreId: Int?) =
        discoverRepository.discoverMovie(genreId).cachedIn(viewModelScope)
}