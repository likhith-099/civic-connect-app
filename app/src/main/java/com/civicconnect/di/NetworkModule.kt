package com.civicconnect.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.civicconnect.utils.Constants
import com.civicconnect.data.remote.AuthApi
import com.civicconnect.data.remote.ComplaintApi
import com.civicconnect.data.remote.AiApi
import com.civicconnect.data.remote.AdminApi
import com.civicconnect.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): Interceptor {
        return Interceptor { chain ->
            val token = runBlocking {
                try {
                    tokenManager.token.first()
                } catch (e: Exception) {
                    null
                }
            }
            val request = chain.request().newBuilder().apply {
                if (!token.isNullOrBlank()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }.build()
            
            val response = chain.proceed(request)
            
            if (response.code == 401) {
                runBlocking { tokenManager.clearToken() }
            }
            
            response
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideComplaintApi(retrofit: Retrofit): ComplaintApi {
        return retrofit.create(ComplaintApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAiApi(retrofit: Retrofit): AiApi {
        return retrofit.create(AiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAdminApi(retrofit: Retrofit): AdminApi {
        return retrofit.create(AdminApi::class.java)
    }
}
