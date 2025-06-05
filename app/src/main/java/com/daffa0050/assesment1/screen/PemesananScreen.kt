package com.daffa0050.assesment1.screen

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.daffa0050.assesment1.R
import com.daffa0050.assesment1.model.Pemesanan
import com.daffa0050.assesment1.model.PemesananViewModel
import com.daffa0050.assesment1.util.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPemesananScreen(
    navController: NavHostController,
    viewModel: PemesananViewModel = viewModel()
) {
    val dataStore = SettingsDataStore(LocalContext.current)
    val showList by dataStore.layoutFlow.collectAsState(true)
    val pemesananList by viewModel.allPemesanan.collectAsState()
    val totalEceran by viewModel.totalEceran.collectAsState()
    val totalGrosir by viewModel.totalGrosir.collectAsState()
    val context = LocalContext.current

    var expanded by remember { mutableStateOf(false) }

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
                    IconButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStore.saveLayout(!showList)
                        }
                    }) {
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

                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD7A86E),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("home")
                },
                containerColor = Color(0xFFD7A86E),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Kembali ke Home")
            }
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

            if (!item.eggImage.isNullOrBlank()) {
                Log.d("UI", "eggImage yang akan ditampilkan: ${item.eggImage}")
                AsyncImage(
                    model = item.eggImage,
                    contentDescription = "Gambar Telur",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(text = stringResource(id = R.string.label_nama, item.customerName))
            Text(text = stringResource(id = R.string.label_alamat, item.customerAddress))
            Text(text = stringResource(id = R.string.label_jenis, item.purchaseType))
            Text(text = stringResource(id = R.string.label_jumlah, item.amount))
            Text(text = stringResource(id = R.string.label_total_harga, item.total))

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                val shareText = context.getString(
                    R.string.share_text,
                    item.customerName,
                    item.customerAddress,
                    item.purchaseType,
                    item.amount,
                    item.total.toString()
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

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                navController.navigate("edit_pemesanan/${item.id}")
            }) {
                Text("Edit")
            }
        }
    }
}


    @OptIn(ExperimentalMaterial3Api::class)
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
        val retailValue = "Eceran"
        val wholesaleValue = "Grosir"

        var jenis by remember { mutableStateOf(retailValue) }
        var jumlah by remember { mutableStateOf("") }

        var expanded by remember { mutableStateOf(false) }
        var weightExpanded by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<String?>(null) }
        val wholesaleWeights = listOf(15, 30, 45, 60, 75, 90)

        var showDeleteDialog by remember { mutableStateOf(false) }

        val buyerNameError = stringResource(R.string.buyer_name_error)
        val buyerAddressError = stringResource(R.string.buyer_address_error)
        val selectPurchaseTypeError = stringResource(R.string.select_purchase_type_error)
        val invalidError = stringResource(R.string.invalid)
        val buyerNameLabel = stringResource(R.string.buyer_name)
        val buyerAddressLabel = stringResource(R.string.buyer_address)
        val jenisPembelianLabel = stringResource(R.string.jenis_pembelian)
        val inputKgLabel = stringResource(R.string.input_kg)
        val updateButtonText = stringResource(R.string.update)
        val editPemesananTitle = stringResource(R.string.edit_pemesanan_title)
        val dropdownContentDesc = stringResource(R.string.dropdown_icon)
        val closeIconContentDesc = stringResource(R.string.close_icon)

        val deleteButtonText = stringResource(id = R.string.delete)
        val deleteConfirmationTitle = stringResource(id = R.string.delete_confirmation_title)
        val deleteConfirmationMessage = stringResource(id = R.string.delete_confirmation_message)
        val cancelText = stringResource(id = R.string.cancel)
        val confirmDeleteText = stringResource(id = R.string.confirm_delete)

        LaunchedEffect(pemesanan) {
            pemesanan?.let {
                nama = it.customerName
                alamat = it.customerAddress
                jenis = it.purchaseType
                jumlah = it.amount.toString()
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(deleteConfirmationTitle) },
                text = { Text(deleteConfirmationMessage) },
                confirmButton = {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.deletePemesanan(id)
                                showDeleteDialog = false
                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(confirmDeleteText)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(cancelText)
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(editPemesananTitle) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.Close, contentDescription = closeIconContentDesc)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {

                        errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        OutlinedTextField(
                            value = nama,
                            onValueChange = { nama = it },
                            label = { Text(buyerNameLabel) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = alamat,
                            onValueChange = { alamat = it },
                            label = { Text(buyerAddressLabel) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box {
                            OutlinedTextField(
                                value = if (jenis == retailValue) "Eceran" else "Grosir",
                                onValueChange = {},
                                label = { Text(jenisPembelianLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                readOnly = true
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Eceran") },
                                    onClick = {
                                        jenis = retailValue
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Grosir") },
                                    onClick = {
                                        jenis = wholesaleValue
                                        expanded = false
                                        if (jumlah.toIntOrNull() == null || jumlah.toIntOrNull()!! < 15) {
                                            jumlah = "15"
                                        }
                                    }
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { expanded = true }
                            )
                        }

                        if (jenis == wholesaleValue) {
                            Box {
                                OutlinedTextField(
                                    value = jumlah,
                                    onValueChange = { jumlah = it },
                                    label = { Text(inputKgLabel) },
                                    modifier = Modifier.fillMaxWidth(),
                                    readOnly = true,
                                    trailingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = dropdownContentDesc
                                        )
                                    }
                                )
                                DropdownMenu(
                                    expanded = weightExpanded,
                                    onDismissRequest = { weightExpanded = false }
                                ) {
                                    wholesaleWeights.forEach { weight ->
                                        DropdownMenuItem(
                                            text = { Text("$weight kg") },
                                            onClick = {
                                                jumlah = weight.toString()
                                                weightExpanded = false
                                            }
                                        )
                                    }
                                }
                                Spacer(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { weightExpanded = true }
                                )
                            }
                        } else {
                            OutlinedTextField(
                                value = jumlah,
                                onValueChange = { jumlah = it },
                                label = { Text(inputKgLabel) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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

                            val totalHarga = if (jenis == retailValue) jumlahInt * 25000
                            else (jumlahInt / 15) * 330000

                            val updated = Pemesanan(
                                id = id,
                                customerName = nama,
                                customerAddress = alamat,
                                purchaseType = jenis,
                                amount = jumlahInt,
                                total = totalHarga,
                                eggImage = null,
                                createdAt = null,
                                updatedAt = null
                            )


                            scope.launch {
                                viewModel.updatePemesanan(updated)
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(updateButtonText)
                    }

                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(deleteButtonText)
                    }
                }
            }
        }
    }