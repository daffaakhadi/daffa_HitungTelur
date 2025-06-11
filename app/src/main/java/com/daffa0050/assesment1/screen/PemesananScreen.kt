package com.daffa0050.assesment1.screen

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
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
import com.daffa0050.assesment1.network.TelurApiService
import com.daffa0050.assesment1.util.SettingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import coil.request.ImageRequest
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daffa0050.assesment1.model.UserData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPemesananScreen(
    navController: NavHostController,
    viewModel: PemesananViewModel = viewModel()
) {
    val dataStore = SettingsDataStore(LocalContext.current)
    val showList by dataStore.layoutFlow.collectAsState(true)
    val pemesananList by viewModel.pesananMilikUser.collectAsState()
    val totalEceran by viewModel.totalEceran.collectAsState()
    val totalGrosir by viewModel.totalGrosir.collectAsState()
    val status by viewModel.status.collectAsState()
    val context = LocalContext.current

    // Mengambil data user yang sedang login dari ViewModel.
    // Nama variabel di ViewModel sebaiknya 'currentUser' agar lebih jelas.
    val currentUser by viewModel.currentUserId.collectAsStateWithLifecycle()

    var expanded by remember { mutableStateOf(false) }

    // LaunchedEffect ini akan berjalan secara otomatis setiap kali
    // status login pengguna berubah (dari null ke login, atau sebaliknya).
    LaunchedEffect(currentUser) {
        val userId = currentUser?.email // Ganti .email sesuai nama properti di data class UserData

        // Hanya panggil sinkronisasi jika ada userId yang valid.
        if (!userId.isNullOrBlank()) {
            viewModel.sinkronisasi(userId = userId)
        }
    }

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
                    // Pastikan hanya user yang login yang bisa ke halaman home (tambah data)
                    if (currentUser != null) {
                        navController.navigate("home")
                    }
                },
                containerColor = Color(0xFFD7A86E),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Pesanan")
            }
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Cek kondisi login terlebih dahulu
            if (currentUser == null) {
                Text(
                    text = stringResource(id = R.string.login_required_message),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            } else {
                // Jika sudah login, tampilkan konten berdasarkan status
                when (status) {
                    TelurApiService.Companion.ApiStatus.LOADING -> {
                        CircularProgressIndicator()
                    }
                    TelurApiService.Companion.ApiStatus.ERROR -> {
                        Text(
                            text = stringResource(id = R.string.error_sync_message),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    TelurApiService.Companion.ApiStatus.SUCCESS -> {
                        if (pemesananList.isEmpty()) {
                            Text(text = stringResource(id = R.string.list_pemesanan_empty))
                        } else {
                            Column(modifier = Modifier.fillMaxSize()) {
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

            // Debug logging - lihat di Logcat
            Log.d("PemesananItem", "Raw eggImage: ${item.image}")

            val imageUrl = item.image


            // Tampilkan gambar dengan loading state dan error handling yang lebih baik
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray.copy(alpha = 0.1f))
            ) {
                if (imageUrl != null) {
                    Log.d("PemesananItem", "Attempting to load image: $imageUrl")

                    var isLoading by remember { mutableStateOf(true) }
                    var hasError by remember { mutableStateOf(false) }

                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(imageUrl.replace("http", "https"))
                            .crossfade(true)
                            .listener(
                                onStart = {
                                    Log.d("PemesananItem", "Image loading started")
                                    isLoading = true
                                    hasError = false
                                },
                                onSuccess = { _, _ ->
                                    Log.d("PemesananItem", "Image loaded successfully")
                                    isLoading = false
                                    hasError = false
                                },
                                onError = { _, error ->
                                    Log.e("PemesananItem", "Image load error: ${error.throwable.message}")
                                    isLoading = false
                                    hasError = true
                                }
                            )
                            .build(),
                        contentDescription = "Gambar Telur",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Loading indicator
                    if (isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Error overlay
                    if (hasError) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray.copy(alpha = 0.8f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Image Error",
                                    modifier = Modifier.size(32.dp),
                                    tint = Color.White
                                )
                                Text(
                                    text = "Gagal memuat gambar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                } else {
                    // Tampilkan jika tidak ada URL
                    Log.d("PemesananItem", "No valid image URL found")

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = "No Image",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Text(
                                text = "Gambar tidak tersedia",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Informasi pemesanan
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = stringResource(id = R.string.label_nama, item.customerName))
                Text(text = stringResource(id = R.string.label_alamat, item.customerAddress))
                Text(text = stringResource(id = R.string.label_jenis, item.purchaseType))
                Text(text = stringResource(id = R.string.label_jumlah, item.amount))
                Text(
                    text = stringResource(id = R.string.label_total_harga, item.total),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tombol aksi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
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
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(id = R.string.button_share))
                }

                Button(
                    onClick = {
                        navController.navigate("edit_pemesanan/${item.id}")
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
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
    // 1. Ambil data dari ViewModel. `initial = null` akan membuat 'pemesanan' null pada awalnya.
    val pemesanan by viewModel.getPemesananById(id).collectAsState(initial = null)
    val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_pemesanan_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup")
                    }
                }
            )
        }
    ) { innerPadding ->
        // 2. KONDISI UTAMA: Cek apakah data sudah siap atau masih null.
        if (pemesanan == null) {
            // Jika data belum siap, tampilkan loading indicator di tengah layar.
            // Ini mencegah form kosong ditampilkan, sehingga tidak ada kedip.
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            EditPemesananForm(
                modifier = Modifier.padding(innerPadding),
                pemesanan = pemesanan!!,
                currentUserId = currentUserId,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

@Composable
private fun EditPemesananForm(
    modifier: Modifier = Modifier,
    pemesanan: Pemesanan,
    currentUserId: UserData?,
    viewModel: PemesananViewModel,
    navController: NavHostController
) {
    // Semua state sekarang aman diinisialisasi dengan data dari 'pemesanan'
    // karena Composable ini hanya dipanggil jika 'pemesanan' tidak null.
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var nama by remember(pemesanan.customerName) { mutableStateOf(pemesanan.customerName) }
    var alamat by remember(pemesanan.customerAddress) { mutableStateOf(pemesanan.customerAddress) }
    val retailValue = "Eceran"
    val wholesaleValue = "Grosir"
    var jenis by remember(pemesanan.purchaseType) { mutableStateOf(pemesanan.purchaseType) }
    var jumlah by remember(pemesanan.amount) { mutableStateOf(pemesanan.amount.toString()) }

    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val grosirOptions = listOf(15, 30, 45, 60, 75, 90)
    var expandedJumlah by remember { mutableStateOf(false) }

    val currentUser by viewModel.currentUserId.collectAsStateWithLifecycle()
    var newImageBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            newImageBitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                ImageDecoder.createSource(context.contentResolver, it).let(ImageDecoder::decodeBitmap)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        newImageBitmap = bitmap
    }

    // Dialog konfirmasi hapus
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(id = R.string.delete_confirmation_title)) },
            text = { Text(stringResource(id = R.string.delete_confirmation_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = currentUser?.email
                        if (userId != null) {
                            viewModel.deletePemesanan(
                                userId = userId,
                                id = pemesanan.id.toString(),
                                onSuccess = {
                                    showDeleteDialog = false
                                    navController.popBackStack()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                    showDeleteDialog = false
                                }
                            )
                        } else {
                            Toast.makeText(context, "Gagal mendapatkan ID pengguna.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(id = R.string.confirm_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    // Tampilan Form
    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Gambar Produk", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (newImageBitmap != null) {
                        Image(
                            bitmap = newImageBitmap!!.asImageBitmap(),
                            contentDescription = "Pratinjau Gambar Baru",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else if (!pemesanan.image.isNullOrEmpty()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(pemesanan.image).crossfade(true).build(),
                            contentDescription = "Gambar Saat Ini",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Tidak ada gambar",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = { cameraLauncher.launch() }, modifier = Modifier.weight(1f)) {
                        Text("Kamera")
                    }
                    OutlinedButton(onClick = { galleryLauncher.launch("image/*") }, modifier = Modifier.weight(1f)) {
                        Text("Galeri")
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // 6. FORM INPUT TEKS

                errorMessage?.let {

                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)

                    Spacer(modifier = Modifier.height(8.dp))

                }

                OutlinedTextField(value = nama, onValueChange = { nama = it }, label = { Text("Nama Pembeli") }, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(value = alamat, onValueChange = { alamat = it }, label = { Text("Alamat Pembeli") }, modifier = Modifier.fillMaxWidth())



// ... (Dropdown untuk Jenis Pembelian dan Jumlah tidak diubah, sudah baik)

                Box {

                    OutlinedTextField(

                        value = if (jenis == retailValue) "Eceran" else "Grosir",

                        onValueChange = {},

                        label = { Text("Jenis Pembelian") },

                        modifier = Modifier.fillMaxWidth(),

                        readOnly = true,

                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) }

                    )

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {

                        DropdownMenuItem(text = { Text("Eceran") }, onClick = { jenis = retailValue; expanded = false })

                        DropdownMenuItem(text = { Text("Grosir") }, onClick = { jenis = wholesaleValue; expanded = false })

                    }

                    Spacer(modifier = Modifier.matchParentSize().clickable { expanded = true })

                }

                if (jenis == wholesaleValue) {

                    Box(

                        modifier = Modifier

                            .fillMaxWidth()

                    ) {

                        OutlinedTextField(

                            value = jumlah,

                            onValueChange = {},

                            label = { Text("Jumlah (Kg)") },

                            modifier = Modifier

                                .fillMaxWidth()

                                .clickable { expandedJumlah = true }, // Pastikan ini di luar .onValueChange

                            readOnly = true,

                            trailingIcon = {

                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)

                            }

                        )

                        DropdownMenu(

                            expanded = expandedJumlah,

                            onDismissRequest = { expandedJumlah = false },

                            modifier = Modifier.fillMaxWidth()

                        ) {

                            grosirOptions.forEach { option ->

                                DropdownMenuItem(

                                    text = { Text("$option Kg") },

                                    onClick = {

                                        jumlah = option.toString()

                                        expandedJumlah = false

                                    }

                                )

                            }

                        }

                    }

                } else {

                    OutlinedTextField(

                        value = jumlah,

                        onValueChange = { jumlah = it },

                        label = { Text("Jumlah (Kg)") },

                        modifier = Modifier.fillMaxWidth(),

                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

                    )

                }

            }

        }



// 7. TOMBOL AKSI (UPDATE & DELETE)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            Button(

                onClick = {

                    val jumlahInt = jumlah.toIntOrNull()

                    if (nama.isBlank() || alamat.isBlank() || jumlahInt == null || jumlahInt <= 0) {

                        errorMessage = "Semua field harus diisi dengan benar."

                        return@Button

                    }



                    val totalHarga = if (jenis == retailValue) {

                        jumlahInt * 25000

                    } else {

                        jumlahInt / 15 * 330000 // sudah aman karena dropdown hanya 15,30,...

                    }

                    val userId = currentUserId?.email

                    scope.launch {

                        if (userId != null) {

                            viewModel.updatePemesananWithImage(

                                id = pemesanan.id,

                                userId = userId,

                                customerName = nama,

                                customerAddress = alamat,

                                purchaseType = jenis,

                                amount = jumlahInt,

                                total = totalHarga,

                                image = newImageBitmap,

                                onSuccess = {

                                    Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()

                                    navController.popBackStack()

                                },

                                onError = { errorMsg ->

                                    errorMessage = errorMsg

                                }

                            )

                        }

                    }

                },

                modifier = Modifier

                    .fillMaxWidth()

                    .height(48.dp)

            ) {

                Text("Update Data")

            }



// Beri sedikit jarak antar tombol

            Spacer(modifier = Modifier.height(8.dp))



// Tombol untuk Hapus Data

            Button(

                onClick = { showDeleteDialog = true },

                modifier = Modifier.fillMaxWidth(),

                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)

            ) {

                Text("Hapus Data")

            }

        }

    }

}
