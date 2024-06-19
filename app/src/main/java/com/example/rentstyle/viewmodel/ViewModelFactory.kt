package com.example.rentstyle.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rentstyle.di.Injection
import com.example.rentstyle.model.local.datastore.SettingPref
import com.example.rentstyle.model.repository.ProductRepository
import com.example.rentstyle.model.repository.SellerRepository
import com.example.rentstyle.model.repository.UserRepository

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
            fun getInstance (context: Context): SellerViewModelFactory =
                SellerViewModelFactory(Injection.provideSellerRepository(context))
        }
}

class ProductViewModelFactory private constructor(private val productRepository: ProductRepository) :
        ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddProductViewModel::class.java)) {
            return AddProductViewModel(productRepository) as T
        } else if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
            return ExploreViewModel(productRepository) as T
        } else if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            return CartViewModel(productRepository) as T
        } else if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(productRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

        companion object {
            fun getInstance (context: Context): ProductViewModelFactory =
                ProductViewModelFactory(Injection.provideProductRepository(context))
        }
}

class UserViewModelFactory private constructor(private val userRepository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(userRepository) as T
        } else if (modelClass.isAssignableFrom(TransactionViewModel::class.java)){
            return TransactionViewModel(userRepository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }

    companion object {
        fun getInstance (context: Context): UserViewModelFactory =
            UserViewModelFactory(Injection.provideUserRepository(context))
    }
}

class SettingViewModelFactory (private val pref: SettingPref): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T: ViewModel> create(modelClass: Class<T>): T{
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)){
            return SettingViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {

        @Volatile
        private var instance: SettingViewModelFactory? = null
        fun getInstance (pref: SettingPref): SettingViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: SettingViewModelFactory(pref)
            }.also { instance = it }
    }
}