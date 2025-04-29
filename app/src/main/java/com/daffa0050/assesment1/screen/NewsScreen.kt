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
        context.getString(R.string.article_1) to "https://www.kompas.com/homey/read/2021/07/22/182625276/tips-menyimpan-telur-agar-lebih-tahan-lama#:~:text=Selalu%20simpan%20telur%20dengan%20sisi%20runcing%20menghadap%20ke%20bawah&text=Kantong%20ini%20terletak%20di%20ujung,area%20yang%20cukup%20kedap%20udara.",
        context.getString(R.string.article_2) to "https://www.detik.com/sumut/berita/d-7522609/4-manfaat-untuk-tubuh-jika-makan-telur-setiap-hari",
        context.getString(R.string.article_3) to "https://www.cnnindonesia.com/gaya-hidup/20231208181458-262-1034852/5-cara-menyimpan-telur-agar-awet",
        context.getString(R.string.article_4) to "https://www.dapurumami.com/artikel-tips/perbedaan-telur-ayam-kampung-dan-telur-ayam-negeri"
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

