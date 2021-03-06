package io.luxus.animation.di.module

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.luxus.animation.data.repository.DiscoverRepositoryImpl
import io.luxus.animation.domain.repository.DiscoverRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDiscoverRepository(discoverRepository: DiscoverRepositoryImpl): DiscoverRepository


}