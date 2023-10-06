package sentinel

import keep.load
import epsilon.Blob
import identifier.IndividualDto
import identifier.PersonalProfileApi
import identifier.params.IndividualProfileParams
import identifier.params.PasswordParams
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import koncurrent.Later
import koncurrent.later
import koncurrent.later.await
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import pione.ApiConfigRestKtor
import pione.PiOneConstants
import pione.PiOneEndpoint
import pione.PiOneResponseException
import pione.content
import pione.response.PiOneFailureResponse
import pione.response.PiOneSingleDataSuccessResponse

class PersonalProfileApiPiOneKtor(
    private val config: ApiConfigRestKtor<PiOneEndpoint>
) : PersonalProfileApi {
    private val client get() = config.http
    private val path get() = config.endpoint
    private val codec get() = config.codec
    private val cache get() = config.cache

    override fun changeProfilePicture(file: Blob) = config.scope.later {
        val secret = cache.load(PiOneConstants.SECRET_CACHE_KEY, String.serializer())
        val bytes = file.readBytes().await()

        client.post(path().updateProfilePicture) {
            header("secret", secret.await())
            setBody(MultiPartFormDataContent(formData {
                append("pic", bytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=pic")
                })
            }))
        }.parseIndividual()
    }

    override fun changePassword(params: PasswordParams) = config.scope.later {
        client.post(path().changePassword) {
            setBody(config.content(PiOneEndpoint.DataType.None, params))
        }.parseIndividual()
    }

    override fun update(params: IndividualProfileParams) = config.scope.later {
        client.post(path().updateIndividualProfile) {
            setBody(config.content(PiOneEndpoint.DataType.None, params))
        }.parseIndividual()
    }

    override fun addEmail(email: String): Later<IndividualDto> {
        TODO("Not yet implemented")
    }

    override fun beginEmailVerificationProcess(email: String): Later<String> {
        TODO("Not yet implemented")
    }

    override fun completeEmailVerificationProcess(token: String): Later<String> {
        TODO("Not yet implemented")
    }

    override fun deleteEmail(email: String): Later<IndividualDto> {
        TODO("Not yet implemented")
    }

    override fun beginPhoneVerificationProcess(phone: String): Later<String> {
        TODO("Not yet implemented")
    }

    override fun completePhoneVerificationProcess(phone: String): Later<String> {
        TODO("Not yet implemented")
    }

    override fun addPhone(phone: String): Later<IndividualDto> {
        TODO("Not yet implemented")
    }

    override fun deletePhone(phone: String): Later<IndividualDto> {
        TODO("Not yet implemented")
    }

    suspend fun HttpResponse.parseIndividual(): IndividualDto {
        val text = bodyAsText()
        return if (codec.decodeFromString<JsonObject>(text).isSuccess) {
            codec.decodeFromString(PiOneSingleDataSuccessResponse.serializer(IndividualDto.serializer()), text).obj
        } else {
            throw parseError(text)
        }
    }

    private fun parseError(text: String): PiOneResponseException = try {
        val cause = codec.decodeFromString(PiOneFailureResponse.serializer(), text)
        PiOneResponseException(cause.error)
    } catch (err:Exception) {
        PiOneResponseException(text)
    }

    private val JsonObject.isSuccess get() = get("status")?.jsonPrimitive?.content == "ok"

    suspend fun path(): PiOneEndpoint {
        val companyUrl = config.cache.load<String>(PiOneConstants.CUSTOMER_DOMAIN_KEY).await()
        return config.endpoint.copy(root = companyUrl)
    }
}