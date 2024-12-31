package com.kakaoTeam.gbModsBuilder.ProjectActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kakaoTeam.gbModsBuilder.ui.theme.GBModsBuilderTheme
import org.json.JSONObject
import java.io.File
import androidx.compose.foundation.background

class OpenProjectsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GBModsBuilderTheme {
                OpenProjectsScreen(this)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenProjectsScreen(activity: ComponentActivity) {
    val projects = remember { mutableStateListOf<Project>() }

    LaunchedEffect(Unit) {
        val projectsDir = File(activity.getExternalFilesDir(null), "projects")
        if (projectsDir.exists() && projectsDir.isDirectory) {
            projectsDir.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    val infoFile = File(file, "info.json")
                    if (infoFile.exists()) {
                        try {
                            val jsonObject = JSONObject(infoFile.readText())
                            val name = jsonObject.getString("name")
                            val description = jsonObject.getString("description")
                            val label = jsonObject.getString("label")
                            val colorString = jsonObject.getString("color")
                            val color = Color(android.graphics.Color.parseColor(colorString))
                            projects.add(Project(name, description, label, color))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Open Project") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (projects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No projects found", fontSize = 18.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(projects) { project ->
                    ProjectItem(project)
                }
            }
        }
    }
}

@Composable
fun ProjectItem(project: Project) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(project.color)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(project.name, fontWeight = FontWeight.Bold)
                Text(project.description, fontSize = 14.sp, color = Color.Gray)
                Text(project.label, fontSize = 12.sp, color = Color.DarkGray)
            }
        }
    }
}

data class Project(
    val name: String,
    val description: String,
    val label: String,
    val color: Color
)