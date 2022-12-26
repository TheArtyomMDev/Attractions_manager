package com.cardmanager.di

import com.cardmanager.AttractionsReportsFragment
import com.cardmanager.viewmodels.*
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

    viewModel {
        EmployeesViewModel(get())
    }

    viewModel {
        AttractionsViewModel(get())
    }

    viewModel {
        OperatorsReportsViewModel(get(), get(), get())
    }

    viewModel {
        AttractionsReportsViewModel(get(), get())
    }

}