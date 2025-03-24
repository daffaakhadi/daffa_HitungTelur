package com.daffa0050.assesment1

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.daffa0050.assesment1.ui.theme.Assesment1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Assesment1Theme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFD7A86E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        ScreenContent(Modifier.padding(innerPadding))
    }
}

@Composable
fun ScreenContent(modifier: Modifier = Modifier) {
    var kg by remember { mutableStateOf("") }
    var totalBayar by remember { mutableIntStateOf(0) }
    val hargaPerKg = 25000

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = kg,
            onValueChange = { kg = it },
            label = { Text(stringResource(id = R.string.input_kg)) },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "${stringResource(id = R.string.price_per_kg)} Rp $hargaPerKg")

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val berat = kg.toDoubleOrNull() ?: 0.0
                totalBayar = (berat * hargaPerKg).toInt()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.calculate))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${stringResource(id = R.string.result)}: Rp $totalBayar",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun MainScreenPreview() {
    Assesment1Theme {
        MainScreen()
    }
}
