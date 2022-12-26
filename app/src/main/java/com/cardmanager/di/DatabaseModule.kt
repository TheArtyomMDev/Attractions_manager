package com.cardmanager.di

import com.cardmanager.data.AttractionRepository
import com.cardmanager.data.CardRepository
import com.cardmanager.data.EmployeeRepository
import com.cardmanager.data.TransactionRepository
import org.koin.dsl.module

var databaseModule = module {
    single {
        AttractionRepository(get())
    }
    single {
        CardRepository(get(), get(), get())
    }
    single {
        TransactionRepository(get())
    }
    single {
        EmployeeRepository(get(), get())
    }
}