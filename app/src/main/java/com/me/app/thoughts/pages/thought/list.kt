package com.me.app.thoughts.pages.thought

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.me.app.thoughts.data.Thought
import com.me.app.thoughts.data.thoughtDao
import com.me.app.thoughts.util.TimeUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.TreeSet

@Composable
fun ThoughtList() {
    val list: List<Thought> = thoughtDao().flow().collectAsState(initial = emptyList()).value
    var groupByDate by remember { mutableStateOf(mapOf<LocalDate, Set<Thought>>()) }
    var today by remember { mutableStateOf(LocalDate.now()) }

    // 是否显示子项
    var displayChildren by remember { mutableStateOf(true) }

    // 是否按天显示
    var displayByDay by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = list.hashCode(), key2 = LocalDate.now(), key3 = displayByDay) {
        if (displayByDay) {
            val tempMap = LinkedHashMap<LocalDate, MutableSet<Thought>>()
            list.forEach {
                val date = it.time().toLocalDate()
                val set: MutableSet<Thought> =
                    tempMap.computeIfAbsent(date) { _ ->
                        TreeSet<Thought>(Comparator.comparingLong<Thought?> { i -> i.timestamp }
                            .reversed())
                    }
                set.add(it)
            }
            groupByDate = tempMap
            today = LocalDate.now()
        }
    }

    Row(
        modifier = Modifier.padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = if (displayByDay) "按天" else "按条")
        Switch(checked = displayByDay, onCheckedChange = { displayByDay = it })

        Text(text = if (displayChildren) "显示子项" else "隐藏子项")
        Switch(checked = displayChildren, onCheckedChange = { displayChildren = it })
    }

    LazyColumn() {
        if (displayByDay) {
            // 先不考虑性能问题, 后续需要通过分页按需加载, 但是问题是如何按天group后返回Flow
            items(items = groupByDate.keys.toList(), key = { it.toEpochDay() }) { date ->
                groupByDate[date]?.let {
                    ThoughtOnDay(
                        date = date,
                        thoughts = it,
                        today = today,
                        displayChildren = displayChildren,
                    )
                }
            }
            return@LazyColumn
        }

        items(items = list, key = { it.id }) {
            ThoughtItem(
                item = it,
                displayChildren = displayChildren
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThoughtOnDay(
    date: LocalDate,
    thoughts: Set<Thought>,
    today: LocalDate,
    displayChildren: Boolean
) {
    val recentDays = 3

    val mostLevel = mostLevel(thoughts)
    val defaultColor = sentimentColor(mostLevel)

    var show by remember { mutableStateOf(ChronoUnit.DAYS.between(date, today) < recentDays) }

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .fillMaxWidth(),
        border = BorderStroke(2.dp, defaultColor),
        onClick = { show = !show }
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            // horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${briefDate(date, today)} ${date.dayOfWeek}",
                    fontSize = 24.sp, fontWeight = FontWeight.Bold, color = defaultColor
                )

                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(id = sentimentIconId(mostLevel)),
                    contentDescription = null,
                    tint = sentimentColor(mostLevel)
                )
            }


            if (show) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp)
                        .height(2.dp)
                        .background(color = Color.LightGray)
                )
                thoughts.forEachIndexed { index, it ->
                    ThoughtOnDayItem(item = it, displayChildren = displayChildren)
                    if (index != thoughts.size - 1) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 2.dp)
                                .height(1.dp)
                                .background(color = sentimentColor(it.level))
                        )
                    }
                }
            }
        }


    }
}

private fun mostLevel(thoughts: Collection<Thought>) =
    thoughts.groupingBy { it.level }.eachCount().maxWithOrNull(compareBy(
        { it.value }, // 按出现次数排序
        { it.key }    // 如果次数相同，则按 level 排序
    ))?.key ?: 4

private fun briefDate(date: LocalDate, now: LocalDate = LocalDate.now()): String {
    if (date.year != now.year) {
        return date.toString()
    }
    return TimeUtil.formatMonthDay(date)

}


@Composable
private fun ThoughtOnDayItem(item: Thought, displayChildren: Boolean) {
    ThoughtItemMini(item = item)

    ThoughtSubs(pid = item.id, displayChildren = displayChildren)
}

@Composable
private fun ThoughtSubs(pid: Int, displayChildren: Boolean) {
    val scope = CoroutineScope(Dispatchers.IO)

    var subs: List<Thought> by remember { mutableStateOf(listOf()) }

    LaunchedEffect(key1 = displayChildren) {
        if (displayChildren) {
            scope.launch {
                subs = thoughtDao().listByPidAndTimestamp(pid)
            }
        } else {
            subs = listOf()
        }

    }

    if (subs.isNotEmpty()) {
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
                // .border(1.dp, color = sentimentColor(mostLevel(subs)))
        ) {
            subs.forEach {
                ThoughtItemMini(item = it)
            }
        }
    }

}

@Composable
private fun ThoughtItemMini(item: Thought) {
    Text(
        text = "${
            TimeUtil.formatHourMinute(
                item.time().toLocalTime()
            )
        } ${doWhatIcon(item.doWhat)}"
    )
    Row {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = sentimentIconId(item.level)),
            contentDescription = null,
            tint = sentimentColor(item.level)
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = item.message,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun ThoughtItem(item: Thought, displayChildren: Boolean) {
    val linesCount = item.message.lines().size
    val maxLinesDefault = 2

    var maxLines by remember { mutableIntStateOf(maxLinesDefault) }

    Card(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
        border = BorderStroke(2.dp, sentimentColor(item.level))
    ) {
        Text(
            // text = TimeUtil.format(item.timestamp),
            text = "${TimeUtil.format(item.timestamp)} ${doWhatIcon(item.doWhat)}",
            modifier = Modifier
                .height(20.dp)
                .padding(start = 16.dp, top = 4.dp),
            fontSize = 14.sp,
        )

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

        ThoughtSubs(pid = item.id, displayChildren = displayChildren)
    }
}