package com.signez.signageproblemshooting

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.signez.signageproblemshooting.data.datastore.StoreInitialLaunch
import com.signez.signageproblemshooting.ui.theme.SignEzTheme
import com.signez.signageproblemshooting.ui.tutorial.TutorialView
import kotlinx.coroutines.launch


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
 