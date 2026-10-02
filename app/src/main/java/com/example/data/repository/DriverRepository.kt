package com.example.data.repository

import android.content.Context
import com.example.alerts.AlertsManager
import com.example.data.local.AppDatabase
import com.example.data.local.DriverProfileEntity
import com.example.data.local.RideEntity
import com.example.data.model.ClientContact
import com.example.data.model.DriverGate
import com.example.data.model.DriverProfile
import com.example.data.model.DriverStatus
import com.example.data.model.PlateGenerator
import com.example.data.model.Ride
import com.example.data.model.RideStatus
import com.example.data.model.ServiceType
import com.example.data.remote.FirebaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class DriverRepository(
    private val context: Context,
    private val database: AppDatabase,
    private val alertsManager: AlertsManager,
    private val scope: CoroutineScope
) {
    private val rideDao = database.rideDao()
    private val driverProfileDao = database.driverProfileDao()
    val firebaseService = FirebaseService(context)

    // Offline / Cache indicator state
    private val _isOfflineCacheMode = MutableStateFlow(false)
    val isOfflineCacheMode: StateFlow<Boolean> = _isOfflineCacheMode.asStateFlow()

    init {
        // Sync real Firestore pending rides if reachable
        scope.launch(Dispatchers.IO) {
            runCatching {
                firebaseService.observeFirestorePendingRides().collect { remoteRides ->
                    if (remoteRides.isNotEmpty()) {
                        rideDao.insertRides(remoteRides.map { RideEntity.fromDomain(it) })
                    }
                }
            }
        }
    }

    val driverGateFlow: Flow<DriverGate> = driverProfileDao.getProfileFlow().map { profileEntity ->
        if (profileEntity == null) {
            DriverGate.SignedOut
        } else {
            val domain = profileEntity.toDomain()
            when (domain.status) {
                DriverStatus.APPROVED -> DriverGate.Ready(domain)
                DriverStatus.PENDING -> DriverGate.Blocked(DriverStatus.PENDING)
                DriverStatus.REJECTED -> DriverGate.Blocked(DriverStatus.REJECTED)
                DriverStatus.SUSPENDED -> DriverGate.Blocked(DriverStatus.SUSPENDED)
            }
        }
    }

    val currentProfileFlow: Flow<DriverProfile?> = driverProfileDao.getProfileFlow().map { it?.toDomain() }

    val availableRidesFlow: Flow<List<Ride>> = rideDao.getPendingRides().map { list ->
        list.map { it.toDomain() }
    }

    fun getDriverRidesFlow(driverId: String): Flow<List<Ride>> {
        return rideDao.getDriverRides(driverId).map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getActiveRideFlow(driverId: String): Flow<Ride?> {
        return rideDao.getActiveRide(driverId).map { it?.toDomain() }
    }

    suspend fun registerDriverWithEmail(
        displayName: String,
        email: String,
        password: String,
        phone: String,
        plate: String = "",
        driverBadge: String = ""
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        val cleanPlate = plate.trim().uppercase()
        val cleanBadge = driverBadge.trim().uppercase()
        val cleanPhone = phone.trim()
        val cleanName = displayName.trim()

        if (cleanName.length < 2) {
            return@withContext Result.failure(IllegalArgumentException("Veuillez saisir votre nom complet."))
        }
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return@withContext Result.failure(IllegalArgumentException("Veuillez saisir une adresse email valide."))
        }
        if (cleanPassword.length < 6) {
            return@withContext Result.failure(IllegalArgumentException("Le mot de passe doit comporter au moins 6 caractères."))
        }
        if (cleanPhone.length < 8) {
            return@withContext Result.failure(IllegalArgumentException("Veuillez saisir un numéro de téléphone valide."))
        }
        val finalPlate = if (cleanPlate.isNotBlank()) cleanPlate else PlateGenerator.generateVehiclePlate()
        if (finalPlate.length !in 4..14) {
            return@withContext Result.failure(IllegalArgumentException("La plaque d'immatriculation doit comporter entre 4 et 14 caractères."))
        }

        // 1. Firebase Auth Sign Up
        val fbUserResult = firebaseService.signUpWithEmail(cleanEmail, cleanPassword)
        val uid = fbUserResult.getOrNull()?.uid ?: ("driver_" + UUID.randomUUID().toString().take(8))

        // 2. Profile Creation (Plaque véhicule obligatoire)
        val profile = DriverProfile(
            uid = uid,
            displayName = cleanName,
            email = cleanEmail,
            phone = cleanPhone,
            plate = finalPlate,
            driverBadgeNumber = cleanBadge,
            status = DriverStatus.APPROVED,
            available = true,
            ratingAverage = 5.0,
            ratingCount = 1
        )

        // 3. Save to local Room database
        driverProfileDao.insertProfile(DriverProfileEntity.fromDomain(profile))

        // 4. Save to Firestore in background
        firebaseService.saveDriverProfile(profile)

        Result.success(Unit)
    }

    suspend fun loginDriverWithEmail(
        email: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail.isBlank() || cleanPassword.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Email et mot de passe requis."))
        }

        val fbResult = firebaseService.signInWithEmail(cleanEmail, cleanPassword)
        val fbUser = fbResult.getOrNull()

        // Load profile from Firestore or local
        val uid = fbUser?.uid
        if (uid != null) {
            val remoteProfile = firebaseService.getDriverProfile(uid)
            if (remoteProfile != null) {
                driverProfileDao.insertProfile(DriverProfileEntity.fromDomain(remoteProfile))
                return@withContext Result.success(Unit)
            }
        }

        // Check local profile
        val localProfile = driverProfileDao.getProfile()
        if (localProfile != null) {
            driverProfileDao.updateAvailability(localProfile.uid, true)
            return@withContext Result.success(Unit)
        }

        // If newly logged in with Firebase but no driver document yet
        if (fbUser != null) {
            val fallbackProfile = DriverProfile(
                uid = fbUser.uid,
                displayName = fbUser.displayName ?: cleanEmail.substringBefore("@"),
                email = cleanEmail,
                phone = fbUser.phoneNumber ?: "",
                plate = "",
                status = DriverStatus.APPROVED,
                available = true,
                ratingAverage = 5.0,
                ratingCount = 0
            )
            driverProfileDao.insertProfile(DriverProfileEntity.fromDomain(fallbackProfile))
            firebaseService.saveDriverProfile(fallbackProfile)
            return@withContext Result.success(Unit)
        }

        if (fbResult.isFailure) {
            return@withContext Result.failure(fbResult.exceptionOrNull() ?: IllegalArgumentException("Échec de connexion"))
        }

        Result.success(Unit)
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) {
            return@withContext Result.failure(IllegalArgumentException("Veuillez saisir une adresse email valide."))
        }
        firebaseService.sendPasswordResetEmail(cleanEmail)
    }

    suspend fun loginOrRegisterWithGoogle(
        idToken: String,
        googleName: String?,
        googleEmail: String?,
        phone: String = "",
        plate: String = "",
        driverBadge: String = ""
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val fbResult = firebaseService.signInWithGoogle(idToken)
        val user = fbResult.getOrNull()
        val uid = user?.uid ?: ("google_" + UUID.randomUUID().toString().take(8))

        // Check remote profile
        val remote = firebaseService.getDriverProfile(uid)
        if (remote != null) {
            driverProfileDao.insertProfile(DriverProfileEntity.fromDomain(remote))
            return@withContext Result.success(Unit)
        }

        // Check local profile
        val local = driverProfileDao.getProfile()
        if (local != null && local.uid == uid) {
            driverProfileDao.updateAvailability(uid, true)
            return@withContext Result.success(Unit)
        }

        // Create new driver profile (Plaque véhicule obligatoire)
        val cleanPlate = plate.trim().uppercase()
        val finalPlate = if (cleanPlate.isNotBlank()) cleanPlate else PlateGenerator.generateVehiclePlate()
        val cleanBadge = driverBadge.trim().uppercase()
        val newProfile = DriverProfile(
            uid = uid,
            displayName = googleName ?: user?.displayName ?: "Chauffeur Allô Dabou",
            email = googleEmail ?: user?.email ?: "",
            phone = phone.ifBlank { user?.phoneNumber ?: "+225 07 00 00 00" },
            plate = finalPlate,
            driverBadgeNumber = cleanBadge,
            status = DriverStatus.APPROVED,
            available = true,
            ratingAverage = 5.0,
            ratingCount = 0
        )

        driverProfileDao.insertProfile(DriverProfileEntity.fromDomain(newProfile))
        firebaseService.saveDriverProfile(newProfile)

        Result.success(Unit)
    }

    suspend fun updateDriverPlates(plate: String, badge: String): Result<Unit> = withContext(Dispatchers.IO) {
        val profile = driverProfileDao.getProfile() ?: return@withContext Result.failure(IllegalStateException("Profil introuvable"))
        val finalPlate = plate.trim().uppercase()
        val finalBadge = badge.trim().uppercase()
        if (finalPlate.isNotBlank()) {
            driverProfileDao.updatePlate(profile.uid, finalPlate)
        }
        if (finalBadge.isNotBlank()) {
            driverProfileDao.updateDriverBadge(profile.uid, finalBadge)
        }
        firebaseService.updateDriverPlate(profile.uid, finalPlate, finalBadge)
        Result.success(Unit)
    }

    suspend fun setAvailability(available: Boolean) = withContext(Dispatchers.IO) {
        val profile = driverProfileDao.getProfile() ?: return@withContext
        driverProfileDao.updateAvailability(profile.uid, available)
        firebaseService.updateAvailability(profile.uid, available)
    }

    suspend fun updateDriverStatus(newStatus: DriverStatus) = withContext(Dispatchers.IO) {
        val profile = driverProfileDao.getProfile() ?: return@withContext
        driverProfileDao.updateStatus(profile.uid, newStatus.name)
    }

    suspend fun acceptRide(rideId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val profile = driverProfileDao.getProfile()?.toDomain()
            ?: return@withContext Result.failure(IllegalStateException("NON_CONNECTE"))

        if (profile.status != DriverStatus.APPROVED) {
            return@withContext Result.failure(IllegalStateException("NOT_APPROVED"))
        }

        // Check if driver is already busy with a confirmed ride
        val activeRide = rideDao.getActiveRide(profile.uid).firstOrNull()
        if (activeRide != null) {
            return@withContext Result.failure(IllegalStateException("DRIVER_BUSY"))
        }

        val rideEntity = rideDao.getRideById(rideId)
            ?: return@withContext Result.failure(IllegalStateException("RIDE_NOT_FOUND"))

        // Check idempotency: if already accepted by this driver, success
        if (rideEntity.status == RideStatus.CONFIRMED.name && rideEntity.driverId == profile.uid) {
            return@withContext Result.success(Unit)
        }

        if (rideEntity.status != RideStatus.PENDING.name) {
            return@withContext Result.failure(IllegalStateException("ALREADY_TAKEN"))
        }

        val updated = rideEntity.copy(
            status = RideStatus.CONFIRMED.name,
            driverId = profile.uid,
            acceptedAtMillis = System.currentTimeMillis()
        )
        rideDao.updateRide(updated)
        Result.success(Unit)
    }

    suspend fun completeRide(rideId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val rideEntity = rideDao.getRideById(rideId)
            ?: return@withContext Result.failure(IllegalStateException("RIDE_NOT_FOUND"))

        val updated = rideEntity.copy(
            status = RideStatus.COMPLETED.name
        )
        rideDao.updateRide(updated)
        Result.success(Unit)
    }

    suspend fun releaseRide(rideId: String, reason: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (reason.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("REASON_REQUIRED"))
        }
        val rideEntity = rideDao.getRideById(rideId)
            ?: return@withContext Result.failure(IllegalStateException("RIDE_NOT_FOUND"))

        val updated = rideEntity.copy(
            status = RideStatus.PENDING.name,
            driverId = null,
            notes = if (rideEntity.notes.isBlank()) "Relâchée: $reason" else "${rideEntity.notes} | Relâchée: $reason"
        )
        rideDao.updateRide(updated)
        Result.success(Unit)
    }

    suspend fun deleteAccount(): Result<Unit> = withContext(Dispatchers.IO) {
        val profile = driverProfileDao.getProfile()
        if (profile != null) {
            val activeRide = rideDao.getActiveRide(profile.uid).firstOrNull()
            if (activeRide != null) {
                return@withContext Result.failure(IllegalStateException("HAS_ACTIVE_RIDE"))
            }
        }
        firebaseService.signOut()
        driverProfileDao.clearProfile()
        Result.success(Unit)
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        val profile = driverProfileDao.getProfile()
        if (profile != null) {
            driverProfileDao.updateAvailability(profile.uid, false)
        }
        firebaseService.signOut()
        driverProfileDao.clearProfile()
    }

    fun toggleOfflineCacheMode() {
        _isOfflineCacheMode.value = !_isOfflineCacheMode.value
    }
}
