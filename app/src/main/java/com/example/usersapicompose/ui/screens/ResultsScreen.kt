package com.example.usersapicompose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.usersapicompose.data.model.LocationDto
import com.example.usersapicompose.data.model.NameDto
import com.example.usersapicompose.data.model.PictureDto
import com.example.usersapicompose.data.model.SavedUser
import com.example.usersapicompose.data.model.UserDto
import com.example.usersapicompose.ui.viewmodel.TopFilter
import com.example.usersapicompose.ui.viewmodel.UserMark
import com.example.usersapicompose.ui.viewmodel.UserUi
import com.example.usersapicompose.ui.viewmodel.UsersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(vm: UsersViewModel) {
    val isLoading by vm.isLoading.collectAsState()
    val topFilter by vm.topFilter.collectAsState()
    val lastQueryUsers by vm.lastQueryUsers.collectAsState()
    val savedUsers by vm.savedUsers.collectAsState()
    val selectedApiFields by vm.selectedApiFields.collectAsState()

    val usersToShow: List<UserUi> = when (topFilter) {
        TopFilter.LAST_QUERY -> lastQueryUsers
        TopFilter.ONLY_SAVED -> savedUsers.map { it.toUserUiSaved() }
    }

    var menuOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (topFilter) {
                            TopFilter.LAST_QUERY -> "Rezultatele interogarii"
                            TopFilter.ONLY_SAVED -> "Doar inregistrarile pastrate"
                        }
                    )
                },
                actions = {
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = "Meniu global")
                    }
                    DropdownMenu(
                        expanded = menuOpen,
                        onDismissRequest = { menuOpen = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rezultatele interogarii") },
                            onClick = {
                                menuOpen = false
                                vm.setTopFilter(TopFilter.LAST_QUERY)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Doar inregistrarile pastrate") },
                            onClick = {
                                menuOpen = false
                                vm.setTopFilter(TopFilter.ONLY_SAVED)
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Incarcare inregistrari...")
                    }
                }

                usersToShow.isEmpty() -> {
                    Text(
                        if (topFilter == TopFilter.ONLY_SAVED)
                            "Nu exista inregistrari pastrate."
                        else
                            "Nu am primit date (verifica internetul).",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(usersToShow, key = { it.user.email ?: it.user.fullName() }) { item ->
                            UserRow(
                                item = item,
                                selectedApiFields = selectedApiFields,
                                onReport = { key -> vm.reportUser(key) },
                                onSave = { user -> vm.saveUser(user) },
                                onReset = { key -> vm.resetUser(key) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun SavedUser.toUserUiSaved(): UserUi {
    val parts = fullName.trim().split(" ").filter { it.isNotBlank() }
    val first = parts.firstOrNull().orEmpty()
    val last = if (parts.size >= 2) parts.drop(1).joinToString(" ") else ""

    val locParts = location.split(",").map { it.trim() }
    val city = locParts.firstOrNull().orEmpty()
    val country = locParts.getOrNull(1).orEmpty()

    val dto = UserDto(
        gender = gender.ifBlank { null },
        name = NameDto(title = null, first = first.ifBlank { null }, last = last.ifBlank { null }),
        location = LocationDto(city = city.ifBlank { null }, country = country.ifBlank { null }),
        email = email,
        picture = PictureDto(large = pictureUrl, medium = pictureUrl, thumbnail = pictureUrl)
    )

    return UserUi(user = dto, mark = UserMark.SAVED)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserRow(
    item: UserUi,
    selectedApiFields: Set<String>,
    onReport: (String) -> Unit,
    onSave: (UserDto) -> Unit,
    onReset: (String) -> Unit
) {
    val key = item.user.email?.takeIf { it.isNotBlank() } ?: item.user.fullName()
    val email = item.user.email.orEmpty()

    val bg = when (item.mark) {
        UserMark.REPORTED -> Color(0xFFFDD8D8)
        UserMark.SAVED -> Color(0xFFE4FAE4)
        else -> MaterialTheme.colorScheme.surface
    }

    var sheetOpen by remember { mutableStateOf(false) }
    var confirmReport by remember { mutableStateOf(false) }
    var confirmReset by remember { mutableStateOf(false) }

    if (confirmReport) {
        ConfirmDialog(
            title = "Raporteaza inregistrarea?",
            onYes = {
                confirmReport = false
                if (key.isNotBlank()) onReport(key)
            },
            onNo = { confirmReport = false }
        )
    }

    if (confirmReset) {
        ConfirmDialog(
            title = "Reseteaza inregistrarea?",
            onYes = {
                confirmReset = false
                if (key.isNotBlank()) onReset(key)
            },
            onNo = { confirmReset = false }
        )
    }

    if (sheetOpen) {
        ModalBottomSheet(onDismissRequest = { sheetOpen = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Actiuni", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        sheetOpen = false
                        confirmReport = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Raporteaza inregistrarea") }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        sheetOpen = false
                        onSave(item.user)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Pastreaza inregistrarea") }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        sheetOpen = false
                        confirmReset = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Reseteaza inregistrarea") }

                Spacer(Modifier.height(24.dp))
            }
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(bg)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                if ("picture" in selectedApiFields) {
                    val url = item.user.pictureUrl()
                    if (url.isNotBlank()) {
                        AsyncImage(
                            model = url,
                            contentDescription = "Poza",
                            modifier = Modifier.size(72.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    if ("name" in selectedApiFields) {
                        Text(
                            text = item.user.fullName().ifBlank { "(nume lipsa)" },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    if ("gender" in selectedApiFields) {
                        Text("Gen: ${item.user.gender.orEmpty()}")
                    }
                    if ("location" in selectedApiFields) {
                        Text("Locatie: ${item.user.fullLocation()}")
                    }
                    if ("email" in selectedApiFields) {
                        Text("Email: $email")
                    }
                }

                TextButton(onClick = { sheetOpen = true }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Meniu"
                    )
                }
            }
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    onYes: () -> Unit,
    onNo: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onNo,
        title = { Text(title) },
        confirmButton = {
            TextButton(onClick = onYes) { Text("Da") }
        },
        dismissButton = {
            TextButton(onClick = onNo) { Text("Nu") }
        }
    )
}
