package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.alerts.AlertsManager
import com.example.data.local.AppDatabase
import com.example.data.model.DriverGate
import com.example.data.model.DriverProfile
import com.example.data.model.DriverStatus
import com.example.data.model.Ride
import com.example.data.repository.DriverRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val alertsManager = AlertsManager(application)
    private val database = AppDatabase.getInstance(application)
    val repository = DriverRepository(application, database, alertsManager, viewModelScope)

    val driverGate: StateFlow<DriverGate> = repository.driverGateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DriverGate.Loading
    )

    val currentProfile: StateFlow<DriverProfile?> = repository.currentProfileFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val isOfflineCacheMode: StateFlow<Boolean> = repository.isOfflineCacheMode

    private val _isAlertsHealthy = MutableStateFlow(alertsManager.areNotificationsHealthy())
    val isAlertsHealthy: StateFlow<Boolean> = _isAlertsHealthy.asStateFlow()

    private val _userMessage = MutableSharedFlow<String>()
    val userMessage: SharedFlow<String> = _userMessage.asSharedFlow()

    private val _isAcceptingRideId = MutableStateFlow<String?>(null)
    val isAcceptingRideId: StateFlow<String?> = _isAcceptingRideId.asStateFlow()

    private val _incomingRideOffer = MutableStateFlow<Ride?>(null)
    val incomingRideOffer: StateFlow<Ride?> = _incomingRideOffer.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    val availableRides: StateFlow<List<Ride>> = repository.availableRidesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val driverRides: StateFlow<List<Ride>> = repository.currentProfileFlow.flatMapLatest { profile ->
        if (profile != null) repository.getDriverRidesFlow(profile.uid) else flowOf(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val activeRide: StateFlow<Ride?> = repository.currentProfileFlow.flatMapLatest { profile ->
        if (profile != null) repository.getActiveRideFlow(profile.uid) else flowOf(null)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun refreshAlertsHealth() {
        _isAlertsHealthy.value = alertsManager.areNotificationsHealthy()
    }

    fun toggleAvailability() {
        val profile = currentProfile.value ?: return
        viewModelScope.launch {
            repository.setAvailability(!profile.available)
        }
    }

    fun toggleOfflineCache() {
        repository.toggleOfflineCacheMode()
    }

    fun acceptRide(ride: Ride) {
        if (isOfflineCacheMode.value) {
            viewModelScope.launch {
                _userMessage.emit("Données hors ligne (cache seul) : impossible d'accepter en mode hors ligne.")
            }
            return
        }

        viewModelScope.launch {
            _isAcceptingRideId.value = ride.id
            val result = repository.acceptRide(ride.id)
            _isAcceptingRideId.value = null
            result.onSuccess {
                _userMessage.emit("Course acceptée avec succès ! En route vers le client.")
            }.onFailure { ex ->
                when (ex.message) {
                    "ALREADY_TAKEN" -> _userMessage.emit("Cette course vient d'être prise par un autre chauffeur.")
                    "DRIVER_BUSY" -> _userMessage.emit("Terminez votre course en cours avant d'en accepter une autre.")
                    "NOT_APPROVED" -> _userMessage.emit("Votre compte n'est pas encore validé.")
                    else -> _userMessage.emit("Erreur : ${ex.message ?: "Impossible d'accepter la course."}")
                }
            }
        }
    }

    fun completeRide(rideId: String) {
        viewModelScope.launch {
            val result = repository.completeRide(rideId)
            result.onSuccess {
                _userMessage.emit("Course terminée avec succès ! Félicitations.")
            }.onFailure { ex ->
                _userMessage.emit("Erreur lors de la clôture de la course : ${ex.message}")
            }
        }
    }

    fun releaseRide(rideId: String, reason: String) {
        viewModelScope.launch {
            val result = repository.releaseRide(rideId, reason)
            result.onSuccess {
                _userMessage.emit("Course annulée et remise à disposition des autres chauffeurs.")
            }.onFailure { ex ->
                _userMessage.emit("Erreur : ${ex.message}")
            }
        }
    }

    fun showRideReception(ride: Ride) {
        _incomingRideOffer.value = ride
        alertsManager.triggerAlertSoundAndVibration()
    }

    fun dismissRideReception() {
        _incomingRideOffer.value = null
    }

    fun acceptRideOffer(ride: Ride) {
        _incomingRideOffer.value = null
        acceptRide(ride)
    }

    fun declineRideOffer(ride: Ride) {
        viewModelScope.launch {
            _incomingRideOffer.value = null
            repository.declineRide(ride.id)
            _userMessage.emit("Demande de course refusée.")
        }
    }

    fun simulateNewIncomingRide() {
        viewModelScope.launch {
            val ride = repository.simulateNewRideOffer()
            _incomingRideOffer.value = ride
            _userMessage.emit("Nouvelle demande de course reçue à Dabou !")
        }
    }

    fun registerWithEmail(
        name: String,
        email: String,
        pass: String,
        phone: String,
        plate: String,
        driverBadge: String = "",
        hasLicense: Boolean = true,
        licenseNumber: String = ""
    ) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = repository.registerDriverWithEmail(
                displayName = name,
                email = email,
                password = pass,
                phone = phone,
                plate = plate,
                driverBadge = driverBadge,
                hasLicense = hasLicense,
                licenseNumber = licenseNumber
            )
            _isAuthLoading.value = false
            result.onSuccess {
                _userMessage.emit("Compte chauffeur enregistré avec succès !")
            }.onFailure { ex ->
                _userMessage.emit(ex.message ?: "Erreur d'inscription")
            }
        }
    }

    fun loginWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = repository.loginDriverWithEmail(email, pass)
            _isAuthLoading.value = false
            result.onSuccess {
                _userMessage.emit("Connexion réussie.")
            }.onFailure { ex ->
                _userMessage.emit(ex.message ?: "Erreur de connexion. Vérifiez vos identifiants.")
            }
        }
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = repository.sendPasswordResetEmail(email)
            _isAuthLoading.value = false
            result.onSuccess {
                onResult(true, "Un email de réinitialisation vous a été envoyé.")
                _userMessage.emit("Email de réinitialisation envoyé à $email")
            }.onFailure { ex ->
                val msg = ex.message ?: "Impossible d'envoyer l'email de réinitialisation."
                onResult(false, msg)
                _userMessage.emit(msg)
            }
        }
    }

    fun loginWithGoogle(idToken: String, displayName: String?, email: String?, phone: String = "", plate: String = "", driverBadge: String = "") {
        viewModelScope.launch {
            _isAuthLoading.value = true
            val result = repository.loginOrRegisterWithGoogle(idToken, displayName, email, phone, plate, driverBadge)
            _isAuthLoading.value = false
            result.onSuccess {
                _userMessage.emit("Connexion Google réussie.")
            }.onFailure { ex ->
                _userMessage.emit(ex.message ?: "Échec de connexion Google")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _userMessage.emit("Déconnexion effectuée.")
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = repository.deleteAccount()
            result.onSuccess {
                _userMessage.emit("Compte supprimé définitivement.")
            }.onFailure { ex ->
                if (ex.message == "HAS_ACTIVE_RIDE") {
                    _userMessage.emit("Impossible de supprimer le compte avec une course en cours.")
                } else {
                    _userMessage.emit("Erreur : ${ex.message}")
                }
            }
        }
    }

    fun updateDriverPlates(plate: String, badge: String) {
        viewModelScope.launch {
            val result = repository.updateDriverPlates(plate, badge)
            result.onSuccess {
                _userMessage.emit("Plaques mises à jour avec succès !")
            }.onFailure { ex ->
                _userMessage.emit("Erreur lors de la mise à jour des plaques : ${ex.message}")
            }
        }
    }

    fun generateNewVehiclePlate(): String = com.example.data.model.PlateGenerator.generateVehiclePlate()
    fun generateNewDriverBadge(): String = com.example.data.model.PlateGenerator.generateDriverBadge()

    fun switchDriverStatus(status: DriverStatus) {
        viewModelScope.launch {
            repository.updateDriverStatus(status)
            _userMessage.emit("Statut chauffeur mis à jour : ${status.name}")
        }
    }
}
