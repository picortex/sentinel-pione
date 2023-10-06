package sentinel

import epsilon.Blob
import identifier.CorporateDto
import identifier.OrganisationProfileApi
import identifier.params.CorporateParams
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kash.Currency
import keep.load
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

class OrganisationProfileApiPiOneKtor (
    private val config: ApiConfigRestKtor<PiOneEndpoint>
) : OrganisationProfileApi {
    private val client get() = config.http
    private val path get() = config.endpoint
    private val codec get() = config.codec
    private val cache get() = config.cache

    override fun update(params: CorporateParams) = config.scope.later {
        client.post(path().updateOrganisationProfile) {
            setBody(config.content(PiOneEndpoint.DataType.None, params))
        }.parseCorporate()
    }

    override fun updateLogo(logo: Blob) = config.scope.later {
        val secret = cache.load(PiOneConstants.SECRET_CACHE_KEY, String.serializer())
        val bytes = logo.readBytes().await()

        client.post(path().updateLogo) {
            header("secret", secret.await())
            setBody(MultiPartFormDataContent(formData {
                append("logo", bytes, Headers.build {
                    append(HttpHeaders.ContentDisposition, "filename=logo")
                })
            }))
        }.parseCorporate()
    }

    override fun updateCurrency(currency: Currency) = config.scope.later {
        client.post(path().updateCurrency) {
            setBody(config.content(PiOneEndpoint.DataType.None, mapOf(
                "currency" to currency.name
            )))
        }.parseCorporate()
        currency
    }

    override fun updateTimezone(tz: String) = config.scope.later {
        client.post(path().updateTimeZone) {
            setBody(config.content(PiOneEndpoint.DataType.None, mapOf(
                "timeZone" to tz
            )))
        }.parseCorporate()
        tz
    }

    override fun updateSalesTax(percentage: Int) = config.scope.later {
        client.post(path().updateSalesTax) {
            setBody(config.content(PiOneEndpoint.DataType.None, mapOf(
                "salesTax" to percentage
            )))
        }.parseCorporate()
        percentage
    }

    suspend fun HttpResponse.parseCorporate(): CorporateDto {
        val text = bodyAsText()
        return if (codec.decodeFromString<JsonObject>(text).isSuccess) {
            codec.decodeFromString(PiOneSingleDataSuccessResponse.serializer(CorporateDto.serializer()), text).obj
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