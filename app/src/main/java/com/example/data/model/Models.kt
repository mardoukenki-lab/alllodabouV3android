package com.example.data.model

enum class RideStatus {
    PENDING,
    CONFIRMED,
    COMPLETED,
    CANCELLED
}

enum class DriverStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUSPENDED
}

enum class ServiceType(val label: String) {
    TAXI("Taxi Allô Dabou"),
    DELIVERY("Livraison Colis"),
    CONCIERGE("Conciergerie")
}

data class DriverProfile(
    val uid: String,
    val displayName: String,
    val email: String = "",
    val phone: String,
    val plate: String,
    val driverBadgeNumber: String = "",
    val status: DriverStatus,
    val available: Boolean,
    val ratingAverage: Double,
    val ratingCount: Int
)

data class ClientContact(
    val phone: String,
    val recipientPhone: String? = null
)

data class Ride(
    val id: String,
    val service: ServiceType,
    val status: RideStatus,
    val pickupAddress: String,
    val pickupLat: Double?,
    val pickupLng: Double?,
    val destinationAddress: String,
    val destinationLat: Double?,
    val destinationLng: Double?,
    val distanceKm: Double,
    val durationMin: Int,
    val priceFcfa: Int,
    val packageDetails: String = "",
    val conciergeTask: String = "",
    val notes: String = "",
    val clientFirstName: String,
    val createdAtMillis: Long = System.currentTimeMillis(),
    val acceptedAtMillis: Long? = null,
    val driverId: String? = null,
    val contact: ClientContact? = null
)

sealed interface DriverGate {
    data object Loading : DriverGate
    data object SignedOut : DriverGate
    data class WrongRole(val role: String?) : DriverGate
    data class Blocked(val status: DriverStatus) : DriverGate // pending, rejected, suspended
    data class Ready(val profile: DriverProfile) : DriverGate
}
