package com.daffa0050.assesment1.screen

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.daffa0050.assesment1.R

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

    var showShareButton by rememberSaveable { mutableStateOf(false) }

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
                            showShareButton = false
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
                            showShareButton = false
                        }
                        kgInput == null || kgInput <= 0 -> {
                            totalBayar = 0
                            errorMessage = context.getString(R.string.invalid)
                            showShareButton = false
                        }
                        else -> {
                            totalBayar = (kgInput * hargaPerKg).toInt()
                            errorMessage = ""
                            showShareButton = true
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
                    showShareButton = true
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

        Spacer(modifier = Modifier.height(12.dp))

        if (showShareButton) {
            Button(
                onClick = {
                    val shareText = if (jenisPembelian == retailLabel) {
                        "Halo, ini pemesanan telur kamu:\nJenis: $jenisPembelian\nJumlah: $kg kg\nTotal harga: Rp $totalBayar"
                    } else {
                        "Halo, ini pemesanan pembelian telur kamu:\nJenis: $jenisPembelian\nJumlah: $grosirKg kg\nTotal harga: Rp $totalBayar"
                    }

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Bagikan via")
                    context.startActivity(shareIntent)
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
