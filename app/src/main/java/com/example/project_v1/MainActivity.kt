package com.example.project_v1

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.activity.viewModels
import com.example.project_v1.viewmodel.MainViewModel
import com.example.project_v1.viewmodel.MainViewModelFactory
import com.example.project_v1.data.models.PostAddToInventoryResult
import com.example.project_v1.data.models.PostAddToInventoryResultDataClass
import com.example.project_v1.data.models.PostSendBarcodeResult
import com.example.project_v1.data.models.PostSendBarcodeResultDataClass
import com.example.project_v1.data.models.TestPostResult
import com.example.project_v1.ui.data.str
import com.example.project_v1.util.ImageUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.common.InputImage
import android.graphics.Bitmap
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val SCAN_TOP_RATIO = 0.2f
private const val SCAN_BOTTOM_RATIO = 0.6f


// private val repository: Repository
class MainActivity() : ComponentActivity() {

    var currentType: Int = Entity.TYPE_ADDRESS


/////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////// MODELE SIECI NEURONOWYCH
    private val mlExecutor = Executors.newSingleThreadExecutor()


    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(application)
    }

/////////////////////////////////////////////////////////////////////////////////////////////////////////



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



/////////////////////////////////////////////////////////////////////////////////////////////////////////

        // uzależnienie modeli od cyklu życia aplikacji
        // scanner initialized in CameraScreen

