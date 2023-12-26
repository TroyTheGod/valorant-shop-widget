import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.addCookie
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.date.GMTDate
import kotlinx.datetime.Clock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import model.AuthTokenModel
import model.AuthTokenModelWrapper
import model.FakePostRequstModel
import model.LoginRequestModel
import store.TokenStore

class Http {
    private var client = HttpClient() {
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
            if (authJson.jsonObject["type"].toString() == "auth_failure") {
                return AuthTokenModelWrapper(
                    false, "auth_failure",
                )
            } else if (authJson.jsonObject.get("type")?.jsonPrimitive?.content != "response") {
                return AuthTokenModelWrapper(
                    false, "other",
                )
            }
            val params = extractParams(
                authJson.jsonObject["response"]?.jsonObject?.get("parameters")?.jsonObject?.get("uri")?.jsonPrimitive?.content
                    ?: ""
            )

            if (params.isNotEmpty()) {
                val token = params["access_token"]
                val tokenId = params["id_token"]
                val expires = params["expires_in"]
                storeCookie()
                cookieReAuth()
                return AuthTokenModelWrapper(
                    true, "other", AuthTokenModel(
                        accessToken = token ?: "",
                        idToken = tokenId ?: "",
                        expires = expires?.toIntOrNull() ?: 0
                    )
                )
            }
        }
        return null
    }

    suspend fun cookieReAuth() {
        val response =
            client.get("https://auth.riotgames.com/authorize?redirect_uri=https%3A%2F%2Fplayvalorant.com%2Fopt_in&client_id=play-valorant-web-prod&response_type=token%20id_token&nonce=1&scope=account%20openid")
        val bb = response.call.
        val aa = response.headers["Location"]
        print(bb)
    }

    suspend fun getPlayerUuid(authTokenModel: AuthTokenModel): String? {
        val result = client.get("https://auth.riotgames.com/userinfo") {
            headers {
                append("Authorization", "Bearer ${authTokenModel.accessToken}")
            }
        }
        if (result.status != HttpStatusCode.OK) {
            val aa = result.body<String>()
            return null
        }
        val playerInfoString = result.body<String>()
        val authJson = Json.parseToJsonElement(playerInfoString)
        return authJson.jsonObject.get("sub")?.jsonPrimitive?.content;
    }


    suspend fun getEntitlementToken(authTokenModel: AuthTokenModel): String? {
        val result = client.post("https://entitlements.auth.riotgames.com/api/token/v1") {
            headers {
                append("Content-Type", "application/json")
                append("Authorization", "Bearer ${authTokenModel.accessToken}")
            }
        }
        val tokenString = result.body<String>()
        val tokenJson = Json.parseToJsonElement(tokenString)
        return tokenJson.jsonObject.get("entitlements_token")?.jsonPrimitive?.content
    }

    suspend fun getStoreFront(
        authTokenModel: AuthTokenModel, uuid: String, entitlementToken: String
    ): ArrayList<String>? {
        val region = "ap";
        val result = client.get("https://pd.$region.a.pvp.net/store/v2/storefront/$uuid") {
            headers {
                append("X-Riot-Entitlements-JWT", entitlementToken)
                append("Authorization", "Bearer ${authTokenModel.accessToken}")
            }
        }
        val storeString = result.body<String>()
        val storeJson = Json.parseToJsonElement(storeString)
        var weaponsList: ArrayList<String> = arrayListOf()
        storeJson.jsonObject.get("SkinsPanelLayout")?.jsonObject?.get("SingleItemOffers")?.jsonArray?.forEach {
            weaponsList.add(it.jsonPrimitive.content)
        }
        print(weaponsList)
        return weaponsList
    }

    private suspend fun storeCookie() {
        val cookies = client.cookies("https://auth.riotgames.com/")
        val jsonString = convertListToJson(cookies)
        TokenStore().setCookie(jsonString)
    }

    private suspend fun restoreCookie(): Boolean {
        val jsonString = TokenStore().getCookie()
        if (jsonString != null) {
            val customStorage = AcceptAllCookiesStorage()
            val cookies = convertJsonToCookie(jsonString)
            cookies.forEach {
                customStorage.addCookie("https://auth.riotgames.com/", it)
            }
            client = HttpClient() {
                install(HttpCookies) {
                    storage = customStorage
                }
            }
        }
        return jsonString != null
    }

    private fun convertListToJson(dataList: List<Cookie>): String {
        return buildString {
            append("[")
            dataList.joinTo(this, separator = ",") { data ->
                "{\"name\": \"${data.name}\"," +
                        "\"path\": \"${data.path}\"," +
                        " \"value\": \"${data.value}\"}"

            }
            append("]")
        }
    }

    fun convertJsonToCookie(json: String): List<Cookie> {
        val jsonElement = Json.parseToJsonElement(json)
        val cookieList = jsonElement.jsonArray.map {
            Cookie(
                domain = "riotgames.com",
                encoding = CookieEncoding.RAW,
                expires = GMTDate(Clock.System.now().toEpochMilliseconds() + 31556926000),
                httpOnly = true,
                maxAge = 31536000,
                name = it.jsonObject["name"]!!.jsonPrimitive.content,
                path = it.jsonObject["path"]!!.jsonPrimitive.content,
                secure = true,
                value = it.jsonObject["value"]!!.jsonPrimitive.content,
            )
        }
        return cookieList
    }

    fun extractParams(url: String): Map<String, String> {
        // Splitting the URL to get the fragment part (after '#')
        val fragment = url.substringAfter("#", "")

        // Splitting the fragment into parameters (split by '&')
        val params = fragment.split("&")

        // Creating a map to hold the key-value pairs
        val paramMap = mutableMapOf<String, String>()

        // Iterating over the parameters to split them into key and value
        for (param in params) {
            val (key, value) = param.split("=")
            paramMap[key] = value
        }
        return paramMap
    }
}