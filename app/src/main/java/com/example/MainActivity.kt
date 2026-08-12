package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.ui.components.WarmNavigationBar
import com.example.ui.screens.CalendarScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.FriendsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LockOverlayScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.WarmJournalTheme
import com.example.ui.viewmodel.CommunityViewModel
import com.example.ui.viewmodel.DiaryViewModel
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab

class MainActivity : FragmentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val diaryViewModel: DiaryViewModel by viewModels()
    private val communityViewModel: CommunityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val userSettings by mainViewModel.userSettings.collectAsState()
            val themeMode = userSettings?.themeMode ?: "SYSTEM"
            val darkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            val themePalette = userSettings?.themePalette ?: "WARM_ROSE"

            WarmJournalTheme(darkTheme = darkTheme, palette = themePalette) {
                val isLocked by mainViewModel.isLocked.collectAsState()

                if (isLocked) {
                    LockOverlayScreen(
                        onAuthenticateBiometric = {
                            mainViewModel.unlockWithBiometrics(this@MainActivity) { _ -> }
                        },
                        onVerifyPin = { pin ->
                            mainViewModel.unlockWithPin(pin)
                        },
                        isBiometricAvailable = mainViewModel.biometricLockManager.isBiometricAvailable()
                    )
                } else {
                    val selectedTab by mainViewModel.selectedTab.collectAsState()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            WarmNavigationBar(
                                selectedTab = selectedTab,
                                onTabSelected = { tab -> mainViewModel.selectTab(tab) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (selectedTab) {
                                NavigationTab.DIARY -> HomeScreen(
                                    diaryViewModel = diaryViewModel,
                                    mainViewModel = mainViewModel,
                                    onNavigateToCalendar = { mainViewModel.selectTab(NavigationTab.CALENDAR) },
                                    userNickname = userSettings?.nickname ?: "暖暖的记录者"
                                )
                                NavigationTab.CALENDAR -> CalendarScreen(
                                    diaryViewModel = diaryViewModel,
                                    onDateSelectedAndSwitchTab = { dateStr ->
                                        diaryViewModel.selectDate(dateStr)
                                        mainViewModel.selectTab(NavigationTab.DIARY)
                                    }
                                )
                                NavigationTab.COMMUNITY -> CommunityScreen(
                                    communityViewModel = communityViewModel,
                                    userNickname = userSettings?.nickname ?: "暖暖的记录者"
                                )
                                NavigationTab.FRIENDS -> FriendsScreen(
                                    communityViewModel = communityViewModel
                                )
                                NavigationTab.SETTINGS -> SettingsScreen(
                                    mainViewModel = mainViewModel,
                                    communityViewModel = communityViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
