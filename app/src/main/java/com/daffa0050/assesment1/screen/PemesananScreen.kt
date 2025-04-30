package com.daffa0050.assesment1.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.daffa0050.assesment1.R
import com.daffa0050.assesment1.model.PemesananViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListPemesananScreen(navController: NavHostController, viewModel: PemesananViewModel = viewModel()) {
    val pemesananList by viewModel.allPemesanan.collectAsState()
    val totalEceran by viewModel.totalEceran.collectAsState()
    val totalGrosir by viewModel.totalGrosir.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.list_pemesanan_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
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
            if (pemesananList.isEmpty()) {
                Text(text = stringResource(id = R.string.list_pemesanan_empty))
            } else {
                Text("Total Eceran: Rp $totalEceran")
                Text("Total Grosir: Rp $totalGrosir")
                Spacer(modifier = Modifier.padding(8.dp))

                pemesananList.forEach {
                    Text("- ${it.nama}, ${it.jenis}, ${it.jumlahKg} kg, Rp ${it.totalHarga}")
                }
            }
        }
    }
}

