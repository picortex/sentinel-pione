package sentinel

import io.ktor.client.*
import keep.CacheMock
import koncurrent.CoroutineExecutor
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import lexi.ConsoleAppender
import lexi.Logger
import picortex.PiCortexApiPiOne
import picortex.PiCortexApiPiOneConfig
import pione.PiOneEndpoint

fun AuthenticationApiProviderPiOneTest() : PiCortexApiPiOne {
    val url = PiOneEndpoint.DEFAULT_STAGING_ENDPOINT
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineName("Sentinel Test Dispatcher"))
    val executor = CoroutineExecutor(scope)
    return PiCortexApiPiOne(PiCortexApiPiOneConfig(
        appId = "<test>",
        cache = CacheMock(),
        endpoint = PiOneEndpoint(url),
        executor = executor,
        logger = Logger(ConsoleAppender()),
        http = HttpClient() {},
        codec = Json {
            isLenient = true
            ignoreUnknownKeys = true
        },
        type = PiOneEndpoint.DataType.None,
        serializer = Unit.serializer(),
        toPiOne = { _, _ -> null },
        fromPiOne = { null },
        verificationUrl = "$url/verify",
        passwordResetUrl = "$url/reset-password",
    ))
}