package com.daffa0050.assesment1.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.daffa0050.assesment1.R

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
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD7A86E), // Warna sama seperti MainScreen
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
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
