package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ClientContact
import com.example.data.model.DriverProfile
import com.example.data.model.DriverStatus
import com.example.data.model.Ride
import com.example.data.model.RideStatus
import com.example.data.model.ServiceType

@Entity(tableName = "rides")
data class RideEntity(
    @PrimaryKey val id: String,
    val service: String,
    val status: String,
    val pickupAddress: String,
    val pickupLat: Double?,
    val pickupLng: Double?,
    val destinationAddress: String,
    val destinationLat: Double?,
    val destinationLng: Double?,
    val distanceKm: Double,
    val durationMin: Int,
    val priceFcfa: Int,
    val packageDetails: String,
    val conciergeTask: String,
    val notes: String,
    val clientFirstName: String,
    val createdAtMillis: Long,
    val acceptedAtMillis: Long?,
    val driverId: String?,
    val clientPhone: String,
    val recipientPhone: String?
) {
    fun toDomain(): Ride {
        return Ride(
            id = id,
            service = runCatching { ServiceType.valueOf(service) }.getOrDefault(ServiceType.TAXI),
            status = runCatching { RideStatus.valueOf(status) }.getOrDefault(RideStatus.PENDING),
            pickupAddress = pickupAddress,
            pickupLat = pickupLat,
            pickupLng = pickupLng,
            destinationAddress = destinationAddress,
            destinationLat = destinationLat,
            destinationLng = destinationLng,
            distanceKm = distanceKm,
            durationMin = durationMin,
            priceFcfa = priceFcfa,
            packageDetails = packageDetails,
            conciergeTask = conciergeTask,
            notes = notes,
            clientFirstName = clientFirstName,
            createdAtMillis = createdAtMillis,
            acceptedAtMillis = acceptedAtMillis,
            driverId = driverId,
            contact = if (clientPhone.isNotEmpty()) ClientContact(clientPhone, recipientPhone) else null
        )
    }

    companion object {
        fun fromDomain(ride: Ride): RideEntity {
            return RideEntity(
                id = ride.id,
                service = ride.service.name,
                status = ride.status.name,
                pickupAddress = ride.pickupAddress,
                pickupLat = ride.pickupLat,
                pickupLng = ride.pickupLng,
                destinationAddress = ride.destinationAddress,
                destinationLat = ride.destinationLat,
                destinationLng = ride.destinationLng,
                distanceKm = ride.distanceKm,
                durationMin = ride.durationMin,
                priceFcfa = ride.priceFcfa,
                packageDetails = ride.packageDetails,
                conciergeTask = ride.conciergeTask,
                notes = ride.notes,
                clientFirstName = ride.clientFirstName,
                createdAtMillis = ride.createdAtMillis,
                acceptedAtMillis = ride.acceptedAtMillis,
                driverId = ride.driverId,
                clientPhone = ride.contact?.phone ?: "",
                recipientPhone = ride.contact?.recipientPhone
            )
        }
    }
}

@Entity(tableName = "driver_profile")
data class DriverProfileEntity(
    @PrimaryKey val uid: String,
    val displayName: String,
    val email: String = "",
    val phone: String,
    val plate: String,
    val status: String,
    val available: Boolean,
    val ratingAverage: Double,
    val ratingCount: Int
) {
    fun toDomain(): DriverProfile {
        return DriverProfile(
            uid = uid,
            displayName = displayName,
            email = email,
            phone = phone,
            plate = plate,
            status = runCatching { DriverStatus.valueOf(status) }.getOrDefault(DriverStatus.APPROVED),
            available = available,
            ratingAverage = ratingAverage,
            ratingCount = ratingCount
        )
    }

    companion object {
        fun fromDomain(profile: DriverProfile): DriverProfileEntity {
            return DriverProfileEntity(
                uid = profile.uid,
                displayName = profile.displayName,
                email = profile.email,
                phone = profile.phone,
                plate = profile.plate,
                status = profile.status.name,
                available = profile.available,
                ratingAverage = profile.ratingAverage,
                ratingCount = profile.ratingCount
            )
        }
    }
}
