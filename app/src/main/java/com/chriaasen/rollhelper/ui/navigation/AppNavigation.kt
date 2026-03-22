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

    // Modifier + roll-adjust state lifted here so it survives page swipes
    var selectedModifiers by remember { mutableStateOf(setOf<String>()) }
    var showModifiers by remember { mutableStateOf(false) }
    var isCritMode by remember { mutableStateOf(false) }
    var isProfSelected by remember { mutableStateOf(false) }
    var isExpertiseSelected by remember { mutableStateOf(false) }

    // DataStore
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val coroutineScope = rememberCoroutineScope()

    // Preload state and progress
    var isDataPreloaded by remember { mutableStateOf(false) }
    var loadingProgress by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        // Load data and animate progress bar in parallel
        val dataJob = coroutineScope.launch {
            withTimeoutOrNull(5000L) {
                dataStoreManager.getRollHistory().firstOrNull()?.let { savedHistory ->
                    rollHistory.clear()
                    rollHistory.addAll(savedHistory)
                }
            }
        }
        // Animate progress bar while data loads
        for (i in 1..100) {
            delay(8)
            loadingProgress = i / 100f
        }
        dataJob.join()
        isDataPreloaded = true
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
                            // Reset post-roll toggles on each new roll
                            isCritMode = false
                            isProfSelected = false
                            isExpertiseSelected = false
                        },
                        shouldAnimate = shouldAnimate,
                        dataStoreManager = dataStoreManager,
                        selectedModifiers = selectedModifiers,
                        onSelectedModifiersChanged = { selectedModifiers = it },
                        showModifiers = showModifiers,
                        onShowModifiersChanged = { showModifiers = it },
                        isCritMode = isCritMode,
                        onCritModeChanged = { isCritMode = it },
                        isProfSelected = isProfSelected,
                        onIsProfSelectedChanged = { isProfSelected = it },
                        isExpertiseSelected = isExpertiseSelected,
                        onIsExpertiseSelectedChanged = { isExpertiseSelected = it }
                    )
                    2 -> ProfilePage(dataStoreManager = dataStoreManager)
                }
            }
            PageIndicator(
                currentPage = pagerState.currentPage,
                pageCount = 3,
                scrollOffsetFraction = pagerState.currentPageOffsetFraction,
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
