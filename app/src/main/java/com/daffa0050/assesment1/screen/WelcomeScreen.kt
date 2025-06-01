package com.daffa0050.assesment1.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daffa0050.assesment1.R

@Composable
fun WelcomeScreen(navController: NavController) {
    val mainColor = Color(0xFFD7A86E)
    val white = Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(mainColor)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.baseline_egg_24),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )


            Text(
                text = stringResource(id = R.string.welcome_title),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = white
            )

            Text(
                text = stringResource(id = R.string.welcome_subtitle),
                fontSize = 16.sp,
                color = white
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("login") },
                colors = ButtonDefaults.buttonColors(containerColor = white),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.login),
                    color = mainColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Tombol Login dengan Google
            Button(
                onClick = {
                    // Arahkan ke fungsi login Google (implementasi disesuaikan)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.search), // Ganti dengan ID ikon Google kamu
                    contentDescription = "Google Icon",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Login with Google",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { navController.navigate("register") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF5F5F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.daftar),
                    color = mainColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Tombol Info Aplikasi
            Button(
                onClick = {
                    navController.navigate("info") // Ganti dengan screen tujuan kamu
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_info_24),
                    contentDescription = "Info Icon",
                    tint = mainColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.info_aplikasi),
                    color = mainColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
