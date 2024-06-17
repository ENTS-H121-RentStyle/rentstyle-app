package com.example.rentstyle.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rentstyle.di.Injection
import com.example.rentstyle.model.repository.SellerRepository

class SellerViewModelFactory private constructor(private val sellerRepository: SellerRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SellerViewModel::class.java)) {
            return SellerViewModel(sellerRepository) as T
        } else if (modelClass.isAssignableFrom(SellerRegisterViewModel::class.java)) {
            return SellerRegisterViewModel(sellerRepository) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(sellerRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

        companion object {

            @Volatile
            private var instance: SellerViewModelFactory? = null
            fun getInstance (context: Context): SellerViewModelFactory =
                instance ?: synchronized(this) {
                    instance ?: SellerViewModelFactory(Injection.provideSellerRepository(context))
                }.also { instance = it }
        }
}