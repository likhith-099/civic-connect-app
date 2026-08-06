package com.civicconnect.di

import com.civicconnect.data.repository.ComplaintRepositoryImpl
import com.civicconnect.domain.repository.ComplaintRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ComplaintModule {

    @Binds
    @Singleton
    abstract fun bindComplaintRepository(
        complaintRepositoryImpl: ComplaintRepositoryImpl
    ): ComplaintRepository
}
