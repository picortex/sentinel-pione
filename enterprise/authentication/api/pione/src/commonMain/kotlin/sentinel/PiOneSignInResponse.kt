package sentinel

import kotlinx.serialization.Serializable

@Serializable
data class PiOneSignInResponse(
    val chatChannel: ChatChannel,
    val hostDetails: HostDetails,
    val secret: String,
    val status: String,
    val user: User
)

@Serializable
data class ChatChannel(
    val attachmentType: String,
    val messageType: String,
    val threadType: String
)

@Serializable
data class HostDetails(
    val companyUrl: String,
    val namespace: String?=null,
    val icon: String?=null,
    val name: String?=null,
)

@Serializable
data class User(
    val authorised: Boolean,
    val checkedIn: Boolean,
    val clientId: Int,
    val creationTimestamp: Long,
    val databaseResultMapKey: Int,
    val dateAdded: DateAdded,
    val dateModified: DateModified,
    val deleted: Boolean,
    val email: String,
    val formattedDate: String,
    val id: Int,
    val initials: String,
    val password: String,
    val passwordChanged: Boolean,
    val providesServiceLogins: Boolean,
    val salt: String,
    val secret: String,
    val staff: Int,
    val status: String,
    val stringValue: String,
    val tempPassword: String,
    val timeZone: String,
    val timestamp: Long,
    val type: String,
    val userDepartment: String,
    val uuid: String,
    val version: Int,
    val accessCode: String?=null,
    val name: String?=null,
)

@Serializable
data class DateAdded(
    val date: Int,
    val day: Int,
    val hours: Int,
    val minutes: Int,
    val month: Int,
    val nanos: Int,
    val seconds: Int,
    val time: Long,
    val timezoneOffset: Int,
    val year: Int
)

@Serializable
data class DateModified(
    val date: Int,
    val day: Int,
    val hours: Int,
    val minutes: Int,
    val month: Int,
    val nanos: Int,
    val seconds: Int,
    val time: Long,
    val timezoneOffset: Int,
    val year: Int
)