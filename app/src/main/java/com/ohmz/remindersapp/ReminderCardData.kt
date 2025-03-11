package com.ohmz.remindersapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ohmz.remindersapp.presentation.common.components.ReminderCardData

@Composable
fun ReminderCard(
    data: ReminderCardData, onClick: (ReminderCardData) -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp) // Adjust width/height to suit your layout
            .height(120.dp)
            .padding(8.dp)
            .clickable { onClick(data) },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = data.icon,
                contentDescription = data.title,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
                Text(
                    text = data.count.toString(),
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp)
                )
            }
        }
    }
}
