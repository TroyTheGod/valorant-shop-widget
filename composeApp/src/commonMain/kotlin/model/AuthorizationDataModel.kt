package model

import kotlinx.serialization.Serializable

@Serializable
data class FakePostRequstModel(
    val acr_values: String,
    val claims: String? = null,
    val client_id: String,
    val code_challenge: String? = null,
    val code_challenge_method: String? = null,
    val nonce: String,
    val redirect_uri: String,
    val response_type: String,
    val scope: String,
    val region: String? = null,

    )

@Serializable
data class LoginRequestModel(
    val language: String,
    val password: String,
    val region: String? = null,
    val remember: String,
    val type: String,
    val username: String,
)
    

