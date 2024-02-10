package com.me.app.thoughts.dto

import java.time.LocalDateTime

data class Thought(
    val id: Int,
    val level: Int,
    val doWhat: String,
    val message: String,
    val time: LocalDateTime,
)
