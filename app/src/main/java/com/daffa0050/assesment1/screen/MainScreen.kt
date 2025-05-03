package com.daffa0050.assesment1.screen

import android.app.Application
import android.content.res.Configuration.*
import android.widget.Toast
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daffa0050.assesment1.AssesmentApp
import com.daffa0050.assesment1.model.Pemesanan
import com.daffa0050.assesment1.model.PemesananViewModel
import com.daffa0050.assesment1.model.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: PemesananViewModel = viewModel(factory = AppViewModelProvider(context))

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
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.menu_list_pemesanan)) },
                            onClick = {
                                expanded = false
                                navController.navigate("list_pemesanan")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.read_article)) },
                            onClick = {
                                expanded = false
                                navController.navigate("news")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.buat_transaksi_keuangan)) },
                            onClick = {
                                expanded = false
                                navController.navigate("keuangan")
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.keluar)) },
                            onClick = {
                                expanded = false
                                navController.navigate("welcome") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    viewModel: PemesananViewModel
) {
    val context = LocalContext.current
    val retailLabel = stringResource(R.string.retail)
    val wholesaleLabel = stringResource(R.string.wholesale)
    val selectLabel = stringResource(R.string.select)
    val selectWholesaleOptionLabel = stringResource(R.string.select_wholesale_option)

    val hargaPerKg = 25000
    val grosirHargaPer15Kg = 330000

    val jenisOptions = listOf(selectLabel, retailLabel, wholesaleLabel)
    val grosirOptions = listOf(selectWholesaleOptionLabel) + listOf(15, 30, 45, 60, 75, 90).map { "$it kg" }

    var jenisPembelian by rememberSaveable { mutableStateOf(selectLabel) }
    var kg by rememberSaveable { mutableStateOf("") }
    var grosirKg by rememberSaveable { mutableStateOf(selectWholesaleOptionLabel) }
    var totalBayar by rememberSaveable { mutableIntStateOf(0) }
    var errorMessage by rememberSaveable { mutableStateOf("") }

    var namaPembeli by rememberSaveable { mutableStateOf("") }
    var alamatPembeli by rememberSaveable { mutableStateOf("") }
    var namaPembeliError by rememberSaveable { mutableStateOf("") }
    var alamatPembeliError by rememberSaveable { mutableStateOf("") }

    var expandedJenis by remember { mutableStateOf(false) }
    var expandedGrosir by remember { mutableStateOf(false) }
    var showShareButton by rememberSaveable { mutableStateOf(false) }


    LaunchedEffect(kg, grosirKg, jenisPembelian) {
        totalBayar = when {
            jenisPembelian == retailLabel && kg.isNotEmpty() -> {
                val kgInput = kg.toDoubleOrNull()
                if (kgInput != null && kgInput > 0 && !kg.startsWith("0")) {
                    (kgInput * hargaPerKg).toInt()
                } else {
                    0
                }
            }
            jenisPembelian == wholesaleLabel && grosirKg != selectWholesaleOptionLabel -> {
                val beratKg = grosirKg.split(" ")[0].toInt()
                (beratKg / 15) * grosirHargaPer15Kg
            }
            else -> 0
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = namaPembeli,
            onValueChange = {
                namaPembeli = it
                namaPembeliError = ""
            },
            label = { Text(stringResource(R.string.buyer_name)) },
            modifier = Modifier.fillMaxWidth(),
            isError = namaPembeliError.isNotEmpty(),
            supportingText = {
                if (namaPembeliError.isNotEmpty()) {
                    Text(text = namaPembeliError, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = alamatPembeli,
            onValueChange = {
                alamatPembeli = it
                alamatPembeliError = ""
            },
            label = { Text(stringResource(R.string.buyer_address)) },
            modifier = Modifier.fillMaxWidth(),
            isError = alamatPembeliError.isNotEmpty(),
            supportingText = {
                if (alamatPembeliError.isNotEmpty()) {
                    Text(text = alamatPembeliError, color = MaterialTheme.colorScheme.error)
                }
            }
        )

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = expandedJenis, onExpandedChange = { expandedJenis = !expandedJenis }) {
            OutlinedTextField(
                readOnly = true,
                value = jenisPembelian,
                onValueChange = {},
                label = { Text(stringResource(R.string.jenis_pembelian)) },
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

        Spacer(Modifier.height(8.dp))

        if (jenisPembelian != selectLabel) {
            Image(
                painter = painterResource(id = if (jenisPembelian == wholesaleLabel) R.drawable.telurgrosir else R.drawable.telureceran),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (jenisPembelian == retailLabel) {
                    stringResource(id = R.string.current_price_eceran, hargaPerKg)
                } else {
                    stringResource(id = R.string.current_price_grosir, grosirHargaPer15Kg)
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

        }

        if (jenisPembelian == retailLabel) {
            OutlinedTextField(
                value = kg,
                onValueChange = {
                    kg = it
                    errorMessage = ""
                },
                label = { Text(stringResource(R.string.input_kg)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty(),
                supportingText = {
                    if (errorMessage.isNotEmpty()) {
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        } else if (jenisPembelian == wholesaleLabel) {
            ExposedDropdownMenuBox(expanded = expandedGrosir, onExpandedChange = { expandedGrosir = !expandedGrosir }) {
                OutlinedTextField(
                    readOnly = true,
                    value = grosirKg,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.select_wholesale_package)) },
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
        }

        Spacer(Modifier.height(12.dp))

        // Menampilkan total pembayaran dengan tampilan yang menonjol
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.result),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Rp $totalBayar",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                namaPembeliError = if (namaPembeli.isEmpty()) context.getString(R.string.buyer_name_error) else ""
                alamatPembeliError = if (alamatPembeli.isEmpty()) context.getString(R.string.buyer_address_error) else ""

                if (namaPembeli.isNotEmpty() && alamatPembeli.isNotEmpty()) {
                    if (jenisPembelian == selectLabel) {
                        errorMessage = context.getString(R.string.select_purchase_type_error)
                        return@Button
                    }

                    if (jenisPembelian == retailLabel) {
                        val kgInput = kg.toDoubleOrNull()
                        if (kgInput == null || kgInput <= 0 || kg.startsWith("0")) {
                            errorMessage = context.getString(R.string.invalid)
                            return@Button
                        }
                    } else {
                        if (grosirKg == selectWholesaleOptionLabel) {
                            errorMessage = context.getString(R.string.hitung_error)
                            return@Button
                        }
                    }

                    val pemesanan = Pemesanan(
                        nama = namaPembeli,
                        alamat = alamatPembeli,
                        jenis = if (jenisPembelian == retailLabel)
                            "Eceran"
                        else
                            "Grosir",
                        jumlahKg = if (jenisPembelian == retailLabel) kg.toDoubleOrNull()?.toInt() ?: 0 else grosirKg.split(" ")[0].toInt(),
                        totalHarga = totalBayar
                    )
                    viewModel.tambahPemesanan(pemesanan)

                    Toast.makeText(
                        context,
                        context.getString(R.string.order_success_message),
                        Toast.LENGTH_SHORT
                    ).show()

                    namaPembeli = ""
                    alamatPembeli = ""
                    kg = ""
                    grosirKg = selectWholesaleOptionLabel
                    jenisPembelian = selectLabel
                    totalBayar = 0
                    showShareButton = true
                }
            },
            enabled = jenisPembelian != selectLabel && totalBayar > 0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.calculate))
        }

        Spacer(Modifier.height(12.dp))
    }
}

@Preview(name = "Light Mode", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun Assesment1AppPreviewLight() {
    AssesmentApp()
}

@Preview(name = "Dark Mode", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun Assesment1AppPreviewDark() {
    AssesmentApp()
}