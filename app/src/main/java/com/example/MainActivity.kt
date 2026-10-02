package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DriverGate
import com.example.data.model.DriverStatus
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.MainDriverScreen
import com.example.ui.screens.PendingApprovalScreen
import com.example.ui.screens.SuspendedScreen
import com.example.ui.theme.BgCanvas
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val lifecycleOwner = LocalLifecycleOwner.current

                // Refresh alerts health check on resume
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.refreshAlertsHealth()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                // Request POST_NOTIFICATIONS on Android 13+
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) {
                    viewModel.refreshAlertsHealth()
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                        if (!hasPermission) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
                }

                val driverGate by viewModel.driverGate.collectAsStateWithLifecycle()
                val currentProfile by viewModel.currentProfile.collectAsStateWithLifecycle()
                val availableRides by viewModel.availableRides.collectAsStateWithLifecycle()
                val driverRides by viewModel.driverRides.collectAsStateWithLifecycle()
                val activeRide by viewModel.activeRide.collectAsStateWithLifecycle()
                val isOfflineCache by viewModel.isOfflineCacheMode.collectAsStateWithLifecycle()
                val isAlertsHealthy by viewModel.isAlertsHealthy.collectAsStateWithLifecycle()
                val isAcceptingRideId by viewModel.isAcceptingRideId.collectAsStateWithLifecycle()
                val isAuthLoading by viewModel.isAuthLoading.collectAsStateWithLifecycle()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(BgCanvas)
                ) {
                    when (val gate = driverGate) {
                        is DriverGate.Loading -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = GreenPrimary,
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        is DriverGate.SignedOut -> {
                            AuthScreen(
                                isLoading = isAuthLoading,
                                onLoginEmail = { email, pass -> viewModel.loginWithEmail(email, pass) },
                                onRegisterEmail = { name, email, pass, phone, plate, driverBadge ->
                                    viewModel.registerWithEmail(name, email, pass, phone, plate, driverBadge)
                                },
                                onForgotPassword = { email, onResult ->
                                    viewModel.sendPasswordResetEmail(email, onResult)
                                }
                            )
                        }

                        is DriverGate.Blocked -> {
                            when (gate.status) {
                                DriverStatus.PENDING -> {
                                    PendingApprovalScreen(
                                        onRefresh = { viewModel.refreshAlertsHealth() },
                                        onSimulateApprove = { viewModel.switchDriverStatus(DriverStatus.APPROVED) },
                                        onSignOut = { viewModel.signOut() }
                                    )
                                }

                                DriverStatus.SUSPENDED, DriverStatus.REJECTED -> {
                                    SuspendedScreen(
                                        onSignOut = { viewModel.signOut() },
                                        onSimulateReactivate = { viewModel.switchDriverStatus(DriverStatus.APPROVED) }
                                    )
                                }

                                DriverStatus.APPROVED -> {
                                    // Should not occur as it's mapped to DriverGate.Ready
                                }
                            }
                        }

                        is DriverGate.WrongRole -> {
                            SuspendedScreen(
                                onSignOut = { viewModel.signOut() },
                                onSimulateReactivate = { viewModel.switchDriverStatus(DriverStatus.APPROVED) }
                            )
                        }

                        is DriverGate.Ready -> {
                            MainDriverScreen(
                                profile = currentProfile ?: gate.profile,
                                availableRides = availableRides,
                                historyRides = driverRides,
                                activeRide = activeRide,
                                isOfflineCache = isOfflineCache,
                                isAlertsHealthy = isAlertsHealthy,
                                acceptingRideId = isAcceptingRideId,
                                userMessages = viewModel.userMessage,
                                onToggleAvailability = { viewModel.toggleAvailability() },
                                onUpdatePlates = { plate, badge -> viewModel.updateDriverPlates(plate, badge) },
                                onAcceptRide = { ride -> viewModel.acceptRide(ride) },
                                onCompleteRide = { rideId -> viewModel.completeRide(rideId) },
                                onReleaseRide = { rideId, reason -> viewModel.releaseRide(rideId, reason) },
                                onTestSoundAndVibration = {
                                    viewModel.alertsManager.triggerAlertSoundAndVibration()
                                },
                                onOpenNotificationSettings = {
                                    startActivity(viewModel.alertsManager.openNotificationSettingsIntent())
                                },
                                onOpenBatterySettings = {
                                    startActivity(viewModel.alertsManager.openBatteryOptimizationSettingsIntent())
                                },
                                onDeleteAccount = { viewModel.deleteAccount() },
                                onSignOut = { viewModel.signOut() }
                            )
                        }
                    }
                }
            }
        }
    }
}
