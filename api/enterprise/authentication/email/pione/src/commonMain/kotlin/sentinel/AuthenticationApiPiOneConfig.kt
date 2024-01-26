package sentinel

import pione.ApiConfigRestKtor

interface AuthenticationApiPiOneConfig<out E> : ApiConfigRestKtor<E> {
    val passwordResetUrl: String
}