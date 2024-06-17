package com.example.rentstyle.di

import android.content.Context
import android.util.Log
import com.example.rentstyle.model.local.datastore.LoginSession
import com.example.rentstyle.model.local.datastore.dataStore
import com.example.rentstyle.model.remote.retrofit.ApiConfig
import com.example.rentstyle.model.repository.SellerRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideSellerRepository (context: Context): SellerRepository {
        val pref = LoginSession.getInstance(context.dataStore)
        val token = runBlocking { pref.getSessionToken().first() }

        val apiService = ApiConfig.getApiService(token.toString())

        return SellerRepository.getInstance(apiService)
    }
}