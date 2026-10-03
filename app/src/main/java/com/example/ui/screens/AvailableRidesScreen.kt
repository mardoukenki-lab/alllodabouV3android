package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriverProfile
import com.example.data.model.Ride
import com.example.ui.components.ServiceBadge
import com.example.ui.components.launchAddressSearch
import com.example.ui.components.launchNavigationIntent
import com.example.ui.theme.AccentRed
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedContainer
import com.example.ui.theme.BgCanvas
import com.example.ui.theme.BorderInput
import com.example.ui.theme.BorderLight
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.InkDark
import com.example.ui.theme.InkMuted

@Composable
fun AvailableRidesScreen(
    rides: List<Ride>,
    hasActiveRide: Boolean,
    isOfflineCache: Boolean,
    acceptingRideId: String?,
    onAcceptRide: (Ride) -> Unit,
    onRefuseRide: (Ride) -> Unit = {},
    onOpenReception: (Ride) -> Unit = {},
    onSimulateRide: () -> Unit = {},
    driverProfile: DriverProfile? = null,
    onOpenPlateGenerator: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isMissingPlates = driverProfile != null && (driverProfile.plate.isBlank() || driverProfile.driverBadgeNumber.isBlank())

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        if (rides.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isMissingPlates) {
                    MissingPlateBanner(onOpenGenerator = onOpenPlateGenerator)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                EmptyAvailableRidesState(onSimulateRide = onSimulateRide)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isMissingPlates) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        MissingPlateBanner(onOpenGenerator = onOpenPlateGenerator)
                    }
                }

                // Simulation banner to quickly test the 'Réception de course' view
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSimulateRide() }
                            .testTag("simulate_ride_reception_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = GreenContainer),
                        border = BorderStroke(1.dp, GreenBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = null,
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Simuler une réception de course",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = InkDark
                                        )
                                    )
                                    Text(
                                        text = "Ouvre la vue 'Réception de course' avec alerte",
                                        style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                item {
                    Text(
                        text = "${rides.size} course${if (rides.size > 1) "s" else ""} disponible${if (rides.size > 1) "s" else ""}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    )
                }

                items(rides, key = { it.id }) { ride ->
                    AvailableRideCard(
                        ride = ride,
                        hasActiveRide = hasActiveRide,
                        isOfflineCache = isOfflineCache,
                        isAccepting = acceptingRideId == ride.id,
                        onAccept = { onAcceptRide(ride) },
                        onRefuse = { onRefuseRide(ride) },
                        onOpenReception = { onOpenReception(ride) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun MissingPlateBanner(
    onOpenGenerator: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Immatriculation obligatoire manquante",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    )
                    Text(
                        text = "Générez votre plaque officielle en 1 clic.",
                        style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "La plaque d'immatriculation du véhicule est obligatoire pour prendre en charge des courses à Dabou. Générez votre plaque conforme immédiatement.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = InkDark.copy(alpha = 0.82f),
                    lineHeight = 17.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpenGenerator,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(
                    text = "🎲 Générer ma plaque obligatoire",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun AvailableRideCard(
    ride: Ride,
    hasActiveRide: Boolean,
    isOfflineCache: Boolean,
    isAccepting: Boolean,
    onAccept: () -> Unit,
    onRefuse: () -> Unit = {},
    onOpenReception: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenReception() }
            .testTag("ride_card_${ride.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Header: Service badge + Price + Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ServiceBadge(service = ride.service)
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.clickable { onOpenReception() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Réception",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = InkMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Ouvrir réception",
                                tint = InkMuted,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${ride.priceFcfa} FCFA",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenPrimary
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = InkMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${"%.1f".format(ride.distanceKm)} km • ~${ride.durationMin} min",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InkMuted,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Itinerary / Addresses
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BgCanvas, RoundedCornerShape(14.dp))
                    .padding(12.dp)
            ) {
                // Pickup
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Départ (Prise en charge)",
                            style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
                        )
                        Text(
                            text = ride.pickupAddress,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = InkDark
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Destination
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(AccentRed)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Destination",
                            style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
                        )
                        Text(
                            text = ride.destinationAddress,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = InkDark
                            )
                        )
                    }
                }
            }

            // Client and Notes info
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = InkMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Client : ${ride.clientFirstName}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = InkDark
                    )
                )
            }

            if (ride.packageDetails.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Colis : ${ride.packageDetails}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GreenDark,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            if (ride.conciergeTask.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mission : ${ride.conciergeTask}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GreenDark,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            if (ride.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Note : ${ride.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action row: Itinerary preview + Refuser + Accepter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Itinerary Preview Button (Only if valid coordinates exist)
                val hasCoords = (ride.pickupLat != null && ride.pickupLng != null && ride.pickupLat != 0.0 && ride.pickupLng != 0.0)
                if (hasCoords) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                launchNavigationIntent(context, ride.pickupLat!!, ride.pickupLng!!)
                            },
                        shape = RoundedCornerShape(14.dp),
                        color = GreenContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenBorder)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Directions,
                                contentDescription = "Voir itinéraire sur carte",
                                tint = GreenPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Bouton 'Refuser'
                OutlinedButton(
                    onClick = onRefuse,
                    enabled = !isAccepting,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("refuse_ride_card_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AccentRed
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RedBorder)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Refuser",
                        tint = AccentRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Refuser",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AccentRed
                        )
                    )
                }

                // Bouton 'Accepter'
                val canAccept = !hasActiveRide && !isOfflineCache
                Button(
                    onClick = onAccept,
                    enabled = canAccept && !isAccepting,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("accept_ride_card_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        disabledContainerColor = if (hasActiveRide) Color(0xFFE2E8F0) else Color(0xFFE5E7EB)
                    )
                ) {
                    if (isAccepting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "En cours...",
                            style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
                        )
                    } else if (hasActiveRide) {
                        Text(
                            text = "Active",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InkMuted
                            )
                        )
                    } else if (isOfflineCache) {
                        Text(
                            text = "Hors ligne",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InkMuted
                            )
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Accepter",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAvailableRidesState(
    onSimulateRide: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(GreenContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.NearMe,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "En attente de courses",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = InkDark
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Aucune course en attente à Dabou pour l'instant. Restez en statut « DISPONIBLE » : les nouvelles courses commandées par les clients apparaîtront ici automatiquement avec une alerte sonore.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSimulateRide,
            modifier = Modifier
                .height(48.dp)
                .testTag("empty_simulate_ride_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "⚡ Simuler une réception de course",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
    }
}
