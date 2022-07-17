package com.siltech.cryptochat.ui.aunt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.siltech.cryptochat.repo.Repository

class MyViewModelFactory constructor(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            LoginViewModel(this.repository) as T
        } else {
            throw IllegalArgumentException("ViewModel не найден")
        }
    }
}
