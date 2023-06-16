package me.wibisa.kulotmdb.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.wibisa.kulotmdb.core.data.remote.response.Movie
import me.wibisa.kulotmdb.core.data.remote.service.TheMovieDBApi

class DiscoverMoviePagingSource constructor(
    private val theMovieDBApi: TheMovieDBApi,
    private val genreId: Int?,
) : PagingSource<Int, Movie>() {
    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->

            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = theMovieDBApi.discoverMovie(page = position, genreId = genreId)
            if (response.isSuccessful) {
                val movies = response.body()?.results
                LoadResult.Page(
                    data = movies ?: emptyList(),
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (movies.isNullOrEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("Failed to get movie list"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}