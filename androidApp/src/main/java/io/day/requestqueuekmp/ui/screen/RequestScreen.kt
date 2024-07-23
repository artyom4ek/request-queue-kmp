package io.day.requestqueuekmp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RequestScreen(
    highPriorityQueueSize: Int,
    lowPriorityQueueSize: Int,
    onAddHighPriorityRequest: () -> Unit,
    onAddLowPriorityRequest: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            onAddHighPriorityRequest()
        }) {
            Text(text = "Add High Priority Request")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "High Priority Queue Size: $highPriorityQueueSize")

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            onAddLowPriorityRequest()
        }) {
            Text(text = "Add Low Priority Request")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Low Priority Queue Size: $lowPriorityQueueSize")
    }
}