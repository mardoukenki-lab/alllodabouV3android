package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DriverProfile
import com.example.data.model.DriverStatus
import com.example.data.model.Ride
import com.example.data.model.RideStatus
import com.example.data.model.ServiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
            status = DriverStatus.APPROVED,
            available = true,
            ratingAverage = 4.9,
            ratingCount = 88
        )
        assertEquals("Amadou Koné", profile.displayName)
        assertEquals(DriverStatus.APPROVED, profile.status)
        assertEquals(true, profile.available)
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
}
