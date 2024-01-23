package com.example.ble_wiegewertanzeigen.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.ble_wiegewertanzeigen.data.ConnectionState
import com.example.ble_wiegewertanzeigen.presentation.permissions.PermissionUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.runtime.*
import kotlin.math.pow
import kotlin.math.roundToInt


@OptIn(ExperimentalPermissionsApi::class)
@Composable
@Preview
fun WeightShowingScreen(
    viewModel: WeightViewModel = hiltViewModel()

) {

    val permissionState = rememberMultiplePermissionsState(permissions = PermissionUtils.permissions)
    val lifecycleOwner = LocalLifecycleOwner.current
    val bleConnectionState = viewModel.connectionState

    var selectedFood by remember { mutableStateOf<String?>(null) }
    var currentWeight by remember { mutableStateOf(0) }

    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver{_,event ->
                if(event == Lifecycle.Event.ON_START){
                    permissionState.launchMultiplePermissionRequest()
                    if(permissionState.allPermissionsGranted && bleConnectionState == ConnectionState.Disconnected){
                        viewModel.reconnect()
                    }
                }
                if(event == Lifecycle.Event.ON_STOP){
                    if (bleConnectionState == ConnectionState.Connected){
                        viewModel.disconnect()
                    }
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )
    
    LaunchedEffect(key1 = permissionState.allPermissionsGranted){
        if(permissionState.allPermissionsGranted){
            if(bleConnectionState == ConnectionState.Uninitialized){
                viewModel.initializeConnection()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Box oben (Gewichtsanzeige)
        Column (
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(1f)
                .border(
                    BorderStroke(
                        5.dp, Color.Blue
                    ),
                    RoundedCornerShape(10.dp)
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            if(bleConnectionState == ConnectionState.CurrentlyInitializing){
                Column (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    CircularProgressIndicator()
                    if(viewModel.initializingMessage != null){
                        Text(
                            text = viewModel.initializingMessage!!
                        )
                    }
                }
            }else if(!permissionState.allPermissionsGranted){
                Text(
                    text = "Go to the app setting and allow the missing permissions.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }else if(viewModel.errorMessage != null){
                Column (
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = viewModel.errorMessage!!
                    )
                    Button(
                        onClick = {
                            if(permissionState.allPermissionsGranted){
                                viewModel.initializeConnection()
                            }
                        }
                    ) {
                        Text(
                            "Try again"
                        )

                    }
                }
            }else if(bleConnectionState == ConnectionState.Connected){
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text(
                        text = "Gewicht in Gramm: ${viewModel.weight}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        // Buttons in der Mitte
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Button für Weißbrot
            Button(
                onClick = {
                    selectedFood = "Weißbrot"
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Weißbrot")
            }

            // Button für Apfel
            Button(
                onClick = {
                    selectedFood = "Apfel"
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Apfel")
            }

            // Button für Banane
            Button(
                onClick = {
                    selectedFood = "Banane"
                },
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text("Banane")
            }

            // Button für Gewichtseingabe

        }

        // Text unten (nur das Ergebnis)
        val weightAsInt: Int? = viewModel.weight?.toIntOrNull()
        val interactiveText = when (selectedFood) {
            "Weißbrot" -> "Kohlenhydrate (Weißbrot): ${calculateCarbohydrates(selectedFood, weightAsInt)}g"
            "Apfel" -> "Kohlenhydrate (Apfel): ${calculateCarbohydrates(selectedFood, weightAsInt)}g"
            "Banane" -> "Kohlenhydrate (Banane): ${calculateCarbohydrates(selectedFood, weightAsInt)}g"
            else -> "Bitte wähle eines der Lebensmittel aus."
        }

        Text(
            text = interactiveText,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

}

fun calculateCarbohydrates(food: String?, weight: Int?): Double {
    // Logik zur Berechnung der Kohlenhydrate für jedes Lebensmittel unter Berücksichtigung des Gewichts

    val actualWeight = weight ?: 0
    var result = -1.0

    if (food != null) {
        if (food.toString() == "Weißbrot") {
            result = (50.0 / 100.0) * actualWeight.toDouble()
        }
        else if (food.toString() == "Apfel") {
            result = (14.4 / 100.0) * actualWeight.toDouble()
        }
        else if (food.toString() == "Banane"){
            result = (23.0 / 100.0) * actualWeight.toDouble()
        }
    }
    return result.takeIf { it >= 0 }?.round(2) ?: result
}

// Erweiterungsfunktion für die Rundung auf Dezimalstellen
fun Double.round(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToInt() / factor
}
