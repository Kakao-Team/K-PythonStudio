package com.kakaoTeam.gbModsBuilder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kakaoTeam.gbModsBuilder.ui.theme.GBModsBuilderTheme
import com.kakaoTeam.gbModsBuilder.ProjectActivity.CreateProjectActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setContent {
            GBModsBuilderTheme {
                GBModsBuilderApp()
            }
        }
    }
}

fun navigateToCreateProject(context: Context) {
    val intent = Intent(context, CreateProjectActivity::class.java)
    context.startActivity(intent)
}

fun navigateToOpenProjects(context: Context) {

    val intent = Intent(context, com.kakaoTeam.gbModsBuilder.ProjectActivity.OpenProjectsActivity::class.java)
    context.startActivity(intent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GBModsBuilderApp() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val context = LocalContext.current

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
        onDispose {}
    }
    val gradientColor by animateColorAsState(
        targetValue = Color(0xFF98C379),
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF61AFEF), gradientColor),
                        start = Offset.Zero,
                        end = Offset.Infinite
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "//",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        ),
                        textAlign = TextAlign.Start,
                        modifier = Modifier.padding(start = 67.dp)
                    )
                    Text(
                        "GB Mods Builder",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  //  Image(
              //          painter = painterResource(id = R.drawable.logo),
                 //       contentDescription = "GB Mods Builder Icon",
                 //       modifier = Modifier
                 //           .size(240.dp)
                 //           .clip(RoundedCornerShape(16.dp))
                 //           .background(Color.White.copy(alpha = 0.2f))
                 //           .padding(16.dp)
                //    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Welcome to GoreBox Mods Builder!",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Create, Edit, and Build GB Mods",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White.copy(alpha = 0.8f)
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Button(
                        onClick = { navigateToCreateProject(context) },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("New Project", fontSize = 18.sp, color = Color(0xFF61AFEF))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navigateToOpenProjects(context) },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text("Open Project", fontSize = 18.sp, color = Color.White)
                    }
                }
            }
        }
    }
}