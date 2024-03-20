package com.androidants.sampleapp.di

import com.androidants.sampleapp.common.Constants
import com.androidants.sampleapp.data.ApiCalls
import com.androidants.sampleapp.data.repository.MainRepository
import com.androidants.sampleapp.data.repository.MainRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApi(): ApiCalls {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiCalls::class.java)
    }

    @Provides
    @Singleton
    fun provideMainRepository(api: ApiCalls): MainRepository {
        return MainRepositoryImpl(api)
    }

}