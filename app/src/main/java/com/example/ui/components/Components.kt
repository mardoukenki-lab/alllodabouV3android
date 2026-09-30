package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriverProfile
import com.example.data.model.DriverStatus
import com.example.data.model.Ride
import com.example.data.model.ServiceType
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AmberBorder
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.BgInput
import com.example.ui.theme.BlueBorder
import com.example.ui.theme.BlueContainer
import com.example.ui.theme.BorderInput
import com.example.ui.theme.BorderLight
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.InkDark
import com.example.ui.theme.InkMuted
import com.example.ui.theme.RedBorder
import com.example.ui.theme.RedContainer
import com.example.ui.theme.TaxiYellow
import com.example.ui.theme.TaxiYellowContainer

@Composable
fun TopHeaderBar(
    profile: DriverProfile?,
    isOfflineCache: Boolean,
    isAlertsHealthy: Boolean,
    onToggleAvailability: () -> Unit,
    onOpenAlertsSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Allô Dabou",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    )
                }
                Text(
                    text = profile?.displayName?.ifBlank { profile.email } ?: "Espace Chauffeur",
                    style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                )
            }

            // Availability Toggle Pill
            if (profile != null) {
                AvailabilityPill(
                    isAvailable = profile.available,
                    onClick = onToggleAvailability
                )
            }
        }

        // Offline / Cache Banner
        AnimatedVisibility(visible = isOfflineCache) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
                color = AmberContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WifiOff,
                        contentDescription = "Mode hors ligne",
                        tint = AccentAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mode cache : données non à jour, boutons d'acceptation désactivés.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AccentAmber,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        // Notification Alerts Degraded Warning Banner
        AnimatedVisibility(visible = !isAlertsHealthy) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clickable { onOpenAlertsSettings() },
                shape = RoundedCornerShape(12.dp),
                color = RedContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, RedBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsOff,
                            contentDescription = "Alertes coupées",
                            tint = AccentRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Notifications désactivées : vous risquez de manquer des courses.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AccentRed,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                    Text(
                        text = "Régler",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AccentRed,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AvailabilityPill(
    isAvailable: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isAvailable) GreenContainer else BgInput,
        label = "pillBg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isAvailable) GreenBorder else BorderInput,
        label = "pillBorder"
    )
    val textColor = if (isAvailable) GreenPrimary else InkMuted

    // Subtle pulsing animation for dot when active
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotScale"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(999.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(if (isAvailable) dotScale else 1f)
                    .clip(CircleShape)
                    .background(if (isAvailable) GreenPrimary else InkMuted)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isAvailable) "DISPONIBLE" else "INDISPONIBLE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    letterSpacing = 0.3.sp
                )
            )
        }
    }
}

@Composable
fun ActiveRideStickyBar(
    ride: Ride,
    onOpenRide: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenRide() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Course en cours • ${ride.service.label}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${ride.clientFirstName} • ${ride.destinationAddress}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${ride.priceFcfa} FCFA • ${"%.1f".format(ride.distanceKm)} km",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GreenContainer,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Call Quick Action
                IconButton(
                    onClick = {
                        val phone = ride.contact?.phone
                        if (!phone.isNullOrBlank()) {
                            launchDialIntent(context, phone)
                        } else {
                            Toast.makeText(context, "Numéro du client non disponible", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Appeler le client",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Navigation Quick Action
                IconButton(
                    onClick = {
                        val lat = ride.destinationLat ?: ride.pickupLat
                        val lng = ride.destinationLng ?: ride.pickupLng
                        if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                            launchNavigationIntent(context, lat, lng)
                        } else {
                            launchAddressSearch(context, ride.destinationAddress)
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = "Navigation GPS",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceBadge(service: ServiceType, modifier: Modifier = Modifier) {
    val (bgColor, textColor, borderColor, icon) = when (service) {
        ServiceType.TAXI -> Quadruple(TaxiYellowContainer, Color(0xFF854D0E), Color(0xFFFDE047), Icons.Default.LocalTaxi)
        ServiceType.DELIVERY -> Quadruple(GreenContainer, GreenDark, GreenBorder, Icons.Default.LocalShipping)
        ServiceType.CONCIERGE -> Quadruple(BlueContainer, AccentBlue, BlueBorder, Icons.Default.ShoppingBag)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = service.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}

@Composable
fun DriverStatusBadge(status: DriverStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, borderColor, label) = when (status) {
        DriverStatus.APPROVED -> Quadruple(GreenContainer, GreenDark, GreenBorder, "Compte validé")
        DriverStatus.PENDING -> Quadruple(AmberContainer, AccentAmber, AmberBorder, "En attente")
        DriverStatus.REJECTED -> Quadruple(RedContainer, AccentRed, RedBorder, "Non retenu")
        DriverStatus.SUSPENDED -> Quadruple(RedContainer, AccentRed, RedBorder, "Suspendu")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(999.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

fun launchDialIntent(context: Context, rawPhone: String) {
    runCatching {
        val clean = rawPhone.replace(" ", "").replace("-", "")
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$clean"))
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, "Impossible d'ouvrir l'application d'appel", Toast.LENGTH_SHORT).show()
    }
}

fun launchWhatsAppIntent(context: Context, rawPhone: String, initialMessage: String = "") {
    runCatching {
        val clean = rawPhone.replace(" ", "").replace("+", "").replace("-", "")
        val uri = if (initialMessage.isNotBlank()) {
            Uri.parse("https://wa.me/$clean?text=${Uri.encode(initialMessage)}")
        } else {
            Uri.parse("https://wa.me/$clean")
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, "Impossible d'ouvrir WhatsApp", Toast.LENGTH_SHORT).show()
    }
}

fun launchNavigationIntent(context: Context, lat: Double, lng: Double) {
    runCatching {
        // First try Google Navigation
        val navUri = Uri.parse("google.navigation:q=$lat,$lng&mode=d")
        val intent = Intent(Intent.ACTION_VIEW, navUri)
        context.startActivity(intent)
    }.recoverCatching {
        // Fallback geo:
        val geoUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, "Aucune application de carte ou navigation trouvée", Toast.LENGTH_SHORT).show()
    }
}

fun launchAddressSearch(context: Context, address: String) {
    runCatching {
        val geoUri = Uri.parse("geo:0,0?q=${Uri.encode("$address, Dabou")}")
        val intent = Intent(Intent.ACTION_VIEW, geoUri)
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(context, "Impossible d'ouvrir la carte", Toast.LENGTH_SHORT).show()
    }
}
