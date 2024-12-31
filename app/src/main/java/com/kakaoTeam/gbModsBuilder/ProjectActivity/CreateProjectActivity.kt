package com.kakaoTeam.gbModsBuilder.ProjectActivity

import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import coil.compose.AsyncImage
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class CreateProjectActivity : ComponentActivity() {
    private var selectedIconUri by mutableStateOf<Uri?>(null)

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedIconUri = it
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )

            CreateProjectScreen(
                onCreateProject = { name, description, label, color ->
                    createProject(name, description, label, color)
                },
                onSelectIcon = { selectIcon() }
            )
        }
    }

    @Composable
    fun CreateProjectScreen(
        onCreateProject: (String, String, String, Color) -> Unit,
        onSelectIcon: () -> Unit
    ) {
        var projectName by remember { mutableStateOf("") }
        var projectDescription by remember { mutableStateOf("") }
        var selectedLabel by remember { mutableStateOf("") }
        var selectedColor by remember { mutableStateOf(Color.Unspecified) }
        val context = LocalContext.current
        val isFormValid by remember(projectName, selectedColor) {
            derivedStateOf { projectName.isNotBlank() && selectedColor != Color.Unspecified }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF61AFEF).copy(alpha = 0.7f), Color(0xFF98C379).copy(alpha = 0.7f))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Create New Project",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = projectName,
                    onValueChange = { projectName = it },
                    label = { Text("Project Name", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                OutlinedTextField(
                    value = projectDescription,
                    onValueChange = { projectDescription = it },
                    label = { Text("Project Description", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Text(
                    "Select Project Label",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Game", "Tool", "Utility", "Education", "Entertainment", "Other")) { label ->
                        LabelButton(label, selectedLabel) { selectedLabel = label }
                    }
                }
                Text(
                    "Select Color Label",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(listOf(Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan)) { color ->
                        ColorButton(color, selectedColor) { selectedColor = color }
                    }
                }
                Button(
                    onClick = { onSelectIcon() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text("Choose Project Icon", fontSize = 18.sp, color = Color(0xFF61AFEF))
                }
                selectedIconUri?.let { uri ->
                    AsyncImage(
                        model = uri,
                        contentDescription = "Selected Icon",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(top = 16.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (isFormValid) {
                            onCreateProject(projectName, projectDescription, selectedLabel, selectedColor)
                        } else {
                            Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .alpha(if (isFormValid) 1f else 0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color.White else Color.Gray
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = isFormValid
                ) {
                    Text(
                        "Create Project",
                        fontSize = 18.sp,
                        color = if (isFormValid) Color(0xFF61AFEF) else Color.DarkGray
                    )
                }
            }
        }
    }

    @Composable
    fun LabelButton(label: String, selectedLabel: String, onSelect: (String) -> Unit) {
        Button(
            onClick = { onSelect(label) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (label == selectedLabel) Color.White else Color.Transparent,
                contentColor = if (label == selectedLabel) Color(0xFF61AFEF) else Color.White
            ),
            border = BorderStroke(1.dp, Color.White),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(label)
        }
    }

    @Composable
    fun ColorButton(color: Color, selectedColor: Color, onSelect: (Color) -> Unit) {
        Button(
            onClick = { onSelect(color) },
            modifier = Modifier
                .size(60.dp)
                .drawBehind {
                    if (color == selectedColor) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.5f), Color.Transparent),
                                center = center,
                                radius = 40.dp.toPx()
                            ),
                            radius = 40.dp.toPx()
                        )
                    }
                },
            colors = ButtonDefaults.buttonColors(containerColor = color),
            shape = RoundedCornerShape(8.dp),
            border = if (color == selectedColor) BorderStroke(2.dp, Color.White) else null
        ) {}
    }

    private fun createProject(name: String, description: String, label: String, color: Color) {
        val projectsDir = File(getExternalFilesDir(null), "projects")
        if (!projectsDir.exists()) {
            projectsDir.mkdirs()
        }

        val projectDir = File(projectsDir, name)
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }

        // Create Scripts folder
        val scriptsDir = File(projectDir, "Scripts")
        scriptsDir.mkdirs()

        // Create res folder
        File(projectDir, "res").mkdirs()

        // Create hook.ms file in the Scripts folder
        val hookFile = File(scriptsDir, "hook.txt")
        FileOutputStream(hookFile).use {
            it.write("// This is a hook file for your mod\n".toByteArray())
        }
        // Create info.json file
        val infoFile = File(projectDir, "info.json")
        val jsonObject = JSONObject().apply {
            put("name", name)
            put("description", description)
            put("label", label)
            put("color", String.format("#%06X", 0xFFFFFF and color.toArgb()))
            put("created_at", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        }
        FileOutputStream(infoFile).use {
            it.write(jsonObject.toString(4).toByteArray())
        }
        // Copy the selected icon to the project directory
        selectedIconUri?.let { uri ->
            val iconFile = File(projectDir, "icon.png")
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(iconFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        // Show success message
        Toast.makeText(this, "Project created successfully", Toast.LENGTH_SHORT).show()

        // Close the activity
        finish()
    }

    private fun selectIcon() {
        selectImageLauncher.launch("image/*")
    }
}