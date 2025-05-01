package com.daffa0050.assesment1.screen

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.daffa0050.assesment1.R
import com.daffa0050.assesment1.model.Pemesanan
import com.daffa0050.assesment1.model.PemesananViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPemesananScreen(
    navController: NavHostController,
    viewModel: PemesananViewModel = viewModel()
) {
    var showList by remember { mutableStateOf(true) }
    val pemesananList by viewModel.allPemesanan.collectAsState()
    val totalEceran by viewModel.totalEceran.collectAsState()
    val totalGrosir by viewModel.totalGrosir.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.list_pemesanan_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showList = !showList }) {
                        Icon(
                            painter = painterResource(
                                if (showList) R.drawable.baseline_grid_view_24
                                else R.drawable.baseline_list_24
                            ),
                            contentDescription = stringResource(
                                if (showList) R.string.grid
                                else R.string.list
                            ),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD7A86E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (pemesananList.isEmpty()) {
                Text(text = stringResource(id = R.string.list_pemesanan_empty))
            } else {
                Text(text = stringResource(id = R.string.total_eceran, totalEceran))
                Text(text = stringResource(id = R.string.total_grosir, totalGrosir))
                Spacer(modifier = Modifier.height(16.dp))

                if (showList) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(pemesananList) { item ->
                            PemesananItem(item = item, context = context, navController = navController)
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pemesananList) { item ->
                            PemesananItem(item = item, context = context, navController = navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PemesananItem(
    item: Pemesanan,
    context: Context,
    navController: NavHostController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(id = R.string.label_nama, item.nama))
            Text(text = stringResource(id = R.string.label_alamat, item.alamat))
            Text(text = stringResource(id = R.string.label_jenis, item.jenis))
            Text(text = stringResource(id = R.string.label_jumlah, item.jumlahKg))
            Text(text = stringResource(id = R.string.label_total_harga, item.totalHarga))

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val shareText = context.getString(
                    R.string.share_text,
                    item.nama,
                    item.alamat,
                    item.jenis,
                    item.jumlahKg,
                    item.totalHarga.toString()
                )
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                context.startActivity(
                    Intent.createChooser(intent, context.getString(R.string.share_title))
                )
            }) {
                Text(stringResource(id = R.string.button_share))
            }

            Button(onClick = {
                navController.navigate("edit_pemesanan/${item.id}")
            }) {
                Text("Edit")
            }
        }
    }
}

@Composable
fun EditPemesananScreen(
    navController: NavHostController,
    id: Int,
    viewModel: PemesananViewModel = viewModel()
) {
    val pemesanan by viewModel.getPemesananById(id).collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    var nama by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var jenis by remember { mutableStateOf("eceran") }
    var jumlah by remember { mutableStateOf("") }

    var expanded by remember { mutableStateOf(false) }
    var weightExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    val wholesaleWeights = listOf(15, 30, 45, 60, 75, 90)

    val buyerNameError = stringResource(R.string.buyer_name_error)
    val buyerAddressError = stringResource(R.string.buyer_address_error)
    val selectPurchaseTypeError = stringResource(R.string.select_purchase_type_error)
    val invalidError = stringResource(R.string.invalid)

    LaunchedEffect(pemesanan) {
        pemesanan?.let {
            nama = it.nama
            alamat = it.alamat
            jenis = it.jenis
            jumlah = it.jumlahKg.toString()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_pemesanan_title),
            style = MaterialTheme.typography.titleLarge
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text(stringResource(R.string.buyer_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = alamat,
            onValueChange = { alamat = it },
            label = { Text(stringResource(R.string.buyer_address)) },
            modifier = Modifier.fillMaxWidth()
        )

        Box {
            OutlinedTextField(
                value = if (jenis == "eceran") stringResource(R.string.retail) else stringResource(R.string.wholesale),
                onValueChange = {},
                label = { Text(stringResource(R.string.jenis_pembelian)) },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.retail)) },
                    onClick = {
                        jenis = "eceran"
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.wholesale)) },
                    onClick = {
                        jenis = "grosir"
                        expanded = false
                        // Reset jumlah to minimum wholesale value if switching to wholesale
                        if (jumlah.toIntOrNull() == null || jumlah.toIntOrNull()!! < 15) {
                            jumlah = "15"
                        }
                    }
                )
            }
            Spacer(modifier = Modifier
                .matchParentSize()
                .clickable { expanded = true })
        }
        if (jenis == "grosir") {
            Box {
                OutlinedTextField(
                    value = jumlah,
                    onValueChange = { jumlah = it },
                    label = { Text(stringResource(R.string.input_kg)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
                DropdownMenu(
                    expanded = weightExpanded,
                    onDismissRequest = { weightExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    wholesaleWeights.forEach { weight ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    "$weight kg",
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                jumlah = weight.toString()
                                weightExpanded = false
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier
                    .matchParentSize()
                    .clickable { weightExpanded = true })
            }
        } else {
            OutlinedTextField(
                value = jumlah,
                onValueChange = { jumlah = it },
                label = { Text(stringResource(R.string.input_kg)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Button(
            onClick = {
                if (nama.isBlank()) {
                    errorMessage = buyerNameError
                    return@Button
                }
                if (alamat.isBlank()) {
                    errorMessage = buyerAddressError
                    return@Button
                }
                if (jenis.isBlank()) {
                    errorMessage = selectPurchaseTypeError
                    return@Button
                }
                val jumlahInt = jumlah.toIntOrNull()
                if (jumlahInt == null || jumlahInt <= 0) {
                    errorMessage = invalidError
                    return@Button
                }

                val totalHarga = if (jenis == "eceran") jumlahInt * 23000
                else (jumlahInt / 15) * 330000

                val updated = Pemesanan(
                    id = id,
                    nama = nama,
                    alamat = alamat,
                    jenis = jenis,
                    jumlahKg = jumlahInt,
                    totalHarga = totalHarga
                )

                scope.launch {
                    viewModel.updatePemesanan(updated)
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.update))
        }
    }
}



