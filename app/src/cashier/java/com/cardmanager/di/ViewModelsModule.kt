package com.cardmanager.di

import com.cardmanager.viewmodels.HistoryViewModel
import com.cardmanager.viewmodels.HomeViewModel
import com.cardmanager.viewmodels.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

var viewModelsModule = module {
    
    // Activities
    viewModel {
        HomeViewModel(get(), get(), get())
    }

    viewModel {
        LoginViewModel(get(), get())
    }

    viewModel {
        HistoryViewModel(get(), get(), get())
    }

}