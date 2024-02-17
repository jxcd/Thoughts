package com.me.app.thoughts.compose

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

data class SelectOption<T>(
    val title: String,
    val value: T,
    val enable: Boolean = true,
)

@Composable
fun <T> Select(
    selected: T,
    onSelect: (T) -> Unit,
    options: Map<T, SelectOption<T>>,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    val title = options[selected]?.title ?: ""

    OutlinedButton(
        onClick = { expanded = !expanded },
        modifier = modifier,
    ) {
        Icon(
            if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
            contentDescription = null
        )
        Text(text = title)

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { (key, option) ->
                DropdownMenuItem(text = {
                    Text(
                        text = option.title,
                        color = if (key == selected) Color.Black else Color.Gray
                    )
                }, onClick = {
                    onSelect(key)
                    expanded = false
                })
            }
        }
    }

}