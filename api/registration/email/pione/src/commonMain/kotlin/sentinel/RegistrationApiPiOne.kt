package sentinel

import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import keep.loadOrNull
import koncurrent.Later
import koncurrent.TODOLater
import koncurrent.later
import koncurrent.later.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import pione.PiOneApi
import pione.PiOneConstants
import pione.PiOneEndpoint
import pione.PiOneUnAuthorized
import pione.content
import sentinel.params.EmailSignUpParams
import sentinel.params.EmailVerificationParams
import sentinel.params.UserAccountParams

class RegistrationApiPiOne(override val config: RegistrationApiPiOneConfig<PiOneEndpoint>) : PiOneApi by PiOneApi(config), EmailRegistrationApi {

    override fun signUp(params: EmailSignUpParams): Later<EmailSignUpParams> = config.scope.later {
        val payload = codec.encodeToString(PiOneUnAuthorized(body = mapOf("email" to params.email, "name" to params.name)))
        val response = client.post(config.endpoint.signup) {
            setBody(payload)
        }

        val text = response.bodyAsText()
        val result = codec.decodeFromString(JsonObject.serializer(), text)

        if (result["status"]?.jsonPrimitive?.content == "ok") {
            params
        } else {
            throw RuntimeException(result["error"]?.jsonPrimitive?.content)
        }
    }

    override fun verify(params: EmailVerificationParams): Later<EmailVerificationParams> = config.scope.later {
        val payload = codec.encodeToString(PiOneUnAuthorized(body = mapOf("email" to params.email, "token" to params.token)))
        val response = client.post(config.endpoint.verifyEmail) {
            setBody(payload)
        }

        val text = response.bodyAsText()
        val result = codec.decodeFromString(JsonObject.serializer(), text)

        if (result["status"]?.jsonPrimitive?.content == "ok") {
            params
        } else {
            throw RuntimeException(result["error"]?.jsonPrimitive?.content)
        }
    }

    override fun sendVerificationLink(email: String): Later<String> = config.scope.later {
        val payload = codec.encodeToString(PiOneUnAuthorized(body = mapOf("email" to email, "url" to config.verificationUrl)))
        val response = client.post(config.endpoint.sendVerificationLink) {
            setBody(payload)
        }
        val text = response.bodyAsText()
        val result = codec.decodeFromString(JsonObject.serializer(), text)

        if (result["status"]?.jsonPrimitive?.content == "ok") {
            email
        } else {
            throw RuntimeException(result["error"]?.jsonPrimitive?.content)
        }
    }

    override fun createUserAccount(params: UserAccountParams): Later<UserAccountParams> = config.scope.later {
        val secret = config.cache.loadOrNull<String>(PiOneConstants.SECRET_CACHE_KEY).await()
        val payload = mapOf(
            "email" to params.loginId,
            "password" to params.password,
            "token" to params.registrationToken
        )
        val reqBody = if (secret == null) {
            config.codec.encodeToString(PiOneUnAuthorized(body = payload))
        } else {
            config.content(type = PiOneEndpoint.DataType.None, body = payload)
        }

        val response = config.http.post(config.endpoint.createAccount) {
            setBody(reqBody)
        }

        val text = response.bodyAsText()
        val result = config.codec.decodeFromString(JsonObject.serializer(), text)

        if (result["status"]?.jsonPrimitive?.content == "ok") {
            params
        } else {
            throw RuntimeException(result["error"]?.jsonPrimitive?.content)
        }
    }

    override fun abort(email: String): Later<String> = TODOLater("On can not abort a pione registration process yet")
}