package com.siltech.cryptochat.data.modules

//import com.siltech.cryptochat.data.db.chats.ChatsDB
//import com.siltech.cryptochat.data.db.chats.ChatDBViewModel
import com.siltech.cryptochat.data.db.messages.s.SMSDATABASE
import com.siltech.cryptochat.repo.DBRepo
//import com.siltech.cryptochat.repo.DBRepo
import com.siltech.cryptochat.repo.Repository
import com.siltech.cryptochat.repo.SmsRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dbModule = module {
    single { SMSDATABASE }
}

val repositoryModule = module {
    single { SmsRepository(get())}
    single { Repository(get())}
    single { DBRepo(get()) }

}

val dbModuleChat = module {
//    viewModel { ChatDBViewModel(get()) }
//    single { DBRepo(get(), get())}

}
