package sentinel

import identifier.OrganisationProfileApi
import identifier.PersonalProfileApi
import pione.ApiConfigRestKtor
import pione.PiOneEndpoint

class ProfileApiPiOne(
    private val config: ApiConfigRestKtor<PiOneEndpoint>
) : ProfileApi  {
    override val personal: PersonalProfileApi by lazy { PersonalProfileApiPiOneKtor(config) }

    override val organisation: OrganisationProfileApi by lazy { OrganisationProfileApiPiOneKtor(config) }
}