package com.kakaoTeam.gbModsBuilder.BlockEditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlin.random.Random

class BlockCodeEditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = false
                )
            }
            BlockCodeEditorTheme {
                BlockCodeEditorScreen()
            }
        }
    }
}

@Composable
fun BlockCodeEditorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockCodeEditorScreen() {
    var blocks by remember { mutableStateOf(listOf<CodeBlock>()) }
    var compiledCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Block Code Editor") },
                actions = {
                    IconButton(onClick = {
                        val (code, errors) = compileAndValidate(blocks)
                        if (errors.isEmpty()) {
                            compiledCode = code
                            errorMessage = ""
                        } else {
                            errorMessage = "Errors:\n${errors.joinToString("\n")}"}
                    }) {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Compile", tint = Color.Green)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF121212))
        ) {
            BlockPalette { newBlock ->
                blocks = blocks + newBlock.copy(id = generateShortId())
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(blocks) { block ->
                    CodeBlockItem(
                        block = block,
                        onBlockUpdated = { updatedBlock ->
                            blocks = blocks.map { if (it.id == block.id) updatedBlock else it }
                        },
                        onMoveUp = {
                            val index = blocks.indexOf(block)
                            if (index > 0) {
                                blocks = blocks.toMutableList().apply {
                                    removeAt(index)
                                    add(index - 1, block)
                                }
                            }
                        },
                        onMoveDown = {
                            val index = blocks.indexOf(block)
                            if (index < blocks.size - 1) {
                                blocks = blocks.toMutableList().apply {
                                    removeAt(index)
                                    add(index + 1, block)
                                }
                            }
                        },
                        onDelete = {
                            blocks = blocks.filter { it.id != block.id }
                        }
                    )
                }
            }
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, modifier = Modifier.padding(8.dp))
            }

            if (compiledCode.isNotEmpty()) {
                Text("Compiled Code:", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(8.dp))
                Text(compiledCode, color = Color.White, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@Composable
fun CodeBlockItem(
    block: CodeBlock,
    onBlockUpdated: (CodeBlock) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        color = block.color
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "(ID: ${block.id})",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
                Row {
                    IconButton(onClick = onMoveUp) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                    }
                    IconButton(onClick = onMoveDown) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            block.parameters.forEach { param ->
                TextField(
                    value = param.value,
                    onValueChange = { newValue ->
                        val updatedParams = block.parameters.map {
                            if (it == param) it.copy(value = newValue) else it
                        }
                        onBlockUpdated(block.copy(parameters = updatedParams))
                    },
                    label = { Text(param.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BlockPalette(onBlockSelected: (CodeBlock) -> Unit) {
    val categories = listOf("Variables", "Control Flow", "Output")

    var selectedCategory by remember { mutableStateOf(categories.first()) }
    Column(modifier = Modifier.fillMaxWidth()) {
        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(selectedCategory),
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0xFF1E1E1E),
            contentColor = Color.White
        ) {
            categories.forEach { category ->
                Tab(
                    selected = category == selectedCategory,
                    onClick = { selectedCategory = category },
                    text = { Text(category) }
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(8.dp)
        ) {
            items(getBlocksForCategory(selectedCategory)) { block ->
                Button(
                    onClick = { onBlockSelected(block) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = block.color)
                ) {
                    Text(block.name, color = Color.White)
                }
            }
        }
    }
}

data class CodeBlock(
    val id: String = generateShortId(),
    val name: String,
    val category: String,
    val color: Color,
    val parameters: List<BlockParameter> = emptyList(),
    val compile: (CodeBlock) -> String,
    val validate: (CodeBlock) -> List<String> = { emptyList() }
)

data class BlockParameter(
    val name: String,
    var value: String
)

fun generateShortId(): String {
    return Random.nextInt(1000, 9999).toString()
}

fun getBlocksForCategory(category: String): List<CodeBlock> {
    return when (category) {
        "Variables" -> listOf(
            CodeBlock(
                name = "Declare Variable",
                category = "Variables",
                color = Color(0xFF4CAF50),
                parameters = listOf(BlockParameter("Name", "x"), BlockParameter("Value", "5")),
                compile = { "${it.parameters[0].value} = ${it.parameters[1].value}" },
                validate = { block ->
                    val errors = mutableListOf<String>()
                    if (block.parameters[0].value.isBlank()) {
                        errors.add("Variable name cannot be empty")
                    }
                    if (!block.parameters[0].value.matches(Regex("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
                        errors.add("Invalid variable name")
                    }
                    errors
                }
            )
        )
        "Control Flow" -> listOf(
            CodeBlock(
                name = "If Statement",
                category = "Control Flow",
                color = Color(0xFFFFC107),
                parameters = listOf(BlockParameter("Condition", "x > 5")),
                compile = { "if ${it.parameters[0].value} then\nend if" },
                validate = { block ->
                    val errors = mutableListOf<String>()
                    if (block.parameters[0].value.isBlank()) {
                        errors.add("Condition cannot be empty")
                    }
                    if (!block.parameters[0].value.contains(Regex("[<>!=]"))) {
                        errors.add("Invalid condition: must include a comparison operator")
                    }
                    errors
                }
            ),
            CodeBlock(
                name = "For Loop",
                category = "Control Flow",
                color = Color(0xFFFFA000),
                parameters = listOf(BlockParameter("Variable", "i"), BlockParameter("Start", "0"), BlockParameter("End", "10")),
                compile = { "for ${it.parameters[0].value} in range(${it.parameters[1].value}, ${it.parameters[2].value})\nend for" },
                validate = { block ->
                    val errors = mutableListOf<String>()
                    if (block.parameters[0].value.isBlank()) {
                        errors.add("Loop variable name cannot be empty")
                    }
                    if (!block.parameters[1].value.matches(Regex("\\d+"))) {
                        errors.add("Start value must be a number")
                    }
                    if (!block.parameters[2].value.matches(Regex("\\d+"))) {
                        errors.add("End value must be a number")
                    }
                    errors
                }
            )
        )
        "Output" -> listOf(
            CodeBlock(
                name = "Print",
                category = "Output",
                color = Color(0xFF2196F3),
                parameters = listOf(BlockParameter("Message", "Hello, World!")),
                compile = { "print(${it.parameters[0].value})" },
                validate = { block ->
                    val errors = mutableListOf<String>()
                    if (block.parameters[0].value.isBlank()) {
                        errors.add("Print message cannot be empty")
                    }
                    errors
                }
            )
        )
        else -> emptyList()
    }
}

fun compileAndValidate(blocks: List<CodeBlock>): Pair<String, List<String>> {
    val compiledCode = StringBuilder()
    val errors = mutableListOf<String>()

    blocks.forEachIndexed { index, block ->
        val blockErrors = block.validate(block)
        if (blockErrors.isNotEmpty()) {
            errors.addAll(blockErrors.map { "Block ${index + 1} (${block.name}): $it" })
        } else {
            compiledCode.append(block.compile(block)).append("\n")
        }
    }

    return Pair(compiledCode.toString().trim(), errors)
}