package com.siltech.cryptochat.data.modules

import ChatDBViewModel
import com.siltech.cryptochat.chat.ChatViewModel
import com.siltech.cryptochat.chat.callViewModel.CallViewModel
//import com.siltech.cryptochat.data.db.chats.ChatDBViewModel
import com.siltech.cryptochat.ui.aunt.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val smsViewModel = module {
    viewModel { ChatViewModel(get(), get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { ChatDBViewModel(get()) }
    viewModel { CallViewModel(get(),get()) }
}
