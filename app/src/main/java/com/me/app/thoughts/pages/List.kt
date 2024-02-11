package com.me.app.thoughts.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.me.app.thoughts.dto.Thought
import com.me.app.thoughts.dto.thoughtDao
import com.me.app.thoughts.util.TimeUtil

@Composable
fun ThoughtList() {
    val list: List<Thought> = thoughtDao().flow().collectAsState(initial = emptyList()).value

    LazyColumn {
        items(items = list, key = { it.id }) { ThoughtItem(item = it) }
    }

}

@Composable
private fun ThoughtItem(item: Thought) {
    Card(
        modifier = Modifier.padding(4.dp),
    ) {
        Text(
            text = TimeUtil.format(item.timestamp),
            modifier = Modifier.padding(start = 12.dp, top = 8.dp),
            fontSize = 10.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { }) {
                Text(text = doWhatIcon(item.doWhat), fontSize = 32.sp)
            }

            Text(
                text = item.message,
                fontSize = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}