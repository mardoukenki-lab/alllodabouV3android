package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DriverProfile
import com.example.data.model.PlateGenerator
import com.example.ui.theme.BorderInput
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.InkDark
import com.example.ui.theme.InkMuted

@Composable
fun PlateGeneratorDialog(
    profile: DriverProfile?,
    onDismissRequest: () -> Unit,
    onSavePlates: (plate: String, badge: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isMissingInitialPlates = profile == null || profile.plate.isBlank() || profile.driverBadgeNumber.isBlank()

    var vehiclePlateInput by remember {
        mutableStateOf(
            if (profile?.plate.isNullOrBlank()) PlateGenerator.generateVehiclePlate() else profile!!.plate
        )
    }

    var driverBadgeInput by remember {
        mutableStateOf(
            if (profile?.driverBadgeNumber.isNullOrBlank()) PlateGenerator.generateDriverBadge() else profile!!.driverBadgeNumber
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier.fillMaxWidth(),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = GreenContainer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = null,
                            tint = GreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Générateur d'immatriculation",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = InkDark)
                    )
                    Text(
                        text = if (isMissingInitialPlates) "Attribution d'immatriculation chauffeur" else "Mise à jour de vos plaques",
                        style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                // Banner for drivers without registration
                if (isMissingInitialPlates) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Plaques générées automatiquement pour vous ! Vérifiez les combinaisons ci-dessous et validez.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Quick Action: Regenerate Both
                Button(
                    onClick = {
                        vehiclePlateInput = PlateGenerator.generateVehiclePlate()
                        driverBadgeInput = PlateGenerator.generateDriverBadge()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        tint = GreenDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Générer de nouvelles plaques",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = GreenDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Vehicle Plate Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1. Plaque véhicule CI (Obligatoire) *",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = InkDark)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GreenContainer,
                        modifier = Modifier.clickable {
                            vehiclePlateInput = PlateGenerator.generateVehiclePlate()
                        }
                    ) {
                        Text(
                            text = "🎲 Régénérer",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GreenPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = vehiclePlateInput,
                    onValueChange = { vehiclePlateInput = it.uppercase() },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: 7421-HJ-01 (Obligatoire)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = BorderInput
                    )
                )

                // Visual Representation of Vehicle Plate
                if (vehiclePlateInput.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF0F172A)),
                        shadowElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left CI stripe
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF1E40AF),
                                modifier = Modifier.padding(2.dp)
                            ) {
                                Text(
                                    text = "CI",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Text(
                                text = vehiclePlateInput,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color(0xFF0F172A),
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                    fontSize = 17.sp
                                )
                            )

                            // Region code Dabou / Lagunes (01)
                            Text(
                                text = "01",
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. Driver Badge Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "2. Plaque chauffeur (Matricule Allô Dabou)",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = InkDark)
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GreenContainer,
                        modifier = Modifier.clickable {
                            driverBadgeInput = PlateGenerator.generateDriverBadge()
                        }
                    ) {
                        Text(
                            text = "🎫 Régénérer",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GreenPrimary,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = driverBadgeInput,
                    onValueChange = { driverBadgeInput = it.uppercase() },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ex: DABOU-5832") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = BorderInput
                    )
                )

                // Visual Representation of Official Driver Badge
                if (driverBadgeInput.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = GreenDark,
                        shadowElevation = 2.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "★ VILLE DE DABOU • CHAUFFEUR OFFICIEL ★",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = driverBadgeInput,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp,
                                    fontSize = 17.sp
                                )
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalPlate = if (vehiclePlateInput.trim().isNotBlank()) {
                        vehiclePlateInput.trim().uppercase()
                    } else {
                        PlateGenerator.generateVehiclePlate()
                    }
                    val finalBadge = driverBadgeInput.trim().uppercase()
                    onSavePlates(finalPlate, finalBadge)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
            ) {
                Text(
                    text = "Valider et enregistrer mes plaques",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Annuler",
                    color = InkMuted
                )
            }
        }
    )
}
