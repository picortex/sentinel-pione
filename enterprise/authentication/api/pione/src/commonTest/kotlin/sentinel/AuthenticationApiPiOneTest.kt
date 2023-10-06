package sentinel

import koncurrent.later.await
import kotlinx.coroutines.test.runTest
import sentinel.params.PasswordResetParams
import kotlin.test.Test


class AuthenticationApiPiOneTest {
    val api = AuthenticationApiProviderPiOneTest()

    @Test
    fun should_send_password_reset_email() = runTest {
        api.sendPasswordResetLink("george.sechu@gmail.com").await()
    }

    @Test
    fun should_reset_password() = runTest {
        api.resetPassword(PasswordResetParams(
            password = "newpassword123",
            passwordResetToken = "528e2c83-8b89-45b8-9a63-5e5525a0d47d")
        ).await()
    }
}