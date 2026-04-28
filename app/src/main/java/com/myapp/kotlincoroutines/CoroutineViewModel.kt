package com.myapp.kotlincoroutines

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class CoroutineViewModel : ViewModel() {

    // --- Demo 1: Basic Launch ---
    var launchProgress by mutableStateOf(0f)
        private set
    var isLaunching by mutableStateOf(false)
        private set

    fun startLaunchDemo() {
        if (isLaunching) return
        isLaunching = true
        launchProgress = 0f
        viewModelScope.launch {
            for (i in 1..100) {
                delay(20) // Simulate work
                launchProgress = i / 100f
            }
            isLaunching = false
        }
    }

    // --- Demo 2: Async/Await ---
    var asyncResult by mutableStateOf("Ready to fetch...")
        private set
    var isAsyncRunning by mutableStateOf(false)
        private set

    fun startAsyncDemo() {
        if (isAsyncRunning) return
        isAsyncRunning = true
        asyncResult = "Fetching from two sources..."
        viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val deferred1 = async { fetchDataFromSource1() }
            val deferred2 = async { fetchDataFromSource2() }
            
            val res1 = deferred1.await()
            val res2 = deferred2.await()
            
            val endTime = System.currentTimeMillis()
            asyncResult = "$res1 & $res2\n(Took ${endTime - startTime}ms)"
            isAsyncRunning = false
        }
    }

    private suspend fun fetchDataFromSource1(): String {
        delay(1000)
        return "Source A"
    }

    private suspend fun fetchDataFromSource2(): String {
        delay(1000)
        return "Source B"
    }

    // --- Demo 3: Dispatchers ---
    var dispatcherInfo by mutableStateOf("Tap to run on different threads")
        private set

    fun runDispatchersDemo() {
        viewModelScope.launch {
            val mainThread = Thread.currentThread().name
            val ioResult = withContext(Dispatchers.IO) {
                val ioThread = Thread.currentThread().name
                "Running on: $ioThread"
            }
            val defaultResult = withContext(Dispatchers.Default) {
                val defaultThread = Thread.currentThread().name
                "Running on: $defaultThread"
            }
            dispatcherInfo = "Main: $mainThread\nIO: $ioResult\nDefault: $defaultResult"
        }
    }

    // --- Demo 4: Timeout & Cancellation ---
    var timeoutStatus by mutableStateOf("Task not started")
        private set
    private var cancellableJob: Job? = null

    fun startTimeoutDemo() {
        cancellableJob?.cancel()
        timeoutStatus = "Running (will timeout in 2s)..."
        cancellableJob = viewModelScope.launch {
            try {
                withTimeout(2000) {
                    repeat(10) { i ->
                        timeoutStatus = "Processing step $i..."
                        delay(500)
                    }
                    timeoutStatus = "Completed successfully!"
                }
            } catch (e: TimeoutCancellationException) {
                timeoutStatus = "Timed out after 2 seconds!"
            } catch (e: CancellationException) {
                timeoutStatus = "Task was cancelled!"
            }
        }
    }

    fun cancelTimeoutDemo() {
        cancellableJob?.cancel()
    }

    // --- Demo 5: Exception Handling ---
    var errorStatus by mutableStateOf("No errors yet")
        private set

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        errorStatus = "Caught: ${exception.localizedMessage}"
    }

    fun startExceptionDemo() {
        errorStatus = "Triggering risky task..."
        viewModelScope.launch(exceptionHandler) {
            delay(500)
            throw RuntimeException("Something went wrong!")
        }
    }

    // --- Demo 6: Kotlin Flow ---
    val flowData: Flow<Int> = flow {
        var count = 0
        while (true) {
            emit(count++)
            delay(1000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val colorFlow: Flow<Long> = flow {
        while (true) {
            emit(Random.nextLong(0xFF000000, 0xFFFFFFFF))
            delay(2000)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0xFF6200EE)
}
