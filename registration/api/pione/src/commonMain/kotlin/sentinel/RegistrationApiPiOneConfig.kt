package sentinel

import pione.ApiConfigRestKtor

interface RegistrationApiPiOneConfig<E> : ApiConfigRestKtor<E> {
    val verificationUrl: String
}