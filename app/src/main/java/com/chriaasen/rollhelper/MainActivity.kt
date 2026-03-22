package com.chriaasen.rollhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chriaasen.rollhelper.ui.navigation.AppNavigation
import com.chriaasen.rollhelper.ui.theme.RollHelper3Theme
import com.chriaasen.rollhelper.ui.theme.GradientBackground

class MainActivity : ComponentActivity() {
    @RequiresApi(35)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RollHelper3Theme {
                GradientBackground {
                    AppNavigation()
                }
            }
        }
    }
}

@RequiresApi(35)
@Preview(showBackground = true)
@Composable
fun MainRollPagePreview() {
    RollHelper3Theme {
        GradientBackground {
            AppNavigation()
        }
    }
}
