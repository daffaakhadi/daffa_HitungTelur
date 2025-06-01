package com.daffa0050.assesment1.screen

import android.app.Application
import android.content.Context
import android.content.res.Configuration.*
import androidx.credentials.CredentialManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CustomCredential
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewmodel.compose.viewModel
import com.daffa0050.assesment1.AssesmentApp
import com.daffa0050.assesment1.BuildConfig
import com.daffa0050.assesment1.model.Pemesanan
import com.daffa0050.assesment1.model.PemesananViewModel
import com.daffa0050.assesment1.model.AppViewModelProvider
import com.daffa0050.assesment1.util.ColorManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import coil.compose.rememberAsyncImagePainter
import com.daffa0050.assesment1.model.UserData
import com.daffa0050.assesment1.util.SettingsDataStore
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


object PreferenceKeys {
    val THEME_COLOR = stringPreferencesKey("theme_color")
}

data class AppColorScheme(
    val primary: Color,
    val primaryContainer: Color,
    val onPrimary: Color,
    val secondary: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val onPrimaryContainer: Color,
    val error: Color,
    val onError: Color
)

val colorThemes = mapOf(
    "Coklat" to AppColorScheme(
        primary = Color(0xFFD7A86E),
        primaryContainer = Color(0xFFEFDCC9),
        onPrimary = Color.White,
        secondary = Color(0xFFB08E59),
        background = Color.White,
        surface = Color.White,
        onSurface = Color.Black,
        onPrimaryContainer = Color(0xFF553E1F),
        error = Color(0xFFB3261E),
        onError = Color.White
    ),
    "Merah" to AppColorScheme(
        primary = Color(0xFFD32F2F),
        primaryContainer = Color(0xFFFFDAD6),
        onPrimary = Color.White,
        secondary = Color(0xFFB71C1C),
        background = Color.White,
        surface = Color.White,
        onSurface = Color.Black,
        onPrimaryContainer = Color(0xFF410002),
        error = Color(0xFFB3261E),
        onError = Color.White
    ),
    "Hijau" to AppColorScheme(
        primary = Color(0xFF388E3C),
        primaryContainer = Color(0xFFADDAAF),
        onPrimary = Color.White,
        secondary = Color(0xFF1B5E20),
        background = Color.White,
        surface = Color.White,
        onSurface = Color.Black,
        onPrimaryContainer = Color(0xFF00210B),
        error = Color(0xFFB3261E),
        onError = Color.White
    ),
    "Biru" to AppColorScheme(
        primary = Color(0xFF1976D2),
        primaryContainer = Color(0xFFD4E3FF),
        onPrimary = Color.White,
        secondary = Color(0xFF0D47A1),
        background = Color.White,
        surface = Color.White,
        onSurface = Color.Black,
        onPrimaryContainer = Color(0xFF001A41),
        error = Color(0xFFB3261E),
        onError = Color.White
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavHostController) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: PemesananViewModel = viewModel(factory = AppViewModelProvider(context))
    val colorManager = remember { ColorManager(context) }
    val scope = rememberCoroutineScope()
    val dataStore = SettingsDataStore(context)
    val userData by dataStore.userFlow.collectAsState(UserData())
    var profilDialog by remember { mutableStateOf(false) }

    val currentThemeName = colorManager.themeColor.collectAsState(initial = "Coklat").value
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val currentColorScheme = colorThemes[currentThemeName] ?: colorThemes["Coklat"]!!

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = stringResource(id = R.string.keluar)) },
            text = { Text(text = stringResource(id = R.string.dialog_exit_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        navController.navigate("welcome") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.yes),
                        color = currentColorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text(
                        text = stringResource(id = R.string.cancel),
                        color = currentColorScheme.primary
                    )
                }
            },
            containerColor = currentColorScheme.surface,
            titleContentColor = currentColorScheme.onSurface,
            textContentColor = currentColorScheme.onSurface
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = currentColorScheme.primary,
                    titleContentColor = currentColorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = currentColorScheme.onPrimary
                        )
                    }
                    IconButton(
                        onClick = {
                            if (userData.email.isEmpty()) {
                                CoroutineScope(Dispatchers.IO).launch {
                                    signIn(context, dataStore)
                                }
                            } else {
                                profilDialog = true
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_account_circle_24),
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
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
                                showDialog = true
                            }
                        )
                        HorizontalDivider()
                        Text(
                            text = "Ganti Tema Warna",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                        colorThemes.forEach { (name, colorScheme) ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.baseline_color_lens_24),
                                            contentDescription = null,
                                            tint = colorScheme.primary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(name)
                                    }
                                },
                                onClick = {
                                    scope.launch {
                                        colorManager.saveThemePreference(name)
                                    }
                                    expanded = false
                                },
                                modifier = Modifier.background(
                                    if (name == currentThemeName) Color.LightGray else Color.Transparent
                                )
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        ScreenContent(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
            colorScheme = currentColorScheme
        )
    }

    // 💬 Profil Dialog muncul di tengah
    if (profilDialog) {
        AlertDialog(
            onDismissRequest = { profilDialog = false },
            title = { Text(text = stringResource(id = R.string.profil)) },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(userData.photoUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(userData.name, fontWeight = FontWeight.Bold)
                    Text(userData.email)
                }
            },
            confirmButton = {
                TextButton(onClick = { profilDialog = false }) {
                    Text("Tutup", color = currentColorScheme.primary)
                }
            },
            containerColor = currentColorScheme.surface,
            titleContentColor = currentColorScheme.onSurface,
            textContentColor = currentColorScheme.onSurface
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContent(
    modifier: Modifier = Modifier,
    viewModel: PemesananViewModel,
    colorScheme: AppColorScheme
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
                    Text(text = namaPembeliError, color = colorScheme.error)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                focusedLabelColor = colorScheme.primary,
                cursorColor = colorScheme.primary
            )
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
                    Text(text = alamatPembeliError, color = colorScheme.error)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                focusedLabelColor = colorScheme.primary,
                cursorColor = colorScheme.primary
            )
        )

        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = expandedJenis, onExpandedChange = { expandedJenis = !expandedJenis }) {
            OutlinedTextField(
                readOnly = true,
                value = jenisPembelian,
                onValueChange = {},
                label = { Text(stringResource(R.string.jenis_pembelian)) },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expandedJenis
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary,
                    unfocusedBorderColor = colorScheme.primary.copy(alpha = 0.5f)
                )
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
                modifier = Modifier.padding(bottom = 4.dp),
                color = colorScheme.primary
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
                        Text(text = errorMessage, color = colorScheme.error)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorScheme.primary,
                    focusedLabelColor = colorScheme.primary,
                    cursorColor = colorScheme.primary
                )
            )
        } else if (jenisPembelian == wholesaleLabel) {
            ExposedDropdownMenuBox(expanded = expandedGrosir, onExpandedChange = { expandedGrosir = !expandedGrosir }) {
                OutlinedTextField(
                    readOnly = true,
                    value = grosirKg,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.select_wholesale_package)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expandedGrosir
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colorScheme.primary,
                        focusedLabelColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.primary.copy(alpha = 0.5f)
                    )
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

        Card(
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.primaryContainer
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
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Rp $totalBayar",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorScheme.onPrimaryContainer
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
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary,
                contentColor = colorScheme.onPrimary,
                disabledContainerColor = colorScheme.primary.copy(alpha = 0.5f),
                disabledContentColor = colorScheme.onPrimary.copy(alpha = 0.5f)
            )
        ) {
            Text(stringResource(R.string.calculate))
        }

        Spacer(Modifier.height(12.dp))
    }
}
suspend fun signIn(context: Context, dataStore: SettingsDataStore) {
    val googleOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}
suspend fun signOut(context: Context, dataStore: SettingsDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(UserData())
    } catch (e: ClearCredentialException){
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}
suspend fun handleSignIn(result: GetCredentialResponse, dataStore: SettingsDataStore) {
    val credential = result.credential
    if (credential is CustomCredential &&
        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.id
            val photoUrl = googleId.profilePictureUri.toString()
            dataStore.saveData(UserData(nama, email, photoUrl))
        } catch (e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN", "Error: ${e.message}")
        }
    } else {
        Log.e("SIGN-IN", "Error: unrecognized custom credential type.")
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