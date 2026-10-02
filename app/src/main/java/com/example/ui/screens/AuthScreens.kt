package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.launchWhatsAppIntent
import com.example.ui.theme.AccentAmber
import com.example.ui.theme.AccentRed
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.BgCanvas
import com.example.ui.theme.BorderInput
import com.example.ui.theme.GreenBorder
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenDark
import com.example.ui.theme.GreenPrimary
import com.example.ui.theme.InkDark
import com.example.ui.theme.InkMuted
import com.example.ui.theme.RedContainer
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    isLoading: Boolean,
    onLoginEmail: (email: String, pass: String) -> Unit,
    onRegisterEmail: (name: String, email: String, pass: String, phone: String, plate: String, driverBadge: String) -> Unit,
    onForgotPassword: (email: String, onResult: (Boolean, String) -> Unit) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Connexion, 1: Inscription

    // Form fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+225 ") }
    var plate by remember { mutableStateOf("") }
    var driverBadge by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Forgot password dialog
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmailInput by remember { mutableStateOf("") }
    var forgotPasswordMessage by remember { mutableStateOf<String?>(null) }
    var forgotPasswordSuccess by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Hero Image Card with branding badge
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(165.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.driver_hero),
                    contentDescription = "Allô Dabou Chauffeur",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Official badge pill top-left
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = GreenDark.copy(alpha = 0.92f),
                    modifier = Modifier
                        .padding(14.dp)
                        .align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "PORTAIL OFFICIEL CHAUFFEURS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Title & Tagline
        Text(
            text = "Allô Dabou Chauffeur",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = InkDark
            )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "Connexion sécurisée pour chauffeurs et livreurs à Dabou",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Tabs: Connexion / Inscription
        Surface(
            modifier = Modifier.fillMaxWidth(),
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
                        .clickable {
                            selectedTab = 0
                            errorMessage = null
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Connexion",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 0) GreenPrimary else InkMuted
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (selectedTab == 1) GreenContainer else Color.Transparent)
                        .clickable {
                            selectedTab = 1
                            errorMessage = null
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Inscription",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == 1) GreenPrimary else InkMuted
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Auth Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Form Header
                Text(
                    text = if (selectedTab == 0) "Espace Chauffeur" else "Inscription Chauffeur Officiel",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = InkDark
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (selectedTab == 0)
                        "Connectez-vous avec votre e-mail et mot de passe pour accéder à vos courses."
                    else
                        "Renseignez vos coordonnées pour activer votre compte professionnel.",
                    style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 1) {
                    // Registration: Full Name
                    Text(
                        text = "Nom et prénom",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: Amadou Koné", color = InkMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = BorderInput
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Email Field
                Text(
                    text = "Adresse email",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = InkDark
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("chauffeur@exemple.com", color = InkMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary)
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = BorderInput
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Password Field
                Text(
                    text = "Mot de passe",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = InkDark
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Au moins 6 caractères", color = InkMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary)
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Masquer" else "Afficher"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = if (selectedTab == 1) ImeAction.Next else ImeAction.Done
                    ),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = BorderInput
                    )
                )

                // Forgot Password link for Login tab
                if (selectedTab == 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Mot de passe oublié ?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GreenPrimary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable {
                                    forgotEmailInput = email.trim()
                                    forgotPasswordMessage = null
                                    showForgotPasswordDialog = true
                                }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        )
                    }
                }

                if (selectedTab == 1) {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Confirm Password
                    Text(
                        text = "Confirmer le mot de passe",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Répétez le mot de passe", color = InkMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = GreenPrimary)
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = BorderInput
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Phone Number
                    Text(
                        text = "Numéro de téléphone (+225)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = InkDark
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("+225 07 00 00 00 00", color = InkMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = GreenPrimary)
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = BorderInput
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notice: Vehicle Plate is mandatory
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEF3C7),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "⚠️ La plaque d'immatriculation du véhicule est obligatoire pour tous les chauffeurs Allô Dabou. Si vous n'en avez pas encore, cliquez sur '🎲 Générer plaque CI' pour en créer une instantanément.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF92400E),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1. Plaque d'immatriculation Chauffeur (Optional)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Matricule chauffeur Allô Dabou (Optionnel)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InkDark
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenContainer,
                            modifier = Modifier.clickable {
                                driverBadge = com.example.data.model.PlateGenerator.generateDriverBadge()
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
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = driverBadge,
                        onValueChange = { driverBadge = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: DABOU-4821 (Facultatif)", color = InkMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GreenPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { driverBadge = com.example.data.model.PlateGenerator.generateDriverBadge() }) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = "Générer", tint = GreenPrimary)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = BorderInput
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Plaque du véhicule (Obligatoire)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plaque d'immatriculation véhicule (Obligatoire) *",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = InkDark
                            )
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GreenContainer,
                            modifier = Modifier.clickable {
                                plate = com.example.data.model.PlateGenerator.generateVehiclePlate()
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
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = plate,
                        onValueChange = { plate = it.uppercase() },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ex: 7421-HJ-01 (Obligatoire ou générer)", color = InkMuted) },
                        leadingIcon = {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = GreenPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { plate = com.example.data.model.PlateGenerator.generateVehiclePlate() }) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = "Générer", tint = GreenPrimary)
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = BorderInput
                        )
                    )
                }

                // Error Banner
                AnimatedVisibility(visible = errorMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = RedContainer
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = AccentRed,
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Email/Password Submit Button
                Button(
                    onClick = {
                        keyboardController?.hide()
                        if (email.trim().isBlank() || !email.contains("@")) {
                            errorMessage = "Veuillez entrer une adresse email valide."
                            return@Button
                        }
                        if (password.trim().length < 6) {
                            errorMessage = "Le mot de passe doit comporter au moins 6 caractères."
                            return@Button
                        }

                        if (selectedTab == 0) {
                            onLoginEmail(email.trim(), password.trim())
                        } else {
                            if (displayName.trim().length < 2) {
                                errorMessage = "Veuillez entrer votre nom complet."
                                return@Button
                            }
                            if (password.trim() != confirmPassword.trim()) {
                                errorMessage = "Les mots de passe ne correspondent pas."
                                return@Button
                            }
                            if (phone.trim().length < 8) {
                                errorMessage = "Veuillez saisir un numéro de téléphone valide."
                                return@Button
                            }
                            val cleanPlate = plate.trim().uppercase()
                            val cleanBadge = driverBadge.trim().uppercase()
                            if (cleanPlate.isBlank()) {
                                errorMessage = "La plaque d'immatriculation du véhicule est obligatoire. Cliquez sur '🎲 Générer plaque CI' si vous n'en avez pas."
                                return@Button
                            }
                            if (cleanPlate.length !in 4..14) {
                                errorMessage = "La plaque d'immatriculation doit comporter entre 4 et 14 caractères (ex: 7421-HJ-01)."
                                return@Button
                            }
                            onRegisterEmail(displayName.trim(), email.trim(), password.trim(), phone.trim(), cleanPlate, cleanBadge)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connexion en cours...")
                    } else {
                        Text(
                            text = if (selectedTab == 0) "Se connecter en toute sécurité" else "Créer mon compte chauffeur",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Assurance Pill Row
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderInput)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Firebase Auth SSL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InkDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(20.dp)
                        .background(BorderInput)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sécurité certifiée",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = InkDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        var isSubmittingReset by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = {
                if (!isSubmittingReset) showForgotPasswordDialog = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = GreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mot de passe oublié",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Saisissez votre adresse email chauffeur. Un lien sécurisé Firebase Auth vous sera envoyé pour réinitialiser votre mot de passe.",
                        style = MaterialTheme.typography.bodySmall.copy(color = InkMuted)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = forgotEmailInput,
                        onValueChange = { forgotEmailInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("chauffeur@exemple.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = GreenPrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (forgotPasswordMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = forgotPasswordMessage ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (forgotPasswordSuccess) GreenPrimary else AccentRed,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = forgotEmailInput.trim()
                        if (clean.isBlank() || !clean.contains("@")) {
                            forgotPasswordMessage = "Veuillez saisir une adresse email valide."
                            forgotPasswordSuccess = false
                            return@Button
                        }
                        isSubmittingReset = true
                        onForgotPassword(clean) { success, msg ->
                            isSubmittingReset = false
                            forgotPasswordSuccess = success
                            forgotPasswordMessage = msg
                            if (success) {
                                coroutineScope.launch {
                                    kotlinx.coroutines.delay(2000)
                                    showForgotPasswordDialog = false
                                }
                            }
                        }
                    },
                    enabled = !isSubmittingReset,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    if (isSubmittingReset) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Envoyer le lien")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }, enabled = !isSubmittingReset) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Composable
fun PendingApprovalScreen(
    onRefresh: () -> Unit,
    onSimulateApprove: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AmberContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = AccentAmber,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Compte en attente de validation",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = InkDark,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Votre profil chauffeur a bien été enregistré. L'équipe d'administration Allô Dabou examine vos pièces pour activer vos alertes de courses.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Contact Admin WhatsApp
        Button(
            onClick = {
                launchWhatsAppIntent(
                    context,
                    "+225 07 00 00 00 00",
                    "Bonjour l'équipe Allô Dabou, je viens de m'inscrire comme chauffeur et souhaite faire activer mon compte."
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
        ) {
            Text(
                text = "Contacter l'administrateur (WhatsApp)",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Refresh
        Button(
            onClick = onRefresh,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GreenContainer)
        ) {
            Text(
                text = "Vérifier le statut d'approbation",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = GreenDark
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Se déconnecter",
            style = MaterialTheme.typography.bodySmall.copy(
                color = InkMuted,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSignOut() }
                .padding(8.dp)
        )
    }
}

@Composable
fun SuspendedScreen(
    onSignOut: () -> Unit,
    onSimulateReactivate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BgCanvas)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(RedContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Accès chauffeur suspendu",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = InkDark,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Votre compte chauffeur a été temporairement suspendu par la direction Allô Dabou. Vous ne pouvez plus accepter de courses.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = InkMuted,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = {
                launchWhatsAppIntent(
                    context,
                    "+225 07 00 00 00 00",
                    "Bonjour, je contacte le support Allô Dabou concernant la suspension de mon compte chauffeur."
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
        ) {
            Text(
                text = "Contacter la direction Allô Dabou",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Se déconnecter",
            style = MaterialTheme.typography.bodySmall.copy(
                color = InkMuted,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSignOut() }
                .padding(8.dp)
        )
    }
}
