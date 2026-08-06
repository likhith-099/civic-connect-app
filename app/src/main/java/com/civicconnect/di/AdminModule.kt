package com.civicconnect.di

import com.civicconnect.data.repository.AdminRepositoryImpl
import com.civicconnect.domain.repository.AdminRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AdminModule {

    @Binds
    @Singleton
    abstract fun bindAdminRepository(
        adminRepositoryImpl: AdminRepositoryImpl
    ): AdminRepository
}
