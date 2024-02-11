package com.me.app.thoughts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.me.app.thoughts.dto.AppDatabase
import com.me.app.thoughts.dto.thoughtDao
import com.me.app.thoughts.pages.ThoughtList
import com.me.app.thoughts.ui.theme.ThoughtsTheme

class MainActivity : ComponentActivity() {
    private val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thoughtDao = { database.thoughtDao() }

        setContent {
            ThoughtsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ThoughtList()
                }
            }
        }
    }
}



