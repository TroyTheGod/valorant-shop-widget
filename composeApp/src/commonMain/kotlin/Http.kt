import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.AuthTokenModel
import model.AuthTokenModelWrapper
import model.FakePostRequstModel
import model.LoginRequestModel

class Http {
    private val client = HttpClient() {
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    suspend fun getToken(username: String, password: String): AuthTokenModelWrapper? {
        val fakePostRequstModel = FakePostRequstModel(
            acr_values = "urn:riot:bronze",
            client_id = "play-valorant-web-prod",
            nonce = "1",
            redirect_uri = "https://playvalorant.com/opt_in",
            response_type = "token id_token",
            scope = "account openid",
        )
        val r = client.post("https://auth.riotgames.com/api/v1/authorization") {
            headers {
                append(
                    "user-agent",
                    "RiotClient/58.0.0.4640299.4552318 %s (Windows;10;;Professional, x64)"
                )
                append("Accept", "application/json, text/plain, */*")
                append("Accept-Language", "en-US,en;q=0.9")
            }
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(fakePostRequstModel))
        }
        val rString = r.body<String>()
        val json = Json.parseToJsonElement(rString)
        if (json.jsonObject["type"].toString() != "response") {
            val loginRequestModel = LoginRequestModel(
                language = "en_US",
                password = password,
                remember = "True",
                type = "auth",
                username = username,
            )
            val response = client.put("https://auth.riotgames.com/api/v1/authorization") {
                headers {
                    append(
                        "user-agent",
                        "RiotClient/58.0.0.4640299.4552318 %s (Windows;10;;Professional, x64)"
                    )
                    append("Accept", "application/json, text/plain, */*")
                    append("Accept-Language", "en-US,en;q=0.9")
                }
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToString(loginRequestModel))
            }
            val authString = response.body<String>()
            val authJson = Json.parseToJsonElement(authString)
            val bb = authJson.jsonObject.get("type")?.jsonPrimitive?.content
            print(bb)
            if (authJson.jsonObject["type"].toString() == "auth_failure") {
                return AuthTokenModelWrapper(
                    false, "auth_failure",
                )
            } else if (authJson.jsonObject.get("type")?.jsonPrimitive?.content != "response") {
                return AuthTokenModelWrapper(
                    false, "other",
                )
            }
            val pattern =
                Regex("access_token=((?:[a-zA-Z]|\\d|\\.|\\-|_)*).*id_token=((?:[a-zA-Z]|\\d|\\.|\\-|_)*).*expires_in=(\\d*)")
            val aa =
                authJson.jsonObject["response"]?.jsonObject?.get("parameters")?.jsonObject?.get("uri")
                    ?.jsonPrimitive?.content
            print(aa)
            val data =
                pattern.find(
                    authJson.jsonObject["response"]?.jsonObject?.get("parameters")?.jsonObject?.get(
                        "uri"
                    )
                        ?.jsonPrimitive?.content ?: ""
                )
            data?.let {
                val token = it.groupValues[1]
                val tokenId = it.groupValues[2]
                val expires = it.groupValues[3]

                return AuthTokenModelWrapper(
                    true, "other", AuthTokenModel(
                        accessToken = token,
                        idToken = tokenId,
                        expires = expires.toIntOrNull() ?: 0
                    )
                )
            }
        }
        return null
    }

    
}