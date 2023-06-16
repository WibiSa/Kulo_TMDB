package me.wibisa.kulotmdb.core.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import me.wibisa.kulotmdb.core.data.paging.ReviewPagingSource
import me.wibisa.kulotmdb.core.data.remote.response.MovieDetailsResponse
import me.wibisa.kulotmdb.core.data.remote.response.Review
import me.wibisa.kulotmdb.core.data.remote.response.Video
import me.wibisa.kulotmdb.core.data.remote.service.TheMovieDBApi
import me.wibisa.kulotmdb.core.util.ResultWrapping
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailsRepository @Inject constructor(private val theMovieDBApi: TheMovieDBApi) {

    suspend fun getMovieDetails(movieId: Int): ResultWrapping<MovieDetailsResponse> {
        return try {
            val response = theMovieDBApi.getMovieDetailsById(movieId)
            if (response.isSuccessful) {
                val movieDetails = response.body()
                ResultWrapping.Success(movieDetails!!)
            } else {
                ResultWrapping.Error("Terjadi kesalahan.")
            }
        } catch (e: Exception) {
            ResultWrapping.Error(e.message.toString())
        }
    }

    fun getReviewList(movieId: Int): LiveData<PagingData<Review>> {
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                ReviewPagingSource(
                    theMovieDBApi = theMovieDBApi,
                    movieId = movieId
                )
            }
        ).liveData
    }

    suspend fun getVideosMovie(movieId: Int): ResultWrapping<List<Video>> {
        return try {
            val response = theMovieDBApi.getVideosMovie(movieId)
            if (response.isSuccessful) {
                val videos = response.body()?.results
                ResultWrapping.Success(videos!!)
            } else {
                ResultWrapping.Error("Terjadi kesalahan.")
            }
        } catch (e: Exception) {
            ResultWrapping.Error(e.message.toString())
        }
    }
}