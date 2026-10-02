package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriverProfile
import com.example.ui.components.DriverStatusBadge
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

@Composable
fun AccountScreen(
    profile: DriverProfile?,
    isAlertsHealthy: Boolean,
    onToggleAvailability: () -> Unit,
    onUpdatePlates: (plate: String, badge: String) -> Unit,
    onTestSoundAndVibration: () -> Unit,
    onOpenNotificationSettings: () -> Unit,
    onOpenBatterySettings: () -> Unit,
    onDeleteAccount: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showBatteryGuideDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showPlateGeneratorDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Driver Profile Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(GreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = profile?.displayName ?: "Chauffeur",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = InkDark
                                )
                            )
                            Text(
                                text = profile?.phone ?: "+225 ...",
                                style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                            )
                        }
                    }

                    if (profile != null) {
                        DriverStatusBadge(status = profile.status)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row: Vehicle plate + Driver Badge + Ratings
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgCanvas, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = profile?.plate?.ifBlank { "Non renseignée" } ?: "Non renseignée",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (profile?.plate.isNullOrBlank()) InkMuted else InkDark
                                )
                            )
                        }
                        Text(
                            text = "Plaque véhicule",
                            style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(BorderInput)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = profile?.driverBadgeNumber?.ifBlank { "Non assignée" } ?: "Non assignée",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (profile?.driverBadgeNumber.isNullOrBlank()) InkMuted else GreenDark
                            )
                        )
                        Text(
                            text = "Plaque chauffeur",
                            style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(30.dp)
                            .background(BorderInput)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFEAB308),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile?.ratingAverage ?: 5.0}",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = InkDark
                                )
                            )
                        }
                        Text(
                            text = "Note moyenne",
                            style = MaterialTheme.typography.labelSmall.copy(color = InkMuted)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Availability Switch Card
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Disponibilité pour les courses",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (profile?.available == true) {
                            "Actif : vous recevez les alertes sonores de nouvelles courses."
                        } else {
                            "Inactif : aucune alerte de course ne vous sera envoyée."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                    )
                }

                Switch(
                    checked = profile?.available == true,
                    onCheckedChange = { onToggleAvailability() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = GreenPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Plate Generator Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(GreenContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsCar,
                                contentDescription = null,
                                tint = GreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Générateur d'immatriculation",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InkDark
                            )
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GreenContainer
                    ) {
                        Text(
                            text = "Disponible",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GreenDark,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "La plaque d'immatriculation du véhicule est obligatoire pour exercer. Générez en 1 clic une plaque au format officiel de Côte d'Ivoire (ex: 7421-HJ-01) ou votre matricule officiel chauffeur Allô Dabou.",
                    style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { showPlateGeneratorDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        Text(
                            text = "🎲 Ouvrir le générateur de plaques",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Réglages alertes
        Text(
            text = "Alertes & Optimisation",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = InkDark
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )

        // Alerts Health Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Health status row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isAlertsHealthy) GreenPrimary else AccentRed)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAlertsHealthy) "État des alertes : Optimal" else "État des alertes : Dégradé",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isAlertsHealthy) GreenDark else AccentRed
                            )
                        )
                    }

                    if (!isAlertsHealthy) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = RedContainer,
                            modifier = Modifier.clickable { onOpenNotificationSettings() }
                        ) {
                            Text(
                                text = "Activer",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AccentRed
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Test sound button
                OutlinedButton(
                    onClick = onTestSoundAndVibration,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
                ) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tester le son & vibration de course",
                        style = MaterialTheme.typography.labelMedium.copy(color = InkDark)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Battery Guide Button (Crucial for West African phones Tecno/Infinix/Itel/Xiaomi)
                OutlinedButton(
                    onClick = { showBatteryGuideDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
                ) {
                    Icon(
                        imageVector = Icons.Default.BatteryAlert,
                        contentDescription = null,
                        tint = AccentAmber,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Guide anti-coupure batterie (Tecno, Infinix, etc.)",
                        style = MaterialTheme.typography.labelMedium.copy(color = InkDark)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Assistance Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Support & Assistance Allô Dabou",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = InkDark
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Besoin d'aide pour votre compte ou vos courses ? L'équipe est disponible 7j/7.",
                    style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        launchWhatsAppIntent(
                            context,
                            "+225 07 00 00 00 00",
                            "Bonjour, je suis chauffeur Allô Dabou et j'ai une question d'assistance."
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenContainer)
                ) {
                    Text(
                        text = "Contacter le support WhatsApp",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GreenDark
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Out & Delete Account
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onSignOut
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    tint = InkMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Se déconnecter",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = InkMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            TextButton(
                onClick = { showDeleteAccountDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Supprimer le compte",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = AccentRed,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Battery Optimization Guide Dialog
    if (showBatteryGuideDialog) {
        AlertDialog(
            onDismissRequest = { showBatteryGuideDialog = false },
            title = {
                Text(
                    text = "Recevoir les alertes en continu",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Sur certains téléphones (Tecno, Infinix, Itel, Xiaomi, Samsung), le gestionnaire de batterie peut fermer l'application en arrière-plan et vous faire rater des courses.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = InkDark)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "1. Désactiver l'optimisation de la batterie pour Allô Dabou Chauffeur.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "2. Autoriser le démarrage automatique dans les réglages système.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "3. Verrouiller l'application dans les tâches récentes.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBatteryGuideDialog = false
                        onOpenBatterySettings()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Ouvrir les réglages batterie")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBatteryGuideDialog = false }) {
                    Text("Compris")
                }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = {
                Text(
                    text = "Supprimer mon compte chauffeur ?",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AccentRed
                    )
                )
            },
            text = {
                Text(
                    text = "Cette action est irréversible. Votre profil, historique et accès chauffeur seront définitivement effacés. Vous ne devez avoir aucune course en cours.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        onDeleteAccount()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Confirmer la suppression")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Plate Generator Dialog
    if (showPlateGeneratorDialog) {
        var currentPlateInput by remember { mutableStateOf(profile?.plate ?: "") }
        var currentBadgeInput by remember { mutableStateOf(profile?.driverBadgeNumber ?: "") }

        AlertDialog(
            onDismissRequest = { showPlateGeneratorDialog = false },
            title = {
                Text(
                    text = "Générateur de plaques & matricules",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Générez ou modifiez vos plaques d'immatriculation pour votre véhicule et votre matricule officiel chauffeur :",
                        style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Driver Badge Generator Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plaque chauffeur / Matricule",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = InkDark)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenContainer,
                            modifier = Modifier.clickable {
                                currentBadgeInput = com.example.data.model.PlateGenerator.generateDriverBadge()
                            }
                        ) {
                            Text(
                                text = "🎲 Générer matricule",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GreenPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = currentBadgeInput,
                        onValueChange = { currentBadgeInput = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: DABOU-7412 (Facultatif)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Visual Driver Badge Plate Preview
                    if (currentBadgeInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = GreenDark,
                            shadowElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "★ ALLÔ DABOU • CHAUFFEUR OFFICIEL ★",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = currentBadgeInput,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 2.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. Vehicle Plate Generator Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plaque véhicule CI (Obligatoire) *",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = InkDark)
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenContainer,
                            modifier = Modifier.clickable {
                                currentPlateInput = com.example.data.model.PlateGenerator.generateVehiclePlate()
                            }
                        ) {
                            Text(
                                text = "🎲 Générer plaque CI",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GreenPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = currentPlateInput,
                        onValueChange = { currentPlateInput = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: 7421-HJ-01 (Obligatoire)") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Visual Vehicle Plate Preview
                    if (currentPlateInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF9FAFB),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1E293B)),
                            shadowElevation = 2.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Blue CI strip
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF1E40AF),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = "CI",
                                            color = Color.White,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Text(
                                    text = currentPlateInput,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color(0xFF0F172A),
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    )
                                )

                                Text(
                                    text = "01",
                                    color = Color(0xFF64748B),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalPlate = if (currentPlateInput.trim().isNotBlank()) {
                            currentPlateInput.trim().uppercase()
                        } else {
                            com.example.data.model.PlateGenerator.generateVehiclePlate()
                        }
                        showPlateGeneratorDialog = false
                        onUpdatePlates(finalPlate, currentBadgeInput.trim().uppercase())
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("Enregistrer les plaques")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPlateGeneratorDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
