package com.me.app.thoughts.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.me.app.thoughts.R
import com.me.app.thoughts.pages.thought.AddThought
import com.me.app.thoughts.pages.thought.ThoughtList

private val pages = mapOf(
    "列表" to R.drawable.format_list_bulleted,
    "添加" to R.drawable.add_reaction,
    "分析" to R.drawable.add_reaction
)

@Composable
fun App() {
    var presses by remember { mutableStateOf("添加") }

    Scaffold(
        topBar = {},
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    pages.forEach { (name, icon) ->
                        ElevatedButton(
                            onClick = { presses = name },
                            modifier = Modifier.width(100.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = null
                                )
                                Text(text = name)
                            }
                        }
                    }
                }
            }
        },
    ) { paddingValues ->
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            shape = RectangleShape,
        ) {
            when (presses) {
                "列表" -> ThoughtList()
                else -> AddThought()
            }

        }
    }
}