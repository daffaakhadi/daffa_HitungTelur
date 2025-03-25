package com.daffa0050.assesment1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.daffa0050.assesment1.ui.theme.Assesment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment1Theme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { MainScreen(navController) }
                    composable("news") { NewsScreen(navController) }
                }
            }
        }
    }
}


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            MainScreen(navController)
        }
        composable(Screen.News.route) {
            NewsScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD7A86E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        ScreenContent(modifier = Modifier.padding(innerPadding), navController = navController)
    }
}


@Composable
fun ScreenContent(modifier: Modifier = Modifier, navController: NavHostController) {
    var kg by remember { mutableStateOf("") }
    var totalBayar by remember { mutableIntStateOf(0) }
    var showError by remember { mutableStateOf(false) }
    val hargaPerKg = 25000

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = kg,
            onValueChange = {
                kg = it
                showError = false
            },
            label = { Text(stringResource(id = R.string.input_kg)) },
            isError = showError,
            supportingText = {
                if (showError) {
                    Text(
                        text = stringResource(id = R.string.hitung_error),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "${stringResource(id = R.string.price_per_kg)} Rp $hargaPerKg")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (kg.isBlank()) {
                    showError = true
                } else {
                    val berat = kg.toDoubleOrNull() ?: 0.0
                    totalBayar = (berat * hargaPerKg).toInt()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.calculate))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${stringResource(id = R.string.result)}: Rp $totalBayar",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.navigate("news") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Baca Artikel tentang Telur")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val articles = listOf(
        Pair("Cara Menyimpan Telur Agar Awet", "https://www.kompas.com/food/read/2024/01/30/131300275/cara-menyimpan-telur-agar-awet"),
        Pair("Manfaat Telur untuk Kesehatan", "https://health.detik.com/berita-detikhealth/d-6007895/7-manfaat-telur-untuk-kesehatan"),
        Pair("Berapa Lama Telur Bisa Disimpan?", "https://www.cnnindonesia.com/gaya-hidup/20221012153704-262-858901/berapa-lama-telur-bisa-disimpan"),
        Pair("Telur Ayam Kampung vs Telur Ayam Negeri", "https://www.alodokter.com/telur-ayam-kampung-vs-telur-ayam-negeri-mana-yang-lebih-sehat"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.news_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD7A86E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.news_content),
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Daftar artikel
            articles.forEach { (title, url) ->
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Text(title)
                }
            }
        }
    }
}
