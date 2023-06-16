package me.wibisa.kulotmdb.core.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import me.wibisa.kulotmdb.core.data.remote.response.Review
import me.wibisa.kulotmdb.core.data.remote.service.TheMovieDBApi

class ReviewPagingSource constructor(
    private val theMovieDBApi: TheMovieDBApi,
    private val movieId: Int,
) : PagingSource<Int, Review>() {
    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->

            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = theMovieDBApi.getReviewsMovie(page = position, movieId = movieId)
            if (response.isSuccessful) {
                val reviews = response.body()?.results
                LoadResult.Page(
                    data = reviews ?: emptyList(),
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (reviews.isNullOrEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("Failed to get review list"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
    }
}