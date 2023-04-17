package com.signez.signageproblemshooting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.signez.signageproblemshooting.ui.theme.SignEzTheme
import com.signez.signageproblemshooting.ui.tutorial.TutorialView

class TutorialActivity : ComponentActivity() {

    companion object {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignEzTheme {
                TutorialView()
            }
        }

    }

}
 