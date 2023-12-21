package model

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokenModel(
    val accessToken: String,
    val idToken: String,
    val expires: Int,
)

data class AuthTokenModelWrapper(
    val success: Boolean,
    val reason: String?,
    val authTokenModel: AuthTokenModel? = null,
)