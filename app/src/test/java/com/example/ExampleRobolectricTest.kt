package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DriverProfile
import com.example.data.model.DriverStatus
import com.example.data.model.PlateGenerator
import com.example.data.model.Ride
import com.example.data.model.RideStatus
import com.example.data.model.ServiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Allô Dabou Chauffeur", appName)
    }

    @Test
    fun `test driver profile creation`() {
        val profile = DriverProfile(
            uid = "driver_1",
            displayName = "Amadou Koné",
            phone = "+225 07 48 92 11 05",
            plate = "7421-HJ-01",
            driverBadgeNumber = "DABOU-1001",
            status = DriverStatus.APPROVED,
            available = true,
            ratingAverage = 4.9,
            ratingCount = 88
        )
        assertEquals("Amadou Koné", profile.displayName)
        assertEquals(DriverStatus.APPROVED, profile.status)
        assertEquals(true, profile.available)
        assertEquals("DABOU-1001", profile.driverBadgeNumber)
    }

    @Test
    fun `test plate generator format`() {
        val vehiclePlate = PlateGenerator.generateVehiclePlate()
        assertTrue(vehiclePlate.matches(Regex("\\d{4}-[A-Z]{2}-01")))

        val driverBadge = PlateGenerator.generateDriverBadge()
        assertTrue(driverBadge.startsWith("DABOU-"))

        val ciPlate = PlateGenerator.generateDriverPlateCI()
        assertTrue(ciPlate.startsWith("CI-DAB-"))
    }

    @Test
    fun `test optional plate driver profile`() {
        // Driver registering with license or without license
        val profileWithoutPlate = DriverProfile(
            uid = "driver_2",
            displayName = "Kouassi Jean",
            phone = "+225 05 12 34 56 78",
            plate = "",
            driverBadgeNumber = "",
            hasLicense = false,
            licenseNumber = "SANS PERMIS",
            status = DriverStatus.APPROVED,
            available = true,
            ratingAverage = 5.0,
            ratingCount = 0
        )
        assertTrue(profileWithoutPlate.plate.isEmpty())
        assertTrue(profileWithoutPlate.driverBadgeNumber.isEmpty())
        assertEquals(false, profileWithoutPlate.hasLicense)
        assertEquals("SANS PERMIS", profileWithoutPlate.licenseNumber)
        assertEquals(DriverStatus.APPROVED, profileWithoutPlate.status)
    }

    @Test
    fun `test ride data integrity`() {
        val ride = Ride(
            id = "ride_101",
            service = ServiceType.TAXI,
            status = RideStatus.PENDING,
            pickupAddress = "Marché Central Dabou",
            pickupLat = 5.3280,
            pickupLng = -4.3795,
            destinationAddress = "Hôpital Général Dabou",
            destinationLat = 5.3210,
            destinationLng = -4.3712,
            distanceKm = 3.2,
            durationMin = 8,
            priceFcfa = 1000,
            clientFirstName = "Awa K."
        )
        assertEquals(1000, ride.priceFcfa)
        assertEquals(RideStatus.PENDING, ride.status)
        assertNotNull(ride.pickupLat)
    }

    @Test
    fun `test ride reception strings and details`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val title = context.getString(R.string.ride_reception_title)
        val accept = context.getString(R.string.btn_accept_ride)
        val refuse = context.getString(R.string.btn_refuse_ride)
        val priceLabel = context.getString(R.string.estimated_price_label)

        assertEquals("Réception de course", title)
        assertEquals("Accepter", accept)
        assertEquals("Refuser", refuse)
        assertEquals("Prix estimé", priceLabel)

        // Incoming ride request with required fields: depart, destination, estimated price
        val incomingOffer = Ride(
            id = "offer_dabou_202",
            service = ServiceType.TAXI,
            status = RideStatus.PENDING,
            pickupAddress = "Gare Routière UTB Dabou",
            pickupLat = 5.3265,
            pickupLng = -4.3792,
            destinationAddress = "Mairie de Dabou",
            destinationLat = 5.3195,
            destinationLng = -4.3725,
            distanceKm = 3.4,
            durationMin = 9,
            priceFcfa = 1200,
            clientFirstName = "Mamadou T."
        )

        assertEquals("Gare Routière UTB Dabou", incomingOffer.pickupAddress)
        assertEquals("Mairie de Dabou", incomingOffer.destinationAddress)
        assertEquals(1200, incomingOffer.priceFcfa)
        assertTrue(incomingOffer.priceFcfa > 0)
    }
}
