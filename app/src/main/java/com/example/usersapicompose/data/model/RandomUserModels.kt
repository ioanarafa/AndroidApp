package com.example.usersapicompose.data.model

import com.google.gson.annotations.SerializedName

data class RandomUserResponse(
    @SerializedName("results") val results: List<UserDto>
)

data class UserDto(
    @SerializedName("gender") val gender: String?,
    @SerializedName("name") val name: NameDto?,
    @SerializedName("location") val location: LocationDto?,
    @SerializedName("email") val email: String?,
    @SerializedName("picture") val picture: PictureDto?
) {

    fun fullName(): String {
        val first = name?.first.orEmpty()
        val last = name?.last.orEmpty()
        return (first + " " + last).trim()
    }

    fun fullLocation(): String {
        val city = location?.city.orEmpty()
        val country = location?.country.orEmpty()
        return listOf(city, country).filter { it.isNotBlank() }.joinToString(", ")
    }

    fun pictureUrl(): String {
        return picture?.large ?: picture?.medium ?: picture?.thumbnail ?: ""
    }
}

data class NameDto(
    @SerializedName("title") val title: String?,
    @SerializedName("first") val first: String?,
    @SerializedName("last") val last: String?
)

data class LocationDto(
    @SerializedName("city") val city: String?,
    @SerializedName("country") val country: String?
)

data class PictureDto(
    @SerializedName("large") val large: String?,
    @SerializedName("medium") val medium: String?,
    @SerializedName("thumbnail") val thumbnail: String?
)