/////////////////////////////////////////////////////////////////////////////////////////////////////////

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = str.main.name) {
                composable(str.main.name) {
                    MainScreen(onClick = {
                        navController.navigate(it)
                    })
                }
                composable(str.settings.name) {
                    SettingsScreen(onClick = {
                        navController.navigate(
                            route = it,
                            navOptions = navOptions {
                                popUpTo(str.main.name) { inclusive = true}
                            })
                    })
                }
                composable(str.camera.name) {
                    CameraScreen(onClick = {
                        navController.navigate(
                            route = it,
                            navOptions = navOptions {
                                popUpTo(str.main.name) { inclusive = true}
                            })
                    })
                }
                composable(str.http_query.name) {
                    HttpQueryScreen(onClick = {
                        navController.navigate(
                            route = it,
                            navOptions = navOptions {
                                popUpTo(str.main.name) { inclusive = true}
                            })
                    })
                }
            }
        }
    }


    // WYŚWIETLANY TEKST W OKNIE KAMERY DO DETEKCJI
    @Composable
    fun DataPreview(modifier: Modifier = Modifier, date: String) {
        Card(
            modifier = modifier.wrapContentSize(),
            elevation = CardDefaults.elevatedCardElevation(5.dp)
        ) {
            Text(
                modifier = Modifier.padding(5.dp),
                text = date,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 25.sp
            )
        }

    }


    @Composable
    fun MainScreen(modifier: Modifier = Modifier, onClick: (String) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.DarkGray),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(160.dp))
            Text(text = "Ekran główny", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onClick(str.settings.name) }) {
                Text(text = "Ustawienia" )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onClick(str.http_query.name)}) {
                Text(text = "Test połączenia")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button( onClick = {
                        if (viewModel.settingDone.value == 1) {
                            onClick(str.camera.name)
                        } }
            ) {
                if (viewModel.settingDone.value == 1) {
                    Text(text = "Przejdź do skanera")
                } else {
                    Text(text = "SPRAWDŹ USTAWIENIA")
                }

            }
            Text(text = "Obecnie wybrany adres IP ${viewModel.baseURL.value}" , color = Color.White)
            Text(text = "Obecny użytkownik ${viewModel.appUsername.value}" , color = Color.White)
            Text(text = "Obecna inwentura ${viewModel.appFilename.value}" , color = Color.White)
        }
    }


    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun CameraScreen(modifier: Modifier = Modifier, onClick: (String) -> Unit) {
        val cameraExecutor = Executors.newSingleThreadExecutor()

        val imageAnalysis = ImageAnalysis.Builder().build()
        val preview = Preview.Builder().build()

        var screenStage by remember { mutableStateOf(0) }
        var setCurrentAmountStr by remember { mutableStateOf("0") }

        var resultJsonSendBarcodeResult by remember { mutableStateOf(
            PostSendBarcodeResultDataClass("","","","","","","")
        ) }

        var resultJsonAddToInventory by remember { mutableStateOf(
            PostAddToInventoryResultDataClass("","","","","","","", "")
        ) }

        var querySendBarcodeResult by remember { mutableStateOf("""{
                |"username": "",
                |"filename": "",
                |"barcode": "",
                |"barcode_recognized": "",
                |"already_knowed_barcode": "",
                |"product_name": "",
                |"amount": ""
                |}""".trimMargin() )}

        var queryAddToInventoryResult by remember { mutableStateOf("""{
                |"username": "",
                |"filename": "",
                |"barcode": "",
                |"barcode_recognized": "",
                |"already_knowed_barcode": "",
                |"amount": "",
                |"how_much_to_add": "",
                |"errorcode": ""
                |}""".trimMargin() )}


        val moshi = Moshi.Builder() .add(KotlinJsonAdapterFactory()).build()
        val adapterSB = moshi.adapter(PostSendBarcodeResult::class.java)
        val adapterATI = moshi.adapter(PostAddToInventoryResult::class.java)

        val lifecycleOwner = LocalLifecycleOwner.current
        val scanner = remember(viewModel.barcodeFormats.value) {
            val formats = viewModel.barcodeFormats.value.toIntArray()
            val builder = BarcodeScannerOptions.Builder()
            if (formats.isNotEmpty()) {
                val first = formats[0]
                val rest = if (formats.size > 1) formats.sliceArray(1 until formats.size) else intArrayOf()
                builder.setBarcodeFormats(first, *rest)
            }
            val options = builder
                .setExecutor(mlExecutor)
                .build()
            BarcodeScanning.getClient(options)
        }
        DisposableEffect(scanner) {
            lifecycleOwner.lifecycle.addObserver(scanner)
            onDispose { scanner.close() }
        }


        ////////////////////////////////////////////////////////////
        ///////////////// MODELE SIECI NEURONOWYCH /////////////////
        var model by rememberSaveable { mutableStateOf(true) }
        ////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////

        val cameraPermissionState = rememberMultiplePermissionsState(
            permissions = listOf(Manifest.permission.CAMERA)
        )
        if (cameraPermissionState.allPermissionsGranted && model) {
            Box(modifier = Modifier.fillMaxSize()) {
                var dataToPrint by remember {
                    mutableStateOf("")
                }

                if (screenStage == 0) {
                    val configuration = LocalConfiguration.current
                    val screenHeight = configuration.screenHeightDp.dp
                    val topOffset = screenHeight * SCAN_TOP_RATIO
                    val previewHeight = screenHeight * (SCAN_BOTTOM_RATIO - SCAN_TOP_RATIO)
                    Box(
                        modifier = Modifier
                            .offset(y = topOffset)
                            .fillMaxWidth()
                            .height(previewHeight)
                    ) {
                        CameraView(
                            imageAnalysis = imageAnalysis,
                            preview = preview,
                            executor = cameraExecutor,
                            scanner = scanner,
                            callable = { annotations ->
                                dataToPrint = annotations.toString()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }



                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ){
                    if (screenStage == 0){
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ){
                            Button(onClick = { onClick(str.main.name) },
                                modifier = Modifier
                                    .weight(1f)) {
                                Text(text = "Powrót do ekranu głównego")
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(onClick = {
                                if (dataToPrint != "") {
                                    val jsonData = """{"username": "${viewModel.appUsername.value}", "filename": "${viewModel.appFilename.value}", "barcode": "$dataToPrint"}"""
                                    //val jsonData = """{"username":"bb", "filename":"IWTEST", "barcode":"5900085010800"}"""
                                    val commandTestPOST = "send_barcode"
                                    viewModel.execPOST(jsonData,commandTestPOST) { response ->
                                        querySendBarcodeResult = response

                                        try {
                                            val responseObject = adapterSB.fromJson(querySendBarcodeResult)
                                            //// obiekt "responseObject" jest klasy JSON z wybranymi atrybutami
                                            if (responseObject != null) {
                                                resultJsonSendBarcodeResult.product_name = responseObject.product_name
                                                resultJsonSendBarcodeResult.amount = responseObject.amount
                                                resultJsonSendBarcodeResult.already_knowed_barcode = responseObject.already_knowed_barcode
                                                resultJsonSendBarcodeResult.barcode_recognized = responseObject.barcode_recognized
                                                screenStage = 1
                                            } else {
                                                resultJsonSendBarcodeResult.product_name = "Dostałęm NULL"
                                            }
                                        } catch (e: Exception) {
                                            resultJsonSendBarcodeResult.product_name = "Błąd pobierania nazwy produktu"
                                        }
                                    }


                                }  },

                                modifier = Modifier
                                    .weight(1f)) {
                                if (dataToPrint != "") {
                                    Text(text = "Zatwierdź kod", color = Color.Yellow)
                                   //Text(text = resultJsonSendBarcodeResult.product_name, color = Color.Yellow)
                                } else {
                                    Text(text = "Skanuj kod", color = Color.Yellow)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        DataPreview(date = dataToPrint)
                    }
                    else if (screenStage == 1){
                        var unknown_product_name  by remember { mutableStateOf("None") }
                        Spacer(modifier = Modifier.weight(1f))
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom) {

                            Column(modifier = Modifier,
                                    horizontalAlignment = Alignment.CenterHorizontally) {

                                if(resultJsonSendBarcodeResult.product_name == "Brak") {
                                    TextField(
                                        value = unknown_product_name,
                                        onValueChange = { unknown_product_name = it },
                                        singleLine = true,
                                        label = { Text("Nazwa nierozpoznanego produktu") },
                                        modifier = modifier
                                    )

                                }


                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Ile dodać szt. produktu do inwentury?", color = Color.Yellow)
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(modifier = Modifier
                                    .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Button(onClick = { setCurrentAmountStr = (setCurrentAmountStr.toInt() - 1).toString() },
                                        modifier = Modifier
                                            .weight(1f)
                                            ) {
                                        Text(text = "-")
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Button(onClick = { setCurrentAmountStr = (setCurrentAmountStr.toInt() + 1).toString() },
                                        modifier = Modifier
                                            .weight(1f)) {
                                        Text(text = "+")
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(modifier = Modifier
                                        .weight(1f)){
                                        TextField(
                                            value = setCurrentAmountStr,
                                            onValueChange = { setCurrentAmountStr = it },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = modifier
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                            ){
                                if(resultJsonSendBarcodeResult.product_name == "Brak"){
                                    Text(text = "Aktualna ilość: NIE ZNALEZIONO PRODUKTU W BAZIE DANYCH", color = Color.Green)
                                } else {
                                    Text(
                                        text = "Aktualna ilość: ${resultJsonSendBarcodeResult.amount}",
                                        color = Color.Green
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(IntrinsicSize.Min),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ){
                                Button(onClick = {
                                    screenStage = 0
                                },
                                    modifier = Modifier
                                        .weight(1f)) {
                                    Text(text = "Anuluj")
                                }
                                Spacer(modifier = Modifier.width(16.dp))

                                    Button(
                                        onClick = {
                                            val jsonData =
                                                """{"username": "${viewModel.appUsername.value}", "filename": "${viewModel.appFilename.value}", "barcode": "$dataToPrint", "already_knowed_barcode": "${resultJsonSendBarcodeResult.already_knowed_barcode}", "barcode_recognized": "${resultJsonSendBarcodeResult.barcode_recognized}", "how_much_to_add": "${setCurrentAmountStr}", "unknown_product_name": "${unknown_product_name}"}"""
                                            val commandTestPOST = "add_to_inventory"
                                            viewModel.execPOST(jsonData, commandTestPOST) { response ->
                                                queryAddToInventoryResult = response

                                                try {
                                                    val responseObject = adapterATI.fromJson(
                                                        queryAddToInventoryResult
                                                    )
                                                    //// obiekt "responseObject" jest klasy JSON z wybranymi atrybutami
                                                    if (responseObject != null) {
                                                        resultJsonAddToInventory.amount = responseObject.amount
                                                        resultJsonAddToInventory.how_much_to_add = responseObject.how_much_to_add
                                                        resultJsonAddToInventory.errorcode = responseObject.errorcode

                                                        screenStage = 2
                                                    } else {
                                                        resultJsonAddToInventory.errorcode =
                                                            "Dostałęm NULL"
                                                    }
                                                } catch (e: Exception) {
                                                    resultJsonAddToInventory.errorcode =
                                                        "Błąd pobierania nazwy produktu"
                                                }
                                            }

                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                    ) {
                                        Text(text = "Zatwierdź")
                                    }


                            }


                            Spacer(modifier = Modifier.height(16.dp))
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .height(128.dp)
                            ){
                                Column {
                                    Text(text = "Nazwa produktu:", color = Color.Green)
                                    Text(text = resultJsonSendBarcodeResult.product_name, color = Color.Green)
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }

                    } else if (screenStage == 2){
                        Spacer(modifier = Modifier.weight(1f))
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.DarkGray),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom) {

                            Text(text = "Pomyślnie dodano produkt do inwentury", color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Nazwa zmodyfikowanego produktu:", color = Color.White)
                            Text(text = resultJsonSendBarcodeResult.product_name, color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "Aktualna ilość w inwenturze: ${resultJsonAddToInventory.amount}", color = Color.White)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(text = "W tym dodano teraz: ${resultJsonAddToInventory.how_much_to_add}", color = Color.White)
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(onClick = {
                                screenStage = 0
                            },
                                modifier = Modifier
                                    .width(128.dp)
                                    .height(64.dp)
                            ) {
                                Text(text = "Powrót")
                            }
                            Spacer(modifier = Modifier.height(256.dp))
                        }

                    }

                }






            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.DarkGray),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(160.dp))
                Text(text = "Przedź do skanera", color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                if (!cameraPermissionState.allPermissionsGranted) {
                    Column {
                        Button(onClick = { cameraPermissionState.launchMultiplePermissionRequest() }) {
                            Text(text = "Zezwól na wykorzystanie kamery")
                        }
                    }
                }
            }
        }
    }



    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun SettingsScreen(modifier: Modifier = Modifier, onClick: (String) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.DarkGray),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var ip_address by remember { mutableStateOf(viewModel.ipSuffix.value) }
            var usernameTemp by remember { mutableStateOf(viewModel.appUsername.value) }
            var filenameTemp by remember { mutableStateOf(viewModel.appFilename.value) }
            var selectedFormats by remember { mutableStateOf(viewModel.barcodeFormats.value.toMutableSet()) }

            val allFormats = listOf(
                Barcode.FORMAT_CODE_128 to "Code 128",
                Barcode.FORMAT_CODE_39 to "Code 39",
                Barcode.FORMAT_CODE_93 to "Code 93",
                Barcode.FORMAT_CODABAR to "Codabar",
                Barcode.FORMAT_DATA_MATRIX to "Data Matrix",
                Barcode.FORMAT_EAN_13 to "EAN 13",
                Barcode.FORMAT_EAN_8 to "EAN 8",
                Barcode.FORMAT_ITF to "ITF",
                Barcode.FORMAT_QR_CODE to "QR Code",
                Barcode.FORMAT_UPC_A to "UPC A",
                Barcode.FORMAT_UPC_E to "UPC E",
                Barcode.FORMAT_PDF417 to "PDF417",
                Barcode.FORMAT_AZTEC to "Aztec"
            )

            Spacer(modifier = Modifier.height(160.dp))
            Text(text = "Wpisz adres IP (192.168.)xxx.xxx np. 0.186", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = ip_address,
                onValueChange = { ip_address = it },
                singleLine = true,
                label = { Text("xxx.xxx") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = usernameTemp,
                onValueChange = { usernameTemp = it },
                singleLine = true,
                label = { Text("Imię / Nazwa użytkownika") },
                modifier = modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = filenameTemp,
                onValueChange = { filenameTemp = it },
                singleLine = true,
                label = { Text("nazwa inwentury") },
                modifier = modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Typy kodów do wykrycia", color = Color.White)

            var expanded by remember { mutableStateOf(false) }
            val formatNames = allFormats.filter { selectedFormats.contains(it.first) }
                .joinToString { it.second }

            Box {
                TextField(
                    value = if (formatNames.isNotEmpty()) formatNames else "Wybierz formaty",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        androidx.compose.material3.IconButton(onClick = { expanded = true }) {
                            androidx.compose.material3.Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    allFormats.forEach { (format, label) ->
                        DropdownMenuItem(
                            onClick = {
                                selectedFormats = selectedFormats.toMutableSet().apply {
                                    if (contains(format)) remove(format) else add(format)
                                }
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedFormats.contains(format),
                                        onCheckedChange = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(label)
                                }
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onClick(str.main.name) }) {
                Text(text = "Powrót do ekranu głównego")
            }

            viewModel.updateSettings(ip_address, usernameTemp, filenameTemp, selectedFormats)

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Obecnie wybrany adres IP ${viewModel.baseURL.value}" , color = Color.White)
            Text(text = "Obecny użytkownik: ${viewModel.appUsername.value}" , color = Color.White)
            Text(text = "Obecna inwentura: ${viewModel.appFilename.value}" , color = Color.White)
        }
    }


    @Composable
    fun HttpQueryScreen(modifier: Modifier = Modifier, onClick: (String) -> Unit) {
        val moshi = Moshi.Builder() .add(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(TestPostResult::class.java)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.DarkGray),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(160.dp))
            Text(text = "Przetestuj połączenie z serwerem", color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onClick(str.main.name) }) {
                Text(text = "Powrót do ekranu głównego")
            }
            Spacer(modifier = Modifier.height(16.dp))

//            var queryText by remember { mutableStateOf(":(") }
            var queryText by remember { mutableStateOf("""{
                |"amount": "",
                |"code": "",
                |"mess": "",
                |"name": ""
                |}""".trimMargin() )}
            var querySetFile by remember { mutableStateOf(":(") }
            val jsonData = """{"code":"1011", "name":"produkt testowy", "amount":"4"}"""
            val commandTestPOST = "test_post"

            Button(onClick = { viewModel.execPOST(jsonData,commandTestPOST) { response ->
                queryText = response
                }
            }){
                Text(text = "TEST")
            }

            var messJson = "Empty"
            try {
                val responseObject = adapter.fromJson(queryText)
                if (responseObject != null) {
                    messJson = responseObject.mess
                }
            } catch (e: Exception) {
                messJson = "Error"
            }


            Text("HTTP Response: $messJson", color = Color.White)
            Text(viewModel.baseURL.value, color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))
            Text(text = "Utwórz plik inwentury", color = Color.White)
            if (viewModel.appFilename.value == "Brak aktywnej inwentury" || viewModel.appFilename.value == "") {
                Text(text = "Brak nazwy pliku inwentry, przejdź do ustawień", color = Color.White)
            } else {
                Text(text = "Wybrana nazwa inwentury: ${viewModel.appFilename.value}", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                val jsonData = """{"username":"${viewModel.appUsername.value}", "filename":"${viewModel.appFilename.value}"}"""
                val commandTestPOST = "set_file"

                Button(onClick = { viewModel.execPOST(jsonData,commandTestPOST) { response ->
                    querySetFile = response
                }
                }){
                    Text(text = "Utwórz plik")
                }
                Text(text = "Http respone: $querySetFile", color = Color.White)
            }
        }
    }



    // obsługa kamery i modelu detekcji tekstu
    @Composable
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    fun CameraView(
        imageAnalysis: ImageAnalysis,
        preview: Preview,
        executor: Executor,
        scanner: BarcodeScanner,
        callable: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {

        val context = LocalContext.current
        val lifecycle = LocalLifecycleOwner.current

        val previewView = remember {
            PreviewView(context)
        }

        LaunchedEffect(key1 = Unit) {

            val cameraProvider = ProcessCameraProvider.getInstance(context).await()
            val cameraSelector = CameraSelector.Builder().requireLensFacing(LENS_FACING_BACK).build()

            val useCaseGroup = UseCaseGroup.Builder()
                .addUseCase(imageAnalysis)
                .addUseCase(preview)
                .apply { previewView.viewPort?.let { setViewPort(it) } }
                .build()

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycle, cameraSelector, useCaseGroup)

            preview.setSurfaceProvider(previewView.surfaceProvider)

            // Przesyłanie klatek z kamery
            imageAnalysis.setAnalyzer(executor) { imageProxy ->
                val mediaImage = imageProxy.image

                if (mediaImage == null) {
                    imageProxy.close()
                    return@setAnalyzer
                }

                val bitmap = ImageUtils.imageProxyToBitmap(imageProxy)
                val rotated = ImageUtils.rotateBitmap(bitmap, imageProxy.imageInfo.rotationDegrees)
                val startY = (rotated.height * SCAN_TOP_RATIO).toInt()
                val endY = (rotated.height * SCAN_BOTTOM_RATIO).toInt()
                val cropHeight = endY - startY
                val cropped = if (cropHeight > 0 && startY + cropHeight <= rotated.height) {
                    Bitmap.createBitmap(rotated, 0, startY, rotated.width, cropHeight)
                } else {
                    rotated
                }

                val inputImage = InputImage.fromBitmap(cropped, 0)


/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////// TU PRZEKAZNANIE KLATKI OBRAZU DO PIERWSZEGO MODELU I NASTĘPNIE EKSTRAKCJA
                try {

                    val result = scanner.process(inputImage)
                        .addOnSuccessListener { barcodes ->
                            for (barcode in barcodes) {
                                val rawValue = barcode.getRawValue()
                                callable(rawValue.toString())
                            }
                        }
                        .addOnFailureListener {
                        }

                } catch (e: Exception) {
                    Log.e("DEBUG", "Task exc: ${e.message}")
                } finally {
                    imageProxy.close()
                }
/////////////////////////////////////////////////////////////////////////////////////////////////////////

            }
        }

        AndroidView(modifier = modifier.fillMaxSize(), factory = { previewView })
        // Połączenie composible z android view
    }






    //======================= POST FUN ==========================
    // Networking is now delegated to the ViewModel













}





