// ui/theme/walks/WalksScreen.kt
package com.example.projectfinalpaseador.ui.theme.walks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.projectfinalpaseador.data.model.Walk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalksScreen(
    navController: NavController,
    viewModel: WalksViewModel,
    onWalkClick: (Walk) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Solicitudes", "Agendados", "Historial")

    val pendingWalks by viewModel.pendingWalks.collectAsState()
    val acceptedWalks by viewModel.acceptedWalks.collectAsState()
    val historyWalks by viewModel.historyWalks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllWalks()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Paseos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = error ?: "Error desconocido",
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.loadAllWalks() }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
                else -> {
                    when (selectedTab) {
                        0 -> PendingWalksList(
                            walks = pendingWalks,
                            onAccept = { walk -> viewModel.acceptWalk(walk.id) },
                            onReject = { walk -> viewModel.rejectWalk(walk.id) },
                            onWalkClick = onWalkClick
                        )
                        1 -> AcceptedWalksList(
                            walks = acceptedWalks,
                            onWalkClick = onWalkClick
                        )
                        2 -> HistoryWalksList(
                            walks = historyWalks,
                            onWalkClick = onWalkClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PendingWalksList(
    walks: List<Walk>,
    onAccept: (Walk) -> Unit,
    onReject: (Walk) -> Unit,
    onWalkClick: (Walk) -> Unit
) {
    if (walks.isEmpty()) {
        EmptyListMessage("No hay solicitudes pendientes")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(walks, key = { it.id }) { walk ->
                PendingWalkCard(
                    walk = walk,
                    onAccept = { onAccept(walk) },
                    onReject = { onReject(walk) },
                    onClick = { onWalkClick(walk) }
                )
            }
        }
    }
}

@Composable
fun AcceptedWalksList(
    walks: List<Walk>,
    onWalkClick: (Walk) -> Unit
) {
    if (walks.isEmpty()) {
        EmptyListMessage("No hay paseos agendados")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(walks, key = { it.id }) { walk ->
                AcceptedWalkCard(walk = walk, onClick = { onWalkClick(walk) })
            }
        }
    }
}

@Composable
fun HistoryWalksList(
    walks: List<Walk>,
    onWalkClick: (Walk) -> Unit
) {
    if (walks.isEmpty()) {
        EmptyListMessage("No hay paseos en el historial")
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(walks, key = { it.id }) { walk ->
                HistoryWalkCard(walk = walk, onClick = { onWalkClick(walk) })
            }
        }
    }
}

@Composable
fun EmptyListMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PendingWalkCard(
    walk: Walk,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = walk.getPetNameSafe(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Dueño: ${walk.getOwnerNameSafe()}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha: ${walk.getScheduledAtSafe()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Duración: ${walk.getDurationSafe()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Tipo: ${walk.getPetTypeSafe()}", style = MaterialTheme.typography.bodySmall)
            if (walk.getNotesSafe() != "Sin notas") {
                Text(text = "Notas: ${walk.getNotesSafe()}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onAccept, modifier = Modifier.weight(1f)) {
                    Text("Aceptar")
                }
                OutlinedButton(onClick = onReject, modifier = Modifier.weight(1f)) {
                    Text("Rechazar")
                }
            }
        }
    }
}

@Composable
fun AcceptedWalkCard(walk: Walk, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = walk.getPetNameSafe(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (walk.canStart()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = "LISTO PARA INICIAR",
                            modifier = Modifier.padding(4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Dueño: ${walk.getOwnerNameSafe()}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha: ${walk.getScheduledAtSafe()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Duración: ${walk.getDurationSafe()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Dirección: ${walk.getAddressSafe()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Estado: ${walk.getStatusSafe()}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF4CAF50))
        }
    }
}

@Composable
fun HistoryWalkCard(walk: Walk, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = walk.getPetNameSafe(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Dueño: ${walk.getOwnerNameSafe()}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha: ${walk.getScheduledAtSafe()}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Duración: ${walk.getDurationSafe()}", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Estado: ${walk.getStatusSafe()}",
                style = MaterialTheme.typography.bodySmall,
                color = if (walk.isFinished()) Color(0xFF4CAF50) else Color.Gray
            )
        }
    }
}
