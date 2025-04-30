package com.daffa0050.assesment1.screen

import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.tooling.preview.Preview
import com.daffa0050.assesment1.AssesmentApp

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
                ),
                actions = {
                    var expanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.menu_list_pemesanan)) },
                            onClick = {
                                expanded = false
                                navController.navigate("list_pemesanan")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = stringResource(id = R.string.read_article)) },
                            onClick = {
                                expanded = false
                                navController.navigate("news")
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(modifier = Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(modifier: Modifier = Modifier) {
    val retailLabel = stringResource(id = R.string.retail)
    val wholesaleLabel = stringResource(id = R.string.wholesale)
    val inputKgLabel = stringResource(id = R.string.input_kg)
    val jenisPembelianLabel = stringResource(id = R.string.jenis_pembelian)
    val calculateLabel = stringResource(id = R.string.calculate)
    val calculateWholesaleLabel = stringResource(id = R.string.calculate_grosir)
    val selectWholesalePackageLabel = stringResource(id = R.string.select_wholesale_package)
    val selectWholesaleOptionLabel = stringResource(id = R.string.select_wholesale_option)
    val resultLabel = stringResource(id = R.string.result)
    val namaPembeliLabel = stringResource(id = R.string.buyer_name)
    val alamatPembeliLabel = stringResource(id = R.string.buyer_address)

    var showShareButton by rememberSaveable { mutableStateOf(false) }

    val selectLabel = stringResource(id = R.string.select)
    val jenisOptions = listOf(selectLabel, retailLabel, wholesaleLabel)
    val grosirOptions = listOf(selectWholesaleOptionLabel) + listOf(15, 30, 45, 60, 75, 90).map { "$it kg" }

    val hargaPerKg = 25000
    val grosirHargaPer15Kg = 330000

    var jenisPembelian by rememberSaveable { mutableStateOf(selectLabel) }
    var kg by rememberSaveable { mutableStateOf("") }
    var grosirKg by rememberSaveable { mutableStateOf(selectWholesaleOptionLabel) }
    var totalBayar by rememberSaveable { mutableIntStateOf(0) }
    var errorMessage by rememberSaveable { mutableStateOf("") }
    var expandedJenis by remember { mutableStateOf(false) }
    var expandedGrosir by remember { mutableStateOf(false) }

    var namaPembeli by rememberSaveable { mutableStateOf("") }
    var alamatPembeli by rememberSaveable { mutableStateOf("") }
    var namaPembeliError by rememberSaveable { mutableStateOf("") }
    var alamatPembeliError by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // Input Nama Pembeli
        OutlinedTextField(
            value = namaPembeli,
            onValueChange = {
                namaPembeli = it
                namaPembeliError = ""
            },
            label = { Text(namaPembeliLabel) },
            modifier = Modifier.fillMaxWidth(),
            isError = namaPembeliError.isNotEmpty(),
            supportingText = {
                if (namaPembeliError.isNotEmpty()) {
                    Text(text = namaPembeliError, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Input Alamat Pembeli
        OutlinedTextField(
            value = alamatPembeli,
            onValueChange = {
                alamatPembeli = it
                alamatPembeliError = ""
            },
            label = { Text(alamatPembeliLabel) },
            modifier = Modifier.fillMaxWidth(),
            isError = alamatPembeliError.isNotEmpty(),
            supportingText = {
                if (alamatPembeliError.isNotEmpty()) {
                    Text(text = alamatPembeliError, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Dropdown Jenis Pembelian
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
                            grosirKg = selectWholesaleOptionLabel
                            errorMessage = ""
                            showShareButton = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (jenisPembelian != selectLabel) {
            Image(
                painter = painterResource(id = if (jenisPembelian == wholesaleLabel) R.drawable.telurgrosir else R.drawable.telureceran),
                contentDescription = "Gambar Telur",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.current_price),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
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
        } else if (jenisPembelian == wholesaleLabel) {
            ExposedDropdownMenuBox(expanded = expandedGrosir, onExpandedChange = { expandedGrosir = !expandedGrosir }) {
                OutlinedTextField(
                    readOnly = true,
                    value = grosirKg,
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
                            text = { Text(kgOption) },
                            onClick = {
                                grosirKg = kgOption
                                expandedGrosir = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                namaPembeliError = if (namaPembeli.isEmpty()) context.getString(R.string.buyer_name_error) else ""
                alamatPembeliError = if (alamatPembeli.isEmpty()) context.getString(R.string.buyer_address_error) else ""

                if (namaPembeli.isNotEmpty() && alamatPembeli.isNotEmpty()) {
                    if (jenisPembelian == selectLabel) {
                        errorMessage = context.getString(R.string.select_purchase_type_error)
                        showShareButton = false
                        totalBayar = 0
                        return@Button
                    }

                    if (jenisPembelian == retailLabel) {
                        val kgInput = kg.toDoubleOrNull()
                        when {
                            kg.isEmpty() -> {
                                totalBayar = 0
                                errorMessage = context.getString(R.string.hitung_error)
                                showShareButton = false
                            }
                            kgInput == null || kgInput <= 0 || kg.startsWith("0") -> {
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
                    } else {
                        if (grosirKg == selectWholesaleOptionLabel) {
                            totalBayar = 0
                            errorMessage = context.getString(R.string.hitung_error)
                            showShareButton = false
                        } else {
                            val beratKg = grosirKg.split(" ")[0].toInt()
                            totalBayar = (beratKg / 15) * grosirHargaPer15Kg
                            showShareButton = true
                            errorMessage = ""
                        }
                    }
                }
            },
            enabled = jenisPembelian != selectLabel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (jenisPembelian == retailLabel) calculateLabel else calculateWholesaleLabel)
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
                    val shareText = try {
                        val jumlahKg = if (jenisPembelian == retailLabel) kg.toIntOrNull() ?: 0 else grosirKg.split(" ")[0].toInt()
                        context.getString(R.string.share_message, jenisPembelian, jumlahKg, totalBayar)
                    } catch (e: Exception) {
                        "Halo, ini pemesanan telur kamu:\nJenis: $jenisPembelian\nTotal harga: Rp $totalBayar"
                    }

                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, context.getString(R.string.share_button))
                    context.startActivity(shareIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.share_button))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}
@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun Assesment1AppPreviewLight() {
    AssesmentApp()
}

@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Composable
fun Assesment1AppPreviewDark() {
    AssesmentApp()
}