package me.wibisa.kulotmdb.core.data.remote.service

import me.wibisa.kulotmdb.BuildConfig
import me.wibisa.kulotmdb.core.util.API_KEY
import okhttp3.Interceptor
import okhttp3.Response


internal class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val url = originalUrl.newBuilder()
//            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY) // Dear Reviewer Untuk TMDB API KEY silahkan menambahkan pada local.properties
            .addQueryParameter("api_key", API_KEY) // Agar tidak merepotkan reviewer jadi saya menggunakan api key ini
            .build()

        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}