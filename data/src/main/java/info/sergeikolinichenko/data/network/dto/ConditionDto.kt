package info.sergeikolinichenko.data.network.dto

import com.google.gson.annotations.SerializedName

/** Created by Sergei Kolinichenko on 21.02.2024 at 19:59 (GMT+3) **/

data class ConditionDto(
    @SerializedName("text")
    val text: String,

    @SerializedName("icon")
    val icon: String,
)
