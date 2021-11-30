package com.kenrube.mosaic.di

import com.kenrube.mosaic.data.resource.ResourceShaderRepository
import com.kenrube.mosaic.data.resource.ShaderRepository
import com.kenrube.mosaic.utils.coroutine.CoroutineDispatchersProvider
import com.kenrube.mosaic.utils.coroutine.DispatchersProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SingletonModule {

    @Binds
    fun bindDispatchersProvider(dispatchersProvider: CoroutineDispatchersProvider): DispatchersProvider

    @Binds
    fun bindShaderRepository(repository: ResourceShaderRepository): ShaderRepository
}