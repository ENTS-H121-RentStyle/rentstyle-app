package com.example.rentstyle.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.rentstyle.model.repository.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserViewModel (private val userRepository: UserRepository): ViewModel() {
    var userData = MutableLiveData<List<String?>>(arrayListOf())

    suspend fun getUserProfile () = userRepository.getUserData()
    suspend fun updateUserProfile(userName: RequestBody,
                                  birthDate: RequestBody,
                                  address: RequestBody,
                                  phone: RequestBody,
                                  gender: RequestBody,
                                  file: MultipartBody.Part) = userRepository
                                      .updateUserProfile(userName, birthDate, address, phone, gender, file)
}