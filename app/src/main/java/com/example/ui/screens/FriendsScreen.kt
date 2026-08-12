package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.data.Friend
import com.example.data.repository.AuthResult
import com.example.data.sync.FriendRequestDto
import com.example.ui.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    communityViewModel: CommunityViewModel
) {
    val context = LocalContext.current
    val currentUser by communityViewModel.currentUser.collectAsState()
    val incomingRequests by communityViewModel.incomingRequests.collectAsState()
    val friends by communityViewModel.allFriends.collectAsState()

    var showAuthDialog by remember { mutableStateOf(false) }
    var showSearchFriendDialog by remember { mutableStateOf(false) }
    var friendToDelete by remember { mutableStateOf<Friend?>(null) }
    var showQrCodeDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            communityViewModel.refreshRequests()
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentUser == null) {
                        showAuthDialog = true
                    } else {
                        showSearchFriendDialog = true
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "搜索/添加好友")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Account Banner
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (currentUser?.avatarUrl?.isNotBlank() == true) {
                                        Image(
                                            painter = rememberAsyncImagePainter(currentUser!!.avatarUrl),
                                            contentDescription = "头像",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.People,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column {
                                    if (currentUser != null) {
                                        Text(
                                            text = currentUser!!.nickname,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "账号ID: @${currentUser!!.username}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    } else {
                                        Text(
                                            text = "云端好友空间",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "注册登录账号后，即可搜索用户名与朋友联网互加好友",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (currentUser != null) {
                                    IconButton(
                                        onClick = { communityViewModel.refreshRequests() },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "刷新请求",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    IconButton(
                                        onClick = { showQrCodeDialog = true },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode,
                                            contentDescription = "我的名片",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { showAuthDialog = true },
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Login,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("登录/注册")
                                    }
                                }
                            }
                        }

                        if (currentUser != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { communityViewModel.logout() }
                                ) {
                                    Text("退出当前账号", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Friend Requests Section
                if (currentUser != null && incomingRequests.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MarkEmailUnread,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "收到的好友申请 (${incomingRequests.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    incomingRequests.forEach { request ->
                        FriendRequestCard(
                            request = request,
                            onAccept = {
                                communityViewModel.acceptRequest(request) { msg ->
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            },
                            onReject = {
                                communityViewModel.rejectRequest(request)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "我的好友列表 (${friends.size})",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (currentUser != null) {
                        TextButton(onClick = { showSearchFriendDialog = true }) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("搜索用户名加好友")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (friends.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (currentUser == null) "登录账号后，搜索对方用户名即可与朋友跨联网添加好友！" else "暂无好友，点击右下角按钮搜索朋友的用户名添加吧！",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (currentUser == null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { showAuthDialog = true }) {
                                Text("立即注册/登录账号")
                            }
                        }
                    }
                }
            } else {
                items(friends, key = { it.id }) { friend ->
                    FriendCard(
                        friend = friend,
                        onDelete = { friendToDelete = friend }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Auth Dialog (Login & Register)
    if (showAuthDialog) {
        var selectedTab by remember { mutableStateOf(0) } // 0: Login, 1: Register
        var usernameInput by remember { mutableStateOf("") }
        var passwordInput by remember { mutableStateOf("") }
        var nicknameInput by remember { mutableStateOf("") }
        var isLoading by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isLoading) showAuthDialog = false },
            title = {
                Column {
                    Text(
                        text = "暖记账号中心",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("账号登录") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("极速注册") }
                        )
                    }
                }
            },
            text = {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = usernameInput,
                        onValueChange = { usernameInput = it },
                        label = { Text("用户名 (如 xiaoming)") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("密码 (不少于 4 位)") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (selectedTab == 1) {
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = nicknameInput,
                            onValueChange = { nicknameInput = it },
                            label = { Text("个性昵称 (如 林深见鹿)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    if (isLoading) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("正在连接服务器验证...")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isLoading && usernameInput.isNotBlank() && passwordInput.isNotBlank(),
                    onClick = {
                        isLoading = true
                        if (selectedTab == 0) {
                            communityViewModel.loginAccount(usernameInput, passwordInput) { res ->
                                isLoading = false
                                when (res) {
                                    is AuthResult.Success -> {
                                        Toast.makeText(context, "登录成功！欢迎回来，${res.userProfile.nickname}", Toast.LENGTH_SHORT).show()
                                        showAuthDialog = false
                                    }
                                    is AuthResult.Error -> {
                                        Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        } else {
                            communityViewModel.registerAccount(usernameInput, passwordInput, nicknameInput) { res ->
                                isLoading = false
                                when (res) {
                                    is AuthResult.Success -> {
                                        Toast.makeText(context, "注册成功！已自动为您登录账号", Toast.LENGTH_SHORT).show()
                                        showAuthDialog = false
                                    }
                                    is AuthResult.Error -> {
                                        Toast.makeText(context, res.message, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                ) {
                    Text(if (selectedTab == 0) "登录" else "注册并登录")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isLoading,
                    onClick = { showAuthDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }

    // Search & Add Friend Dialog
    if (showSearchFriendDialog) {
        var targetUsernameInput by remember { mutableStateOf("") }
        var greetingInput by remember { mutableStateOf("想和你一起互相鼓励记日记~") }
        var isSending by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isSending) showSearchFriendDialog = false },
            title = { Text("联网加好友", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "输入你朋友注册的【用户名】，即可跨网络发送好友申请",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = targetUsernameInput,
                        onValueChange = { targetUsernameInput = it },
                        label = { Text("对方用户名 (如 xiaohong)") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = greetingInput,
                        onValueChange = { greetingInput = it },
                        label = { Text("打招呼留言") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (isSending) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("正在通过云端服务器发送申请...")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isSending && targetUsernameInput.isNotBlank(),
                    onClick = {
                        isSending = true
                        communityViewModel.sendFriendRequest(targetUsernameInput, greetingInput) { msg ->
                            isSending = false
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (msg.contains("已发送")) {
                                showSearchFriendDialog = false
                            }
                        }
                    }
                ) {
                    Text("发送好友申请")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isSending,
                    onClick = { showSearchFriendDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }

    // Delete Friend Dialog
    friendToDelete?.let { friend ->
        AlertDialog(
            onDismissRequest = { friendToDelete = null },
            title = { Text("删除好友") },
            text = { Text("确定要解除与好友【${friend.name}】的互相分享关系吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        communityViewModel.removeFriend(friend.id)
                        friendToDelete = null
                    }
                ) {
                    Text("确认解除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { friendToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }

    // QR Code Dialog
    if (showQrCodeDialog) {
        AlertDialog(
            onDismissRequest = { showQrCodeDialog = false },
            title = { Text("我的暖记网络名片", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = "二维码",
                            modifier = Modifier.size(100.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "你的用户名: @${currentUser?.username ?: "guest"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "告诉朋友你的用户名，朋友搜索即可联网添加你",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showQrCodeDialog = false }) {
                    Text("关闭")
                }
            }
        )
    }
}

@Composable
fun FriendRequestCard(
    request: FriendRequestDto,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = request.fromNickname.take(1),
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = request.fromNickname,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "@${request.fromUsername}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Row {
                    IconButton(onClick = onReject) {
                        Icon(Icons.Default.Close, contentDescription = "拒绝", tint = MaterialTheme.colorScheme.error)
                    }
                    IconButton(onClick = onAccept) {
                        Icon(Icons.Default.Check, contentDescription = "同意", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "留言: \"${request.message}\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 48.dp)
            )
        }
    }
}

@Composable
fun FriendCard(
    friend: Friend,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (friend.avatarUrl.isNotBlank()) {
                        Image(
                            painter = rememberAsyncImagePainter(friend.avatarUrl),
                            contentDescription = "头像",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = friend.name.take(1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = friend.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "账号ID: @${friend.friendId}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = friend.bio,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "解除好友",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}
