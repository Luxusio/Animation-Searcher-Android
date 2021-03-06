package io.luxus.animation.data.source.remote.model.discover

data class DiscoverAnimation(
    val id: Int,
    val name: String,
    val img: String,
    val cropped_img: String,
    val distributed_air_time: Any,
    val genres: List<String>,
    val home_cropped_img: String,
    val home_img: String,
    val is_adult: Boolean,
    val is_avod: Boolean,
    val is_dubbed: Boolean,
    val is_laftel_only: Boolean,
    val is_uncensored: Boolean,
    val is_viewing: Boolean,
    val latest_episode_created: String,
    val medium: String,
)