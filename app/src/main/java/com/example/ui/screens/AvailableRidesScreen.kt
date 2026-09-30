package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ride
import com.example.ui.components.ServiceBadge
import com.example.ui.components.launchAddressSearch
import com.example.ui.components.launchNavigationIntent
import com.example.ui.theme.AccentRed
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        if (rides.isEmpty()) {
            EmptyAvailableRidesState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
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
                        onAccept = { onAcceptRide(ride) }
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
fun AvailableRideCard(
    ride: Ride,
    hasActiveRide: Boolean,
    isOfflineCache: Boolean,
    isAccepting: Boolean,
    onAccept: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
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
                ServiceBadge(service = ride.service)

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

            // Action row: Itinerary preview + Accept Button
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    Spacer(modifier = Modifier.width(10.dp))
                }

                // Accept Button
                val canAccept = !hasActiveRide && !isOfflineCache
                Button(
                    onClick = onAccept,
                    enabled = canAccept && !isAccepting,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
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
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Acceptation en cours...")
                    } else if (hasActiveRide) {
                        Text(
                            text = "Course en cours active",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InkMuted
                            )
                        )
                    } else if (isOfflineCache) {
                        Text(
                            text = "Hors ligne (cache seul)",
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
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Accepter la course",
                                style = MaterialTheme.typography.titleMedium.copy(
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
    }
}
