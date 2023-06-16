package me.wibisa.kulotmdb.core.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import me.wibisa.kulotmdb.core.data.paging.DiscoverMoviePagingSource
import me.wibisa.kulotmdb.core.data.remote.response.Genre
import me.wibisa.kulotmdb.core.data.remote.response.Movie
import me.wibisa.kulotmdb.core.data.remote.service.TheMovieDBApi
import me.wibisa.kulotmdb.core.util.ResultWrapping
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoverRepository @Inject constructor(private val theMovieDBApi: TheMovieDBApi) {

    fun discoverMovie(idGenre: Int?): LiveData<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                DiscoverMoviePagingSource(
                    theMovieDBApi = theMovieDBApi,
                    genreId = idGenre
                )
            }
        ).liveData
    }

    suspend fun getGenres(): ResultWrapping<List<Genre>> {
        return try {
            val response = theMovieDBApi.getOfficialGenresForMovies()
            if (response.isSuccessful) {
                val genres = response.body()?.genres
                ResultWrapping.Success(genres!!)
            } else {
                ResultWrapping.Error("Terjadi Kesalahan.")
            }
        } catch (e: Exception) {
            ResultWrapping.Error(e.message.toString())
        }
    }
}