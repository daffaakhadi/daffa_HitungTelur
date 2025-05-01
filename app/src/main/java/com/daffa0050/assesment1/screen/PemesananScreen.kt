package com.daffa0050.assesment1.screen

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.daffa0050.assesment1.R
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
                    // List View
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(pemesananList) { item ->
                            PemesananItem(item = item, context = context)
                        }
                    }
                } else {
                    // Grid View
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pemesananList) { item ->
                            PemesananItem(item = item, context = context)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PemesananItem(item: com.daffa0050.assesment1.model.Pemesanan, context: android.content.Context) {
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
        }
    }
}