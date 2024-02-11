package com.me.app.thoughts.pages.thought

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
    val linesCount = item.message.lines().size
    val maxLinesDefault = 2
    var maxLines by remember { mutableIntStateOf(maxLinesDefault) }

    Card(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        border = BorderStroke(2.dp, sentimentColor(item.level))
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = TimeUtil.format(item.timestamp),
                // text = "${TimeUtil.format(item.timestamp)} ${doWhatIcon(item.doWhat)}",
                modifier = Modifier.height(20.dp),
                fontSize = 14.sp,
            )

            Text(text = doWhatIcon(item.doWhat), fontSize = 24.sp)
        }

        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier
                    .size(64.dp)
                    .clickable {
                        if (linesCount > maxLinesDefault) {
                            maxLines =
                                if (maxLines > maxLinesDefault) maxLinesDefault else Int.MAX_VALUE
                        }
                    },
                painter = painterResource(id = sentimentIconId(item.level)),
                contentDescription = null,
                tint = sentimentColor(item.level)
            )


            Text(
                text = item.message,
                fontSize = if (linesCount > 1) 16.sp else 32.sp,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}