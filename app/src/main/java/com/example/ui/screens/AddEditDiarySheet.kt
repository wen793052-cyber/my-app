package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.DiaryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditDiarySheet(
    existingEntry: DiaryEntry? = null,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        content: String,
        date: String,
        formattedDate: String,
        mood: String,
        weather: String,
        imageUris: String,
        tags: String,
        location: String,
        shareToCommunity: Boolean,
        privacyLevel: String
    ) -> Unit
) {
    val context = LocalContext.current
    val isoFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displayFormat = remember { SimpleDateFormat("yyyy年M月d日 EEEE", Locale.CHINA) }

    val todayMillis = remember { System.currentTimeMillis() }

    var title by remember { mutableStateOf(existingEntry?.title ?: "") }
    var content by remember { mutableStateOf(existingEntry?.content ?: "") }

    var selectedDateMillis by remember {
        mutableStateOf(
            if (existingEntry != null) {
                try {
                    isoFormat.parse(existingEntry.date)?.time ?: todayMillis
                } catch (e: Exception) {
                    todayMillis
                }
            } else todayMillis
        )
    }

    var showDatePicker by remember { mutableStateOf(false) }

    val moodList = listOf(
        "☕ 惬意", "😊 快乐", "🌿 平静", "🌤️ 舒畅", "🌙 沉思",
        "🌟 期待", "🌧️ 感伤", "✨ 治愈", "🥳 激动", "🍵 独处",
        "💭 思考", "🌇 晚霞", "💡 灵感", "🥱 疲惫", "🕯️ 怀念",
        "🍀 幸运", "🐱 偶遇", "📖 沉浸"
    )
    var selectedMood by remember { mutableStateOf(existingEntry?.mood ?: moodList.first()) }

    val weatherList = listOf(
        "☀️ 晴朗", "⛅ 多云", "☁️ 阴天", "🌧️ 小雨", "🌧️ 大雨",
        "⛈️ 雷阵雨", "❄️ 积雪", "🌨️ 飘雪", "💨 清风", "🌪️ 大风",
        "🌈 彩虹", "🌫️ 薄雾", "🌅 霞光", "🌇 晚霞", "🌌 繁星",
        "🌡️ 降温", "♨️ 闷热"
    )
    var selectedWeather by remember { mutableStateOf(existingEntry?.weather ?: weatherList.first()) }

    var tags by remember { mutableStateOf(existingEntry?.tags ?: "#日常生活") }
    var location by remember { mutableStateOf(existingEntry?.location ?: "阳光客厅") }
    var shareToCommunity by remember { mutableStateOf(existingEntry?.isSharedToCommunity ?: false) }

    // Attached Image URIs
    var imageUris by remember {
        mutableStateOf(
            if (!existingEntry?.imageUris.isNullOrBlank()) {
                existingEntry!!.imageUris.split(",").filter { it.isNotBlank() }
            } else emptyList()
        )
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val newUris = uris.map { it.toString() }
            imageUris = (imageUris + newUris).distinct()
        }
    }

    // Preset Warm Aesthetic Photos
    val presetPhotos = listOf(
        "res:///drawable/cozy_header_banner_1786454261230",
        "res:///drawable/matcha_garden_banner_1786454303794",
        "res:///drawable/lavender_night_banner_1786454322456",
        "res:///drawable/sakura_pink_banner_1786454338723",
        "res:///drawable/cozy_journal_banner_1786452016128"
    )

    Surface(
        modifier = Modifier.fillMaxHeight(0.92f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sheet Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (existingEntry == null) "写新日记" else "编辑日记",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "关闭")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Date Button
            val dateDateObj = Date(selectedDateMillis)
            val currentDateIso = isoFormat.format(dateDateObj)
            val currentDateDisplay = displayFormat.format(dateDateObj)

            Surface(
                onClick = { showDatePicker = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "选择日期",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = currentDateDisplay,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "点击修改记录日期",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title Input
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("日记标题 (例如: 阳光下的咖啡)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mood Selector
            Text(
                text = "今天的心情",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                moodList.forEach { mood ->
                    FilterChip(
                        selected = selectedMood == mood,
                        onClick = { selectedMood = mood },
                        label = { Text(mood) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Mood,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Weather Selector
            Text(
                text = "天气与氛围",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                weatherList.forEach { w ->
                    FilterChip(
                        selected = selectedWeather == w,
                        onClick = { selectedWeather = w },
                        label = { Text(w) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Content Input
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("记录今天的生活片段与温柔心声...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                maxLines = 10,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Image Attachments Section
            Text(
                text = "添加图片与美图贴纸",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                item {
                    // Upload button
                    Surface(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "上传图片",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "相册相片", fontSize = 10.sp)
                        }
                    }
                }

                // Preset Warm Banner photo option
                item {
                    val bannerUri = "android.resource://${context.packageName}/drawable/cozy_journal_banner_1786452016128"
                    val isSelected = imageUris.contains(bannerUri)
                    Surface(
                        onClick = {
                            imageUris = if (isSelected) imageUris - bannerUri else imageUris + bannerUri
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .size(80.dp)
                            .border(
                                width = if (isSelected) 3.dp else 0.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(bannerUri),
                            contentDescription = "暖光书桌配图",
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                items(imageUris) { uriStr ->
                    Box(modifier = Modifier.size(80.dp)) {
                        Image(
                            painter = rememberAsyncImagePainter(uriStr),
                            contentDescription = "关联配图",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        )
                        IconButton(
                            onClick = { imageUris = imageUris - uriStr },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "删除配图",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tags & Location
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("标签 (如 #日常生活)") },
                    leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("地点") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Share to Community Switch
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "同步精选片段至私密社区",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "好友可以在私密社区看到此条日记节选并点赞互动",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Switch(
                        checked = shareToCommunity,
                        onCheckedChange = { shareToCommunity = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = {
                    onSave(
                        existingEntry?.id ?: 0L,
                        title,
                        content,
                        currentDateIso,
                        currentDateDisplay,
                        selectedMood,
                        selectedWeather,
                        imageUris.joinToString(","),
                        tags,
                        location,
                        shareToCommunity,
                        if (shareToCommunity) "FRIENDS" else "PRIVATE"
                    )
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                Text(
                    text = if (existingEntry == null) "保存记录" else "更新日记",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Date Picker Modal
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDateMillis = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
