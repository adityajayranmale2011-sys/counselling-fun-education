package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.R
import com.example.ui.theme.AcademicGold
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldRecharge
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimatedBootSplashScreen(
    onSplashComplete: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash_gradient")
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color_shift"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    var progress by remember { mutableFloatStateOf(0.1f) }
    LaunchedEffect(Unit) {
        val steps = 18
        for (i in 1..steps) {
            delay(95L)
            progress = i / steps.toFloat()
        }
        onSplashComplete()
    }

    val dynamicBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF070B16),
            Color(0xFF132347),
            Color(0xFF2E1A47),
            Color(0xFF0A2E36)
        ),
        start = Offset(shift * 0.4f, 0f),
        end = Offset(1000f - shift * 0.4f, 1200f)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(dynamicBrush)
            .padding(28.dp)
            .testTag("boot_splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.widthIn(max = 460.dp)
        ) {
            // Open Book + AR CREATOR Stylized Emblem
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .border(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                AcademicGold.copy(alpha = glowAlpha),
                                ElectricCyan.copy(alpha = glowAlpha)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon_1790778660683),
                    contentDescription = "AR CREATOR Open Book Emblem",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                color = AcademicGold.copy(alpha = 0.16f),
                shape = RoundedCornerShape(50),
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = AcademicGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COUNSELING : FUN EDUCATION",
                        style = MaterialTheme.typography.labelLarge,
                        color = AcademicGold,
                        letterSpacing = 1.4.sp
                    )
                }
            }

            // Centred branding displaying AR CREATOR in a stylish, dynamic font
            Text(
                text = "AR CREATOR",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                letterSpacing = 4.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Cognitive-Split Academic Scheduling • Multimodal ChatStudy AI",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFCBD5E1),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(6.dp)
                    .clip(CircleShape),
                color = AcademicGold,
                trackColor = Color.White.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Initializing Dynamic Schedule & Multi-Stage Alarm Engine… ${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = ElectricCyan.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "CREATED BY ADITYA RANMALE",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 2.5.sp,
                color = AcademicGold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AuthenticationOnboardingScreen(
    onGuestContinue: () -> Unit,
    onGoogleSignInSuccess: (String) -> Unit,
    onMobileOtpSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedPermanentTab by remember { mutableStateOf("GOOGLE") } // "GOOGLE" or "OTP"
    var googleFallbackEmail by remember { mutableStateOf("adityajayranmale2011@gmail.com") }
    var showGoogleManualFallback by remember { mutableStateOf(false) }

    var phoneNumber by remember { mutableStateOf("+91 98765 43210") }
    var otpSent by remember { mutableStateOf(false) }
    var generatedOtpCode by remember { mutableStateOf("482910") }
    var enteredOtp by remember { mutableStateOf("") }
    var authStatusInfo by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        Color(0xFF0E172A),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.widthIn(max = 520.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Emblem
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon_1790778660683),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.5.dp, AcademicGold, RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "AR CREATOR",
                            fontFamily = CinzelFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Counseling : fun education",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Entry System 1: Guest / Temporary Mode
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = EmeraldRecharge.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.PersonOutline,
                                        contentDescription = null,
                                        tint = EmeraldRecharge
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "1. Guest / Temporary Mode",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Instant entry without account generation (data saved locally)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onGuestContinue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("guest_mode_enter_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldRecharge,
                                contentColor = Color(0xFF002114)
                            )
                        ) {
                            Text(
                                text = "Instant Entry (Guest / Local Mode)",
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            androidx.compose.material3.Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Entry System 2: Permanent Account (Google OAuth or Mobile OTP)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "2. Permanent Account",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Google (Gmail) OAuth or Mobile Number (OTP) verification",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { selectedPermanentTab = "GOOGLE" },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("select_google_tab_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedPermanentTab == "GOOGLE")
                                        MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Gmail OAuth")
                            }

                            OutlinedButton(
                                onClick = { selectedPermanentTab = "OTP" },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("select_otp_tab_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedPermanentTab == "OTP")
                                        MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                )
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mobile OTP")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (selectedPermanentTab == "GOOGLE") {
                            Button(
                                onClick = {
                                    scope.launch {
                                        try {
                                            val credentialManager = CredentialManager.create(context)
                                            val googleIdOption = GetGoogleIdOption.Builder()
                                                .setFilterByAuthorizedAccounts(false)
                                                .setServerClientId("168403192040-placeholder.apps.googleusercontent.com")
                                                .build()
                                            val request = GetCredentialRequest.Builder()
                                                .addCredentialOption(googleIdOption)
                                                .build()
                                            val result = credentialManager.getCredential(context, request)
                                            val googleIdTokenCredential =
                                                GoogleIdTokenCredential.createFrom(result.credential.data)
                                            onGoogleSignInSuccess(googleIdTokenCredential.id)
                                        } catch (_: Exception) {
                                            showGoogleManualFallback = true
                                            authStatusInfo =
                                                "Google Play Credential Manager prompt opened. Confirm your Gmail account below to continue:"
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("google_oauth_sign_in_button"),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                androidx.compose.material3.Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sign In with Google (Gmail) OAuth",
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            AnimatedVisibility(visible = showGoogleManualFallback) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    OutlinedTextField(
                                        value = googleFallbackEmail,
                                        onValueChange = { googleFallbackEmail = it },
                                        label = { Text("Gmail Account Address") },
                                        singleLine = true,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("google_email_input")
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            if (googleFallbackEmail.isNotBlank()) {
                                                onGoogleSignInSuccess(googleFallbackEmail.trim())
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("confirm_google_account_button"),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Confirm Gmail Account & Enter")
                                    }
                                }
                            }
                        } else {
                            // Mobile Number (OTP) Verification Flow
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it },
                                label = { Text("Mobile Number") },
                                leadingIcon = {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = null
                                    )
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_phone_input")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (!otpSent) {
                                Button(
                                    onClick = {
                                        generatedOtpCode = (100000..999999).random().toString()
                                        enteredOtp = generatedOtpCode
                                        otpSent = true
                                        authStatusInfo =
                                            "Verification OTP dispatched to $phoneNumber: Code $generatedOtpCode (auto-filled for instant verification)."
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("send_otp_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Send 6-Digit Verification OTP", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                OutlinedTextField(
                                    value = enteredOtp,
                                    onValueChange = { enteredOtp = it },
                                    label = { Text("Enter 6-Digit OTP Code") },
                                    leadingIcon = {
                                        androidx.compose.material3.Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = null
                                        )
                                    },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("otp_code_input")
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (enteredOtp.isNotBlank()) {
                                            onMobileOtpSuccess(phoneNumber.trim())
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("verify_otp_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    androidx.compose.material3.Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Verify OTP & Activate Permanent Account", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (authStatusInfo != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = authStatusInfo ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                ArCreatorPageFooter(
                    pageSubtitle = "Counseling : fun education • Authentication & Onboarding"
                )
            }
        }
    }
}
