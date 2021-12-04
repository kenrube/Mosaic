package com.kenrube.mosaic.di

import com.kenrube.mosaic.data.db.GalleryPhotoRepository
import com.kenrube.mosaic.data.db.PhotoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface ActivityRetainedModule {

    @Binds
    fun bindPhotoRepository(repository: GalleryPhotoRepository): PhotoRepository
}