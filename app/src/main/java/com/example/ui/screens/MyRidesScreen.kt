package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.RideStatus
import com.example.ui.components.ServiceBadge
import com.example.ui.components.launchAddressSearch
import com.example.ui.components.launchDialIntent
import com.example.ui.components.launchNavigationIntent
import com.example.ui.components.launchWhatsAppIntent
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AmberBorder
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.BgCanvas
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyRidesScreen(
    activeRide: Ride?,
    historyRides: List<Ride>,
    onCompleteRide: (String) -> Unit,
    onReleaseRide: (String, String) -> Unit,
    onNavigateToAvailable: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(if (activeRide != null) 0 else 1) }

    var showCompleteDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
    ) {
        // Sub-tabs
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedTab == 0) GreenContainer else Color.Transparent)
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "En cours",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (selectedTab == 0) GreenPrimary else InkMuted
                            )
                        )
                        if (activeRide != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GreenPrimary)
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedTab == 1) GreenContainer else Color.Transparent)
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Historique (${historyRides.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) GreenPrimary else InkMuted
                        )
                    )
                }
            }
        }

        if (selectedTab == 0) {
            if (activeRide != null) {
                ActiveRideDetailView(
                    ride = activeRide,
                    onOpenCompleteDialog = { showCompleteDialog = true },
                    onOpenCancelDialog = { showCancelDialog = true }
                )
            } else {
                EmptyActiveRideView(onNavigateToAvailable = onNavigateToAvailable)
            }
        } else {
            RidesHistoryView(rides = historyRides)
        }
    }

    // Complete Ride Dialog
    if (showCompleteDialog && activeRide != null) {
        CompleteRideDialog(
            ride = activeRide,
            onDismiss = { showCompleteDialog = false },
            onConfirm = {
                showCompleteDialog = false
                onCompleteRide(activeRide.id)
            }
        )
    }

    // Cancel / Release Ride Dialog
    if (showCancelDialog && activeRide != null) {
        CancelRideDialog(
            ride = activeRide,
            onDismiss = { showCancelDialog = false },
            onConfirm = { reason ->
                showCancelDialog = false
                onReleaseRide(activeRide.id, reason)
            }
        )
    }
}

@Composable
fun ActiveRideDetailView(
    ride: Ride,
    onOpenCompleteDialog: () -> Unit,
    onOpenCancelDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Status Progress Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = GreenContainer),
            border = androidx.compose.foundation.BorderStroke(1.dp, GreenBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Directions,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Course confirmée & active",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                    )
                    Text(
                        text = "En route vers la prise en charge ou la destination",
                        style = MaterialTheme.typography.bodySmall.copy(color = GreenDark.copy(alpha = 0.8f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Ride Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ServiceBadge(service = ride.service)
                    Text(
                        text = "${ride.priceFcfa} FCFA",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GreenPrimary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Addresses
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgCanvas, RoundedCornerShape(14.dp))
                        .padding(14.dp)
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
                                text = "Prise en charge",
                                style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
                            )
                            Text(
                                text = ride.pickupAddress,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = InkDark
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

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
                                    fontWeight = FontWeight.Bold,
                                    color = InkDark
                                )
                            )
                        }
                    }
                }

                if (ride.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Notes client : ${ride.notes}",
                        style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Client Contact Card (Private info only visible when assigned)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Contact du client",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = InkDark
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${ride.clientFirstName} • ${ride.contact?.phone ?: "+225 07 00 00 00 00"}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = InkMuted)
                )

                if (!ride.contact?.recipientPhone.isNullOrBlank()) {
                    Text(
                        text = "Destinataire du colis : ${ride.contact?.recipientPhone}",
                        style = MaterialTheme.typography.bodySmall.copy(color = GreenDark)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Direct Contact Buttons
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Call Button
                    Button(
                        onClick = {
                            val phone = ride.contact?.phone ?: ""
                            launchDialIntent(context, phone)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Appeler")
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // WhatsApp Button
                    Button(
                        onClick = {
                            val phone = ride.contact?.phone ?: ""
                            launchWhatsAppIntent(
                                context,
                                phone,
                                "Bonjour ${ride.clientFirstName}, je suis votre chauffeur Allô Dabou pour votre course."
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenContainer)
                    ) {
                        Text(
                            text = "WhatsApp",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GreenDark
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // GPS Navigation Buttons
        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = {
                    if (ride.pickupLat != null && ride.pickupLng != null && ride.pickupLat != 0.0) {
                        launchNavigationIntent(context, ride.pickupLat, ride.pickupLng)
                    } else {
                        launchAddressSearch(context, ride.pickupAddress)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
            ) {
                Icon(
                    imageVector = Icons.Default.Directions,
                    contentDescription = null,
                    tint = GreenPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Itinéraire Départ",
                    style = MaterialTheme.typography.labelMedium.copy(color = InkDark)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            OutlinedButton(
                onClick = {
                    if (ride.destinationLat != null && ride.destinationLng != null && ride.destinationLat != 0.0) {
                        launchNavigationIntent(context, ride.destinationLat, ride.destinationLng)
                    } else {
                        launchAddressSearch(context, ride.destinationAddress)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Itinéraire Dest.",
                    style = MaterialTheme.typography.labelMedium.copy(color = InkDark)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Complete Ride Action
        Button(
            onClick = onOpenCompleteDialog,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Terminer la course (${ride.priceFcfa} FCFA)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cancel / Release Button
        OutlinedButton(
            onClick = onOpenCancelDialog,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, RedBorder)
        ) {
            Text(
                text = "Annuler / Libérer la course",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = AccentRed
                )
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun EmptyActiveRideView(
    onNavigateToAvailable: () -> Unit,
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
                imageVector = Icons.Default.Directions,
                contentDescription = null,
                tint = GreenPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Aucune course en cours",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = InkDark
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Vous n'avez pas de course active actuellement. Rendez-vous sur l'onglet Courses pour en accepter une.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToAvailable,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Text(
                text = "Voir les courses disponibles",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun RidesHistoryView(
    rides: List<Ride>,
    modifier: Modifier = Modifier
) {
    val completedRides = rides.filter { it.status == RideStatus.COMPLETED }
    val totalEarnings = completedRides.sumOf { it.priceFcfa }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Daily Earnings Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Gains cumulés",
                            style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
                        )
                        Text(
                            text = "$totalEarnings FCFA",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GreenPrimary
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GreenContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GreenBorder)
                    ) {
                        Text(
                            text = "${completedRides.size} course${if (completedRides.size > 1) "s" else ""}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GreenDark
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        if (rides.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = InkMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Aucune course dans l'historique",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = InkDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        } else {
            items(rides) { ride ->
                HistoryRideCard(ride = ride)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HistoryRideCard(ride: Ride, modifier: Modifier = Modifier) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.FRENCH) }
    val formattedDate = dateFormat.format(Date(ride.createdAtMillis))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ServiceBadge(service = ride.service)

                val (badgeBg, badgeText, badgeLabel) = when (ride.status) {
                    RideStatus.COMPLETED -> Triple(GreenContainer, GreenDark, "Terminée")
                    RideStatus.CANCELLED -> Triple(RedContainer, AccentRed, "Annulée")
                    RideStatus.CONFIRMED -> Triple(AmberContainer, AccentAmber, "En cours")
                    RideStatus.PENDING -> Triple(AmberContainer, AccentAmber, "En attente")
                }

                Surface(
                    shape = RoundedCornerShape(999.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = badgeLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = badgeText
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${ride.pickupAddress} ➔ ${ride.destinationAddress}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = InkDark
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                )
                Text(
                    text = "${ride.priceFcfa} FCFA",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GreenPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun CompleteRideDialog(
    ride: Ride,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clôturer la course ?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "Avez-vous bien déposé le client ou livré le colis à destination ?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = GreenContainer
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Montant à encaisser :",
                            style = MaterialTheme.typography.labelSmall.copy(color = GreenDark)
                        )
                        Text(
                            text = "${ride.priceFcfa} FCFA",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GreenPrimary
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text("Oui, terminer la course")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Non, continuer")
            }
        }
    )
}

@Composable
fun CancelRideDialog(
    ride: Ride,
    onDismiss: () -> Unit,
    onConfirm: (reason: String) -> Unit
) {
    val predefinedReasons = listOf(
        "Client injoignable par téléphone",
        "Adresse ou point de rendez-vous introuvable",
        "Panne de véhicule / Problème mécanique",
        "Autre motif exceptionnel"
    )

    var selectedReason by remember { mutableStateOf(predefinedReasons[0]) }
    var freeText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Annuler la course",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "La course sera immédiatement remise dans la liste des autres chauffeurs. Veuillez obligatoirement indiquer le motif :",
                    style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                )
                Spacer(modifier = Modifier.height(10.dp))

                predefinedReasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedReason = reason }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason },
                            colors = RadioButtonDefaults.colors(selectedColor = GreenPrimary)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (selectedReason == reason) FontWeight.SemiBold else FontWeight.Normal,
                                color = InkDark
                            )
                        )
                    }
                }

                if (selectedReason == predefinedReasons.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = freeText,
                        onValueChange = { freeText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Précisez la raison...", color = InkMuted) },
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalReason = if (selectedReason == predefinedReasons.last() && freeText.isNotBlank()) {
                        "Autre : $freeText"
                    } else {
                        selectedReason
                    }
                    onConfirm(finalReason)
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text("Confirmer l'annulation")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Retour")
            }
        }
    )
}
