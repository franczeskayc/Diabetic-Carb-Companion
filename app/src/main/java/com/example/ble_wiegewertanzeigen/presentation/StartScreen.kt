package com.example.ble_wiegewertanzeigen.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
@Preview
fun StartScreen(                                            //Das ist die Funktion des StartScreens, das den StartButton fürs Scanning beinhaltet
    navController: NavController
) {    
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Box(modifier = Modifier                             //Die Box ist die Begrenzung und beinhaltet einen clickable Button mit der Farbe blau
            .size(150.dp)
            .clip(CircleShape)
            .background(Color.Blue, CircleShape)
            .clickable {
                //Hier wird zu .... navigiert wenn draufgeklickt wird
                navController.navigate(Screen.WeightShowingScreen.route) {
                    popUpTo(Screen.StartScreen.route) {
                        inclusive = true
                    }
                }
            },
            contentAlignment = Alignment.Center
        ){
            Text(
                text = "Start",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}