package com.mycelengan.pages.Pengaturan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),    // vertikal saja, horizontal ikut parent
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
