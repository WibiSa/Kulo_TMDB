package me.wibisa.kulotmdb.core.data.remote.service

import me.wibisa.kulotmdb.core.data.remote.response.DiscoverMovieResponse
import me.wibisa.kulotmdb.core.data.remote.response.GenreListResponse
import me.wibisa.kulotmdb.core.data.remote.response.MovieDetailsResponse
import me.wibisa.kulotmdb.core.data.remote.response.ReviewListResponse
import me.wibisa.kulotmdb.core.data.remote.response.VideosResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBApi {

    @GET("genre/movie/list")
    suspend fun getOfficialGenresForMovies(
    ): Response<GenreListResponse>

    @GET("discover/movie?language=en&sort_by=popularity.desc")
    suspend fun discoverMovie(
        @Query("page") page: Int,
        @Query("with_genres") genreId: Int?,
    ): Response<DiscoverMovieResponse>

    @GET("movie/{movieId}")
    suspend fun getMovieDetailsById(
        @Path("movieId") movieId: Int,
    ): Response<MovieDetailsResponse>

    @GET("movie/{movieId}/reviews")
    suspend fun getReviewsMovie(
        @Path("movieId") movieId: Int,
        @Query("page") page: Int,
    ): Response<ReviewListResponse>

    @GET("movie/{movieId}/videos")
    suspend fun getVideosMovie(
        @Path("movieId") movieId: Int,
    ): Response<VideosResponse>
}