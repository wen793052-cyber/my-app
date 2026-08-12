package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.NavigationTab

@Composable
fun WarmNavigationBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        // Diary Tab
        NavigationBarItem(
            selected = selectedTab == NavigationTab.DIARY,
            onClick = { onTabSelected(NavigationTab.DIARY) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.DIARY) Icons.Filled.Book else Icons.Outlined.Book,
                    contentDescription = "日记列表"
                )
            },
            label = {
                Text(
                    text = "日记",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == NavigationTab.DIARY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Calendar Tab
        NavigationBarItem(
            selected = selectedTab == NavigationTab.CALENDAR,
            onClick = { onTabSelected(NavigationTab.CALENDAR) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.CALENDAR) Icons.Filled.CalendarMonth else Icons.Outlined.CalendarMonth,
                    contentDescription = "日历视图"
                )
            },
            label = {
                Text(
                    text = "日历",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == NavigationTab.CALENDAR) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Community Tab
        NavigationBarItem(
            selected = selectedTab == NavigationTab.COMMUNITY,
            onClick = { onTabSelected(NavigationTab.COMMUNITY) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.COMMUNITY) Icons.Filled.Forum else Icons.Outlined.Forum,
                    contentDescription = "私密社区"
                )
            },
            label = {
                Text(
                    text = "社区",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == NavigationTab.COMMUNITY) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Friends Tab
        NavigationBarItem(
            selected = selectedTab == NavigationTab.FRIENDS,
            onClick = { onTabSelected(NavigationTab.FRIENDS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.FRIENDS) Icons.Filled.People else Icons.Outlined.People,
                    contentDescription = "好友列表"
                )
            },
            label = {
                Text(
                    text = "好友",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == NavigationTab.FRIENDS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Settings Tab
        NavigationBarItem(
            selected = selectedTab == NavigationTab.SETTINGS,
            onClick = { onTabSelected(NavigationTab.SETTINGS) },
            icon = {
                Icon(
                    imageVector = if (selectedTab == NavigationTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "设置"
                )
            },
            label = {
                Text(
                    text = "设置",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == NavigationTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
