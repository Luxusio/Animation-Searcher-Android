package io.luxus.animation.domain.usecase

import io.luxus.animation.data.source.remote.model.discover.DiscoverResult
import io.luxus.animation.domain.repository.DiscoverRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimationUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository
) {

    fun getDiscover(sortType: String, size: Int, offset: Int): DiscoverResult =
        discoverRepository.getAnimationListResult(sortType, size, offset)

}