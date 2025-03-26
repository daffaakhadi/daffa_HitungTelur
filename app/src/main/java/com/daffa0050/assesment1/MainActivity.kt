package com.daffa0050.assesment1

import android.content.Intent
import android.net.Uri
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(modifier: Modifier = Modifier, navController: NavHostController) {
    val retailLabel = stringResource(id = R.string.retail)
    val wholesaleLabel = stringResource(id = R.string.wholesale)
    val inputKgLabel = stringResource(id = R.string.input_kg)
    val jenisPembelianLabel = stringResource(id = R.string.jenis_pembelian)
    val calculateLabel = stringResource(id = R.string.calculate)
    val calculateWholesaleLabel = stringResource(id = R.string.calculate_grosir)
    val selectWholesalePackageLabel = stringResource(id = R.string.select_wholesale_package)
    val resultLabel = stringResource(id = R.string.result)
    val readArticleLabel = stringResource(id = R.string.read_article)

    val jenisOptions = listOf(retailLabel, wholesaleLabel)
    val grosirOptions = listOf(15, 30, 45, 60, 75, 90)
    val hargaPerKg = 25000
    val grosirHargaPer15Kg = 330000

    var jenisPembelian by rememberSaveable { mutableStateOf(retailLabel) }
    var kg by rememberSaveable { mutableStateOf("") }
    var totalBayar by rememberSaveable { mutableIntStateOf(0) }
    var grosirKg by rememberSaveable { mutableIntStateOf(15) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var expandedJenis by remember { mutableStateOf(false) }
    var expandedGrosir by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        ExposedDropdownMenuBox(expanded = expandedJenis, onExpandedChange = { expandedJenis = !expandedJenis }) {
            OutlinedTextField(
                readOnly = true,
                value = jenisPembelian,
                onValueChange = {},
                label = { Text(jenisPembelianLabel) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJenis) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(expanded = expandedJenis, onDismissRequest = { expandedJenis = false }) {
                jenisOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            jenisPembelian = option
                            expandedJenis = false
                            totalBayar = 0
                            kg = ""
                            errorMessage = ""
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = if (jenisPembelian == wholesaleLabel) R.drawable.telurgrosir else R.drawable.telureceran),
            contentDescription = "Gambar Telur",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (jenisPembelian == retailLabel) {
            OutlinedTextField(
                value = kg,
                onValueChange = {
                    kg = it
                    errorMessage = ""
                },
                label = { Text(inputKgLabel) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty(),
                supportingText = {
                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val kgInput = kg.toDoubleOrNull()
                    when {
                        kg.isEmpty() -> {
                            totalBayar = 0
                            errorMessage = context.getString(R.string.hitung_error)
                        }
                        kgInput == null || kgInput <= 0 -> {
                            totalBayar = 0
                            errorMessage = context.getString(R.string.invalid)
                        }
                        else -> {
                            totalBayar = (kgInput * hargaPerKg).toInt()
                            errorMessage = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(calculateLabel)
            }
        } else {
            ExposedDropdownMenuBox(expanded = expandedGrosir, onExpandedChange = { expandedGrosir = !expandedGrosir }) {
                OutlinedTextField(
                    readOnly = true,
                    value = "$grosirKg kg",
                    onValueChange = {},
                    label = { Text(selectWholesalePackageLabel) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGrosir) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandedGrosir, onDismissRequest = { expandedGrosir = false }) {
                    grosirOptions.forEach { kgOption ->
                        DropdownMenuItem(
                            text = { Text("$kgOption kg") },
                            onClick = {
                                grosirKg = kgOption
                                expandedGrosir = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    totalBayar = (grosirKg / 15) * grosirHargaPer15Kg
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(calculateWholesaleLabel)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "$resultLabel: Rp $totalBayar",
            style = MaterialTheme.typography.titleMedium
        )

        if (totalBayar > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val shareText = context.getString(R.string.share_message, totalBayar, jenisPembelian)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, shareText)
                    }
                    context.startActivity(Intent.createChooser(intent, "Bagikan via"))
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bagikan")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { navController.navigate("news") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(readArticleLabel)
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

            articles.forEach { (title, url) ->
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(title)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assesment1Theme {
        val navController = rememberNavController()
        MainScreen(navController = navController)
    }
}
