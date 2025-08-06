package com.chriaasen.rollhelper.ui.navigation

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chriaasen.rollhelper.ui.components.PageIndicator
import com.chriaasen.rollhelper.ui.history.HistoryPage
import com.chriaasen.rollhelper.ui.main.MainRollPage
import com.chriaasen.rollhelper.ui.profile.ProfilePage
import com.chriaasen.rollhelper.ui.storage.DataStoreManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars

@RequiresApi(35)
@Composable
fun AppNavigation() {
    val pagerState = rememberPagerState(
        initialPage = 1, // Default to MainRollPage
        pageCount = { 3 } // Total number of pages
    )

    // State for roll history
    val rollHistory = remember { mutableStateListOf<Triple<List<Int>, Int, Int>>() }

    // Other states
    val selectedDiceList = remember { mutableStateListOf<Pair<String, Int>>() }
    val rollResults = remember { mutableStateOf<List<Int>>(emptyList()) }
    var hasRolled by remember { mutableStateOf(false) }
    var shouldAnimate by remember { mutableStateOf(false) }

    // DataStore
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Preload state and progress
    var isDataPreloaded by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            withTimeoutOrNull(5000L) { // Timeout after 5 seconds
                try {
                    println("Starting preloading...")
                    delay(100) // Short initial delay

                    // Incrementally update progress
                    for (i in 1..100) {
                        delay(20) // Small delay for smooth progress
                        loadingProgress = i / 100f // Update progress proportionally
                    }

                    // Perform actual data loading
                    dataStoreManager.getRollHistory().firstOrNull()?.let { savedHistory ->
                        rollHistory.clear()
                        rollHistory.addAll(savedHistory)
                    }

                    println("Preloading complete.")
                } catch (e: Exception) {
                    println("Error during preloading: ${e.message}")
                }
            }
            isDataPreloaded = true
        }
    }

    if (isDataPreloaded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> HistoryPage(rollHistory = rollHistory)
                    1 -> MainRollPage(
                        rollHistory = rollHistory,
                        selectedDiceList = selectedDiceList,
                        rollResults = rollResults.value,
                        hasRolled = hasRolled,
                        onHasRolledUpdated = { newHasRolled ->
                            hasRolled = newHasRolled
                            if (newHasRolled) shouldAnimate = true
                        },
                        isFocused = pagerState.currentPage == 1,
                        onRollResultsUpdated = { newRollResults ->
                            rollResults.value = newRollResults
                        },
                        shouldAnimate = shouldAnimate,
                        dataStoreManager = dataStoreManager
                    )
                    2 -> ProfilePage(dataStoreManager = dataStoreManager)
                }
            }
            PageIndicator(
                currentPage = pagerState.currentPage,
                pageCount = 3,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
            )

            // Reset animation flag when leaving the MainRollPage
            LaunchedEffect(pagerState.currentPage) {
                if (pagerState.currentPage != 1) {
                    shouldAnimate = false
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "RollHelper",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp),
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                CustomProgressBar(
                    progress = loadingProgress,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}


@Composable
fun CustomProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
    backgroundColor: Color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
    height: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(backgroundColor, shape = MaterialTheme.shapes.small) // Background track
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f)) // Ensure progress is within bounds
                .fillMaxHeight()
                .background(color, shape = MaterialTheme.shapes.small) // Progress bar
        )
    }
}
