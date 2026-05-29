package com.android.rickmortyandroid.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.rickmortyandroid.ui.theme.StatusAlive
import com.android.rickmortyandroid.ui.theme.StatusDead
import com.android.rickmortyandroid.ui.theme.StatusUnknown

@Composable
fun StatusIndicator(status: String, modifier: Modifier = Modifier) {
    val statusColor = when (status.lowercase()) {
        "alive" -> StatusAlive
        "dead" -> StatusDead
        else -> StatusUnknown
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color = statusColor, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = status,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}
