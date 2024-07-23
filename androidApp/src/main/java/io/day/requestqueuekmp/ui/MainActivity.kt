package io.day.requestqueuekmp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.day.requestqueuekmp.common.QueuePriority
import io.day.requestqueuekmp.data.repository.RequestQueueRepositoryImpl
import io.day.requestqueuekmp.ui.screen.RequestScreen

class MainActivity : ComponentActivity() {
    private val requestQueueRepository = RequestQueueRepositoryImpl()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val highPriorityQueueSize = remember { mutableIntStateOf(0) }
                val lowPriorityQueueSize = remember { mutableIntStateOf(0) }

                requestQueueRepository.setOnQueueSizeChangedCallback { size, priority ->
                    when (priority) {
                        QueuePriority.HIGH -> highPriorityQueueSize.intValue = size
                        QueuePriority.LOW -> lowPriorityQueueSize.intValue = size
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RequestScreen(
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
                }
            }
        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    AppTheme {
        RequestScreen(0, 0, {}, {})
    }
}
