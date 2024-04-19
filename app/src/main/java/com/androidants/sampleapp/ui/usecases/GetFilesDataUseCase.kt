package com.androidants.sampleapp.ui.usecases

import com.androidants.sampleapp.data.model.file.GetFilesResponse
import com.androidants.sampleapp.data.repository.MainRepository
import retrofit2.Response
import javax.inject.Inject

class GetFilesDataUseCase @Inject constructor(
    private val repository: MainRepository
) {
    suspend fun invoke (screenCode:String) : Response<GetFilesResponse> {
        return repository.getVideoLinks(screenCode)
    }
}