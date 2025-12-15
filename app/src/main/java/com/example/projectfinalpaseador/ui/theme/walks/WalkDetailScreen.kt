package com.example.projectfinalpaseador.ui.theme.walks

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
// CAMBIO: Usamos 'Add' (Signo +) porque 'CameraAlt' requiere librería externa
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.projectfinalpaseador.data.model.Walk
import com.example.projectfinalpaseador.utils.FileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalkDetailScreen(
    navController: NavController,
    walkData: Walk,
    viewModel: WalkDetailViewModel = viewModel()
) {
    LaunchedEffect(walkData) { viewModel.setWalkData(walkData) }

    val walk = viewModel.walk ?: walkData
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val file = FileUtils.getFileFromUri(context, it)
            if (file != null) viewModel.uploadEvidence(file)
        }
    }

    val baseUrl = "https://apimascotas.jmacboy.com/"
    val petImageUrl = if (walk.petPhoto?.startsWith("http") == true) walk.petPhoto
    else "$baseUrl${walk.petPhoto?.removePrefix("/")}"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Paseo") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, contentDescription = null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = petImageUrl,
                contentDescription = null,
                modifier = Modifier.size(120.dp).clip(CircleShape).background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(walk.getPetNameSafe(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Tipo: ${walk.getPetTypeSafe()}", style = MaterialTheme.typography.bodyLarge)
            Text("Dueño: ${walk.getOwnerNameSafe()}", style = MaterialTheme.typography.bodyMedium)
            Text("Fecha: ${walk.getScheduledAtSafe()}", style = MaterialTheme.typography.bodyMedium)
            Text("Duración: ${walk.getDurationSafe()}", style = MaterialTheme.typography.bodyMedium)
            Text("Estado: ${walk.getStatusSafe().uppercase()}", color = Color.Blue, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            if (viewModel.successMessage != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
                ) {
                    Text(
                        text = viewModel.successMessage!!,
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (walk.canStart()) {
                Button(
                    onClick = { viewModel.startWalk() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("INICIAR PASEO")
                }
            }

            if (walk.isInProgress()) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    // AQUÍ ESTÁ EL CAMBIO DE ÍCONO
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SUBIR EVIDENCIA / FOTO")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.endWalk() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("FINALIZAR PASEO")
                }
            }

            if (walk.isFinished()) {
                Text("Este paseo ha finalizado.", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Volver")
                }
            }

            if (viewModel.isLoading) CircularProgressIndicator(modifier = Modifier.padding(top=16.dp))
        }
    }
}