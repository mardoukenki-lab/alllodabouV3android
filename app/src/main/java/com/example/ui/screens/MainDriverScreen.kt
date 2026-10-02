package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriverProfile
import com.example.data.model.DriverStatus
import com.example.data.model.Ride
import com.example.ui.components.ActiveRideStickyBar
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.BgCanvas
import com.example.ui.theme.BorderInput
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.InkDark
import com.example.ui.theme.InkMuted

@Composable
fun MainDriverScreen(
    profile: DriverProfile?,
    availableRides: List<Ride>,
    historyRides: List<Ride>,
    activeRide: Ride?,
    isOfflineCache: Boolean,
    isAlertsHealthy: Boolean,
    acceptingRideId: String?,
    userMessages: kotlinx.coroutines.flow.SharedFlow<String>,
    onToggleAvailability: () -> Unit,
    onUpdatePlates: (plate: String, badge: String) -> Unit,
    onAcceptRide: (Ride) -> Unit,
    onCompleteRide: (String) -> Unit,
    onReleaseRide: (String, String) -> Unit,
    onTestSoundAndVibration: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onDeleteAccount: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        userMessages.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopHeaderBar(
                profile = profile,
                isOfflineCache = isOfflineCache,
                isAlertsHealthy = isAlertsHealthy,
                onToggleAvailability = onToggleAvailability,
                onOpenAlertsSettings = onOpenNotificationSettings
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                // Sticky Active Ride Bar on tabs other than "Mes courses" (Tab 1)
                AnimatedVisibility(
                    visible = activeRide != null && selectedTab != 1,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    activeRide?.let { ride ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ActiveRideStickyBar(
                                ride = ride,
                                onOpenRide = { selectedTab = 1 }
                            )
                        }
                    }
                }

                // Standard Navigation Bar respecting WindowInsets.navigationBars
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Tab 0: Courses
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (availableRides.isNotEmpty()) {
                                        Badge(
                                            containerColor = GreenPrimary,
                                            contentColor = Color.White
                                        ) {
                                            Text("${availableRides.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == 0) Icons.Filled.ListAlt else Icons.Outlined.ListAlt,
                                    contentDescription = "Courses disponibles"
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "Courses",
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            selectedTextColor = GreenPrimary,
                            indicatorColor = GreenContainer,
                            unselectedIconColor = InkMuted,
                            unselectedTextColor = InkMuted
                        )
                    )

                    // Tab 1: Mes courses
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (activeRide != null) {
                                        Badge(
                                            containerColor = GreenPrimary,
                                            contentColor = Color.White
                                        ) {
                                            Text("1")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (selectedTab == 1) Icons.Filled.DirectionsCar else Icons.Outlined.DirectionsCar,
                                    contentDescription = "Mes courses"
                                )
                            }
                        },
                        label = {
                            Text(
                                text = "Mes courses",
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            selectedTextColor = GreenPrimary,
                            indicatorColor = GreenContainer,
                            unselectedIconColor = InkMuted,
                            unselectedTextColor = InkMuted
                        )
                    )

                    // Tab 2: Compte
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Compte chauffeur"
                            )
                        },
                        label = {
                            Text(
                                text = "Compte",
                                fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GreenPrimary,
                            selectedTextColor = GreenPrimary,
                            indicatorColor = GreenContainer,
                            unselectedIconColor = InkMuted,
                            unselectedTextColor = InkMuted
                        )
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> AvailableRidesScreen(
                    rides = availableRides,
                    hasActiveRide = activeRide != null,
                    isOfflineCache = isOfflineCache,
                    acceptingRideId = acceptingRideId,
                    onAcceptRide = onAcceptRide
                )

                1 -> MyRidesScreen(
                    activeRide = activeRide,
                    historyRides = historyRides,
                    onCompleteRide = onCompleteRide,
                    onReleaseRide = onReleaseRide,
                    onNavigateToAvailable = { selectedTab = 0 }
                )

                2 -> AccountScreen(
                    profile = profile,
                    isAlertsHealthy = isAlertsHealthy,
                    onToggleAvailability = onToggleAvailability,
                    onUpdatePlates = onUpdatePlates,
                    onTestSoundAndVibration = onTestSoundAndVibration,
                    onOpenNotificationSettings = onOpenNotificationSettings,
                    onOpenBatterySettings = onOpenBatterySettings,
                    onDeleteAccount = onDeleteAccount,
                    onSignOut = onSignOut
                )
            }
        }
    }
}
