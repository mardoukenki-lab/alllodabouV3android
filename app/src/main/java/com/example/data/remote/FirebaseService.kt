package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.ClientContact
import com.example.data.model.DriverProfile
import com.example.data.model.DriverStatus
import com.example.data.model.Ride
import com.example.data.model.RideStatus
import com.example.data.model.ServiceType
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun <T> Task<T>.awaitTask(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { result -> cont.resume(result) }
    addOnFailureListener { exception -> cont.resumeWith(Result.failure(exception)) }
    addOnCanceledListener { cont.cancel() }
}

class FirebaseService(private val context: Context) {

    private val auth: FirebaseAuth? by lazy {
        ensureFirebaseInitialized()
        runCatching { FirebaseAuth.getInstance() }.getOrNull()
    }

    private val firestore: FirebaseFirestore? by lazy {
        ensureFirebaseInitialized()
        runCatching { FirebaseFirestore.getInstance() }.getOrNull()
    }

    private fun ensureFirebaseInitialized() {
        if (FirebaseApp.getApps(context).isEmpty()) {
            runCatching {
                // Initialize fallback app if google-services.json was not provided
                val options = FirebaseOptions.Builder()
                    .setApplicationId("ci.allodabou.driver")
                    .setProjectId("allo-dabou-driver")
                    .setApiKey("AIzaSyDummyFallbackKeyForChauffeurApp")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }.onFailure {
                Log.w("FirebaseService", "FirebaseApp init fallback warning: ${it.message}")
            }
        }
    }

    val currentUser: FirebaseUser?
        get() = auth?.currentUser

    suspend fun signUpWithEmail(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(IllegalStateException("Firebase Auth non disponible"))
        return runCatching {
            val result = authInstance.createUserWithEmailAndPassword(email.trim(), password).awaitTask()
            result.user ?: throw IllegalStateException("Utilisateur introuvable après inscription")
        }
    }

    suspend fun signInWithEmail(
        email: String,
        password: String
    ): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(IllegalStateException("Firebase Auth non disponible"))
        return runCatching {
            val result = authInstance.signInWithEmailAndPassword(email.trim(), password).awaitTask()
            result.user ?: throw IllegalStateException("Utilisateur introuvable après connexion")
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        val authInstance = auth ?: return Result.failure(IllegalStateException("Firebase Auth non disponible"))
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = authInstance.signInWithCredential(credential).awaitTask()
            result.user ?: throw IllegalStateException("Utilisateur Google introuvable après connexion")
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        val authInstance = auth ?: return Result.failure(IllegalStateException("Firebase Auth non disponible"))
        return runCatching {
            authInstance.sendPasswordResetEmail(email.trim()).awaitTask()
            Unit
        }
    }

    fun signOut() {
        runCatching { auth?.signOut() }
    }

    suspend fun saveDriverProfile(profile: DriverProfile): Result<Unit> {
        val db = firestore ?: return Result.success(Unit) // local fallback
        return runCatching {
            val data = hashMapOf(
                "uid" to profile.uid,
                "displayName" to profile.displayName,
                "email" to profile.email,
                "phone" to profile.phone,
                "vehicleNumber" to profile.plate,
                "driverBadge" to profile.driverBadgeNumber,
                "driverStatus" to profile.status.name.lowercase(),
                "available" to profile.available,
                "ratingAverage" to profile.ratingAverage,
                "ratingCount" to profile.ratingCount,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("users").document(profile.uid)
                .set(data, SetOptions.merge())
                .awaitTask()
            Unit
        }
    }

    suspend fun updateDriverPlate(uid: String, plate: String, badge: String): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return runCatching {
            db.collection("users").document(uid)
                .update(mapOf("vehicleNumber" to plate, "driverBadge" to badge, "updatedAt" to System.currentTimeMillis()))
                .awaitTask()
            Unit
        }
    }

    suspend fun getDriverProfile(uid: String): DriverProfile? {
        val db = firestore ?: return null
        return runCatching {
            val snapshot = db.collection("users").document(uid).get().awaitTask()
            if (!snapshot.exists()) return null
            val statusStr = snapshot.getString("driverStatus") ?: "approved"
            val status = when (statusStr.lowercase()) {
                "approved" -> DriverStatus.APPROVED
                "pending" -> DriverStatus.PENDING
                "rejected" -> DriverStatus.REJECTED
                "suspended" -> DriverStatus.SUSPENDED
                else -> DriverStatus.APPROVED
            }
            DriverProfile(
                uid = uid,
                displayName = snapshot.getString("displayName") ?: "",
                email = snapshot.getString("email") ?: "",
                phone = snapshot.getString("phone") ?: "",
                plate = snapshot.getString("vehicleNumber") ?: snapshot.getString("plate") ?: "",
                driverBadgeNumber = snapshot.getString("driverBadge") ?: "",
                status = status,
                available = snapshot.getBoolean("available") ?: true,
                ratingAverage = snapshot.getDouble("ratingAverage") ?: 5.0,
                ratingCount = snapshot.getLong("ratingCount")?.toInt() ?: 0
            )
        }.getOrNull()
    }

    suspend fun updateAvailability(uid: String, available: Boolean): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return runCatching {
            db.collection("users").document(uid)
                .update("available", available)
                .awaitTask()
            Unit
        }
    }

    fun observeFirestorePendingRides(): Flow<List<Ride>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("rides")
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w("FirebaseService", "Listen error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        runCatching {
                            val serviceStr = doc.getString("service") ?: "TAXI"
                            val service = runCatching { ServiceType.valueOf(serviceStr.uppercase()) }.getOrDefault(ServiceType.TAXI)
                            val statusStr = doc.getString("status") ?: "pending"
                            val status = runCatching { RideStatus.valueOf(statusStr.uppercase()) }.getOrDefault(RideStatus.PENDING)
                            val pickupAddress = doc.getString("pickupAddress") ?: ""
                            val destAddress = doc.getString("destinationAddress") ?: ""
                            val distance = doc.getDouble("distanceKm") ?: 0.0
                            val duration = doc.getLong("durationMin")?.toInt() ?: 10
                            val price = doc.getLong("priceFcfa")?.toInt() ?: 1000
                            val clientName = doc.getString("clientFirstName") ?: "Client"
                            val notes = doc.getString("notes") ?: ""
                            val packageDetails = doc.getString("packageDetails") ?: ""
                            val conciergeTask = doc.getString("conciergeTask") ?: ""
                            val userPhone = doc.getString("userPhone") ?: ""

                            Ride(
                                id = doc.id,
                                service = service,
                                status = status,
                                pickupAddress = pickupAddress,
                                pickupLat = doc.getDouble("pickupLat"),
                                pickupLng = doc.getDouble("pickupLng"),
                                destinationAddress = destAddress,
                                destinationLat = doc.getDouble("destinationLat"),
                                destinationLng = doc.getDouble("destinationLng"),
                                distanceKm = distance,
                                durationMin = duration,
                                priceFcfa = price,
                                packageDetails = packageDetails,
                                conciergeTask = conciergeTask,
                                notes = notes,
                                clientFirstName = clientName,
                                createdAtMillis = doc.getLong("createdAtMillis") ?: System.currentTimeMillis(),
                                driverId = doc.getString("driverId"),
                                contact = if (userPhone.isNotEmpty()) ClientContact(userPhone) else null
                            )
                        }.getOrNull()
                    }
                    trySend(list)
                }
            }

        awaitClose { listener.remove() }
    }
}
