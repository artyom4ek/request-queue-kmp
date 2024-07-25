package io.day.requestqueuekmp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.day.requestqueuekmp.common.QueuePriority
import io.day.requestqueuekmp.data.repository.RequestQueueRepositoryImpl
import io.day.requestqueuekmp.ui.screen.RequestScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val requestQueueRepository = RequestQueueRepositoryImpl()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val highPriorityQueueSize = remember { mutableIntStateOf(0) }
                val lowPriorityQueueSize = remember { mutableIntStateOf(0) }
                val isConnectionAvailable = remember {
                    mutableStateOf(requestQueueRepository.getConnectionStatus())
                }

                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                requestQueueRepository.setOnQueueSizeChangedCallback { size, priority ->
                    when (priority) {
                        QueuePriority.HIGH -> highPriorityQueueSize.intValue = size
                        QueuePriority.LOW -> lowPriorityQueueSize.intValue = size
                    }
                }

                requestQueueRepository.setOnNetworkErrorCallback {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Long)
                    }
                }

                requestQueueRepository.setOnConnectionChangedCallback { status ->
                    isConnectionAvailable.value = status
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        RequestScreen(
                            isConnectionAvailable = isConnectionAvailable.value,
                            highPriorityQueueSize = highPriorityQueueSize.intValue,
                            lowPriorityQueueSize = lowPriorityQueueSize.intValue,
                            onAddHighPriorityRequest = {
                                val url = "https://day.io"
                                requestQueueRepository.addRequest(url, QueuePriority.HIGH)
                            },
                            onAddLowPriorityRequest = {
                                val url = "https://google.com"
                                requestQueueRepository.addRequest(url, QueuePriority.LOW)
                            }
                        )

                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    AppTheme {
        RequestScreen(true, 0, 0, {}, {})
    }
}
