package com.daffa0050.assesment1.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.daffa0050.assesment1.R
import com.daffa0050.assesment1.model.UserData
import com.daffa0050.assesment1.util.SettingsDataStore
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


@Composable
fun WelcomeScreen(
    navController: NavController,
    context: Context,
    dataStore: SettingsDataStore
) {
    val scope = rememberCoroutineScope()
    val mainColor = Color(0xFFD7A86E)
    val white = Color.White
    val userData by dataStore.userFlow.collectAsState(initial = UserData("", "", ""))
    var showExitDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mainColor)
            .padding(24.dp)
    ) {
        IconButton(
            onClick = { navController.navigate("info") },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .zIndex(1f) // memastikan tombol berada di atas elemen lain
                .size(48.dp)
                .background(Color.White, shape = CircleShape)
                .padding(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_info_24),
                contentDescription = "Info Aplikasi",
                tint = mainColor
            )
        }


        // Konten utama
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(white.copy(alpha = 0.1f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_egg_24),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(id = R.string.welcome_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = white
            )

            Text(
                text = stringResource(id = R.string.welcome_subtitle),
                fontSize = 18.sp,
                color = white.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            WelcomeButton(
                text = stringResource(id = R.string.login),
                textColor = mainColor,
                backgroundColor = white
            ) {
                navController.navigate("login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            WelcomeButton(
                text = stringResource(id = R.string.daftar),
                textColor = mainColor,
                backgroundColor = Color(0xFFF5F5F5)
            ) {
                navController.navigate("register")
            }

            Spacer(modifier = Modifier.height(24.dp))

            WelcomeButtonWithIcon(
                text = "Login with Google",
                iconResId = R.drawable.search,
                textColor = Color.Black,
                backgroundColor = Color.White
            ) {
                scope.launch {
                    if (userData.email.isNotEmpty()) {
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    } else {
                        signIn(context, dataStore)
                        navController.navigate("home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            WelcomeButton(
                text = stringResource(id = R.string.keluar_aplikasi),
                textColor = Color.White,
                backgroundColor = Color(0xFFB00020)
            ) {
                showExitDialog = true
            }


            Spacer(modifier = Modifier.height(32.dp))
        }
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = {
                    Text(text = stringResource(id = R.string.konfirmasi_keluar))
                },
                text = {
                    Text(text = stringResource(id = R.string.pertanyaan_keluar))
                },
                confirmButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                        exitProcess(0)
                    }) {
                        Text(text = stringResource(id = R.string.ya))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showExitDialog = false
                    }) {
                        Text(text = stringResource(id = R.string.tidak))
                    }
                }
            )
        }

    }
}

@Composable
fun WelcomeButton(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun WelcomeButtonWithIcon(
    text: String,
    iconResId: Int,
    textColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(6.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
