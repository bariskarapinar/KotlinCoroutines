package com.myapp.kotlincoroutines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoroutineDemoApp(viewModel: CoroutineViewModel = viewModel()) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Coroutines Academy", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                DemoCard(
                    title = "Basic Launch & Delay",
                    description = "Starts a coroutine that updates progress over time. Uses launch and delay.",
                    color = Color(0xFF6200EE)
                ) {
                    Column {
                        LinearProgressIndicator(
                            progress = { viewModel.launchProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.startLaunchDemo() },
                            enabled = !viewModel.isLaunching,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF6200EE))
                        ) {
                            Text(if (viewModel.isLaunching) "Running..." else "Start Launch")
                        }
                    }
                }
            }

            item {
                DemoCard(
                    title = "Parallel Async/Await",
                    description = "Fetches two values in parallel and awaits both. Much faster than sequential!",
                    color = Color(0xFF03DAC6)
                ) {
                    Column {
                        Text(viewModel.asyncResult, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.startAsyncDemo() },
                            enabled = !viewModel.isAsyncRunning,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black, contentColor = Color.White)
                        ) {
                            Text("Run Async")
                        }
                    }
                }
            }

            item {
                DemoCard(
                    title = "Dispatchers Playground",
                    description = "See which thread each dispatcher uses (Main, IO, Default).",
                    color = Color(0xFFFF0266)
                ) {
                    Column {
                        Text(viewModel.dispatcherInfo, color = Color.White, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.runDispatchersDemo() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFFF0266))
                        ) {
                            Text("Check Threads")
                        }
                    }
                }
            }

            item {
                val flowValue by viewModel.flowData.collectAsState(initial = 0)
                val bgColor by viewModel.colorFlow.collectAsState(initial = 0xFF6200EE)
                
                DemoCard(
                    title = "Kotlin Flow (Cold Stream)",
                    description = "A stream of values that updates the UI reactively.",
                    color = Color(bgColor)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Counter: $flowValue", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("Flowing...", color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            item {
                DemoCard(
                    title = "Timeout & Cancellation",
                    description = "Manual cancellation or automatic timeout after 2 seconds.",
                    color = Color(0xFFFFC107)
                ) {
                    Column {
                        Text(viewModel.timeoutStatus, color = Color.Black)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(onClick = { viewModel.startTimeoutDemo() }) { Text("Start") }
                            Button(onClick = { viewModel.cancelTimeoutDemo() }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Cancel") }
                        }
                    }
                }
            }

            item {
                DemoCard(
                    title = "Exception Handling",
                    description = "Safely catch errors using CoroutineExceptionHandler.",
                    color = Color(0xFFF44336)
                ) {
                    Column {
                        Text(viewModel.errorStatus, color = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.startExceptionDemo() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Red)
                        ) {
                            Text("Trigger Error")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DemoCard(
    title: String,
    description: String,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if (color.luminance() > 0.5f) Color.Black else Color.White)
            Text(description, fontSize = 14.sp, color = if (color.luminance() > 0.5f) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

fun Color.luminance(): Float {
    return 0.2126f * red + 0.7152f * green + 0.0722f * blue
}

@Preview(showBackground = true)
@Composable
fun DemoAppPreview() {
    CoroutineDemoApp()
}
