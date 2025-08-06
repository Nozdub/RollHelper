package com.chriaasen.rollhelper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.chriaasen.rollhelper.ui.navigation.AppNavigation
import com.chriaasen.rollhelper.ui.theme.RollHelper3Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.chriaasen.rollhelper.ui.theme.GradientBackground

class MainActivity : ComponentActivity() {
    @RequiresApi(35)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            RollHelper3Theme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()

                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                    systemUiController.setNavigationBarColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons,
                        navigationBarContrastEnforced = false
                    )
                }

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
