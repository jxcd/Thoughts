package com.me.app.thoughts.pages.thought

import androidx.compose.ui.graphics.Color
import com.me.app.thoughts.R

val DO_LIST_MAP = mapOf(
    "无" to "🎈",
    "休息" to "💤",
    "阅读" to "📖",
    "思考" to "✨",
    "美食" to "🍔",
    "运动" to "🚵‍♂️",
    "代码" to "👨‍💻",
    "视频" to "📺",
    "游戏" to "🎮",
    "购物" to "🛒",
)

fun doWhatIcon(doWhat: String): String = DO_LIST_MAP.getOrDefault(doWhat, "🎈")

fun sentimentIconId(level: Int) = when (level) {
    1 -> R.drawable.sentiment_very_dissatisfied
    2 -> R.drawable.sentiment_very_very_dissatisfied
    3 -> R.drawable.sentiment_dissatisfied
    4 -> R.drawable.sentiment_neutral
    5 -> R.drawable.sentiment_satisfied
    6 -> R.drawable.sentiment_satisfied_alt
    7 -> R.drawable.sentiment_very_satisfied
    else -> R.drawable.sentiment_neutral
}

fun sentimentColor(level: Int) = when (level) {
    1 -> Color(0xFF000000)
    2 -> Color(0xFF967BB6)
    3 -> Color(0xFFA5D6A7)
    4 -> Color(0xFF80DEEA)
    5 -> Color(0xFFD1C4E9)
    6 -> Color(0xFFEF9A9A)
    7 -> Color(0xFFF06292)
    else -> Color(0xFFB3E5FC)
}


