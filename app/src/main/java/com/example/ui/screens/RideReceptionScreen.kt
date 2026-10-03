package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
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
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.InkDark
import com.example.ui.theme.InkMuted
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedContainer
import kotlinx.coroutines.delay

/**
 * Vue 'Réception de course'
 * Affiche tous les détails d'une nouvelle demande (départ, destination, prix estimé)
 * avec des boutons 'Accepter' et 'Refuser' clairs et accessibles.
 */
@Composable
fun RideReceptionScreen(
    ride: Ride,
    isAccepting: Boolean = false,
    onAccept: (Ride) -> Unit,
    onDecline: (Ride) -> Unit,
    modifier: Modifier = Modifier,
    initialCountdownSeconds: Int = 30
) {
    val context = LocalContext.current
    var remainingSeconds by remember { mutableIntStateOf(initialCountdownSeconds) }

    // Intercept back button to trigger refuse
    BackHandler {
        onDecline(ride)
    }

    // Countdown timer for dispatch auto-expiry
    LaunchedEffect(ride.id) {
        remainingSeconds = initialCountdownSeconds
        while (remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds -= 1
        }
        // When countdown expires, auto decline offer
        onDecline(ride)
    }

    // Pulsing radar animation for live incoming alert
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_radar")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val progressAnimated by animateFloatAsState(
        targetValue = remainingSeconds.toFloat() / initialCountdownSeconds.toFloat(),
        animationSpec = tween(durationMillis = 500),
        label = "countdown_progress"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 1. Header Banner: "Réception de course" + Live Pulse Badge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reception_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, GreenBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Flashing incoming badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GreenContainer,
                            border = BorderStroke(1.dp, GreenBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .scale(pulseScale)
                                        .clip(CircleShape)
                                        .background(GreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "NOUVELLE DEMANDE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GreenPrimary,
                                        letterSpacing = 0.5.sp
                                    )
                                )
                            }
                        }

                        // Countdown badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (remainingSeconds <= 10) RedContainer else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (remainingSeconds <= 10) RedBorder else BorderInput)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = if (remainingSeconds <= 10) AccentRed else InkMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${remainingSeconds}s",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (remainingSeconds <= 10) AccentRed else InkDark
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Réception de course",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = InkDark
                        ),
                        modifier = Modifier.testTag("ride_reception_title")
                    )

                    Text(
                        text = "Une course correspond à votre position à Dabou",
                        style = MaterialTheme.typography.bodySmall.copy(color = InkMuted),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar indicator of countdown
                    LinearProgressIndicator(
                        progress = { progressAnimated },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (remainingSeconds <= 10) AccentRed else GreenPrimary,
                        trackColor = Color(0xFFE2E8F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Prix Estimé (Very Prominent Card)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("estimated_price_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GreenContainer),
                border = BorderStroke(1.5.dp, GreenBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ServiceBadge(service = ride.service)

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                tint = GreenDark,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${"%.1f".format(ride.distanceKm)} km • ~${ride.durationMin} min",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = GreenDark
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PRIX ESTIMÉ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GreenDark,
                            letterSpacing = 1.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${ride.priceFcfa} FCFA",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = GreenPrimary
                        ),
                        modifier = Modifier.testTag("estimated_price_text")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Paiement en espèces ou Mobile Money à bord",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = InkMuted,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Détails Itinéraire (Départ & Destination)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("itinerary_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, BorderInput)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Itinéraire de la course",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // DÉPART (Prise en charge)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("pickup_row"),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = GreenContainer,
                            modifier = Modifier.size(36.dp),
                            border = BorderStroke(1.dp, GreenBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Départ",
                                    tint = GreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DÉPART (Prise en charge)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GreenDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ride.pickupAddress,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = InkDark
                                ),
                                modifier = Modifier.testTag("pickup_address_text")
                            )
                        }
                    }

                    // Vertical connector line
                    Row(modifier = Modifier.padding(start = 17.dp)) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(28.dp)
                                .background(BorderInput)
                        )
                    }

                    // DESTINATION (Dépose)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("destination_row"),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = RedContainer,
                            modifier = Modifier.size(36.dp),
                            border = BorderStroke(1.dp, RedBorder)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Destination",
                                    tint = AccentRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DESTINATION (Dépose client)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentRed
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = ride.destinationAddress,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = InkDark
                                ),
                                modifier = Modifier.testTag("destination_address_text")
                            )
                        }
                    }

                    // Google Maps link if available
                    val hasCoords = (ride.pickupLat != null && ride.pickupLng != null && ride.pickupLat != 0.0 && ride.pickupLng != 0.0)
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            if (hasCoords) {
                                launchNavigationIntent(context, ride.pickupLat!!, ride.pickupLng!!)
                            } else {
                                launchAddressSearch(context, ride.pickupAddress)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("preview_map_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderInput)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Directions,
                            contentDescription = null,
                            tint = InkDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Aperçu de l'itinéraire sur carte",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = InkDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. Client & Additional Information Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("client_info_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, BorderInput)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = InkMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Client demandeur",
                                style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
                            )
                            Text(
                                text = ride.clientFirstName.ifBlank { "Client Allô Dabou" },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = InkDark
                                )
                            )
                        }
                    }

                    if (ride.packageDetails.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Colis : ${ride.packageDetails}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = GreenDark
                                )
                            )
                        }
                    }

                    if (ride.conciergeTask.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Mission de conciergerie : ${ride.conciergeTask}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color = GreenDark
                            )
                        )
                    }

                    if (ride.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Instructions du client : \"${ride.notes}\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = InkMuted,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 5. Actions Footer: Boutons 'Accepter' et 'Refuser'
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // BOUTON 'REFUSER'
                    OutlinedButton(
                        onClick = { onDecline(ride) },
                        enabled = !isAccepting,
                        modifier = Modifier
                            .weight(0.42f)
                            .height(56.dp)
                            .testTag("refuse_ride_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = RedContainer.copy(alpha = 0.5f),
                            contentColor = AccentRed
                        ),
                        border = BorderStroke(1.5.dp, RedBorder)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Refuser",
                                tint = AccentRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Refuser",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentRed
                                )
                            )
                        }
                    }

                    // BOUTON 'ACCEPTER'
                    Button(
                        onClick = { onAccept(ride) },
                        enabled = !isAccepting,
                        modifier = Modifier
                            .weight(0.58f)
                            .height(56.dp)
                            .testTag("accept_ride_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
                    ) {
                        if (isAccepting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Acceptation...",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Accepter",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Accepter",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
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
}
