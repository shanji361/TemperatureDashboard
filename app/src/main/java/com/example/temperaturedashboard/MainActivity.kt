package com.example.temperaturedashboard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.temperaturedashboard.ui.theme.TemperatureDashboardTheme
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemperatureDashboardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TemperatureDashboardScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemperatureDashboardScreen(
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.dashboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Temperature Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            ControlButtons(
                isSimulating = state.isSimulating,
                onToggleSimulation = { viewModel.toggleSimulation() },
                onClear = { viewModel.clearReadings() }
            )

            // stats
            StatsSection(stats = state.stats)
            if (state.readings.isNotEmpty()) {
                TemperatureChart(
                    readings = state.readings.map { it.temperature },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
            // readings list
            Text(
                text = "Recent Readings (Last 20)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            ReadingsList(readings = state.readings)
        }
    }
}

@Composable
fun ControlButtons(
    isSimulating: Boolean,
    onToggleSimulation: () -> Unit,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onToggleSimulation,
            modifier = Modifier.weight(1f)
        ) {

            Spacer(modifier = Modifier.width(8.dp))
            Text(if (isSimulating) "Pause" else "Resume")
        }


        OutlinedButton(
            onClick = onClear,
            modifier = Modifier.weight(1f)
        ) {

            Text("Clear")
        }
    }
}

@Composable
fun TemperatureChart(readings: List<Float>,
                     modifier: Modifier = Modifier ) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(16.dp)
    ) {
        if (readings.isEmpty()) return@Canvas

        val minTemp = readings.minOrNull() ?: 0f
        val maxTemp = readings.maxOrNull() ?: 1f
        val tempRange = maxTemp - minTemp
        val width = size.width
        val height = size.height
        val spacing = width / (readings.size - 1).coerceAtLeast(1)

        val axisColor = Color.Gray
        val strokeWidth = 2f

        // Y-axis
        drawLine(
            color = axisColor,
            start = Offset(0f, 0f),
            end = Offset(0f, height),
            strokeWidth = strokeWidth
        )

        // X-axis
        drawLine(
            color = axisColor,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = strokeWidth
        )
        val path = Path()
        readings.forEachIndexed { index, temp ->
            val x = index * spacing
            val normalized = (temp - minTemp) / tempRange
            val y = height - (normalized * height)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(path, color = Color.Blue, style = Stroke(width = 3f))
    }
}


@Composable
fun StatsSection(stats: TemperatureStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Current",
                    value = stats.current,
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    label = "Average",
                    value = stats.average,
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    label = "Min",
                    value = stats.min,
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    label = "Max",
                    value = stats.max,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: Float, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = String.format("%.1f°F", value),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}


@Composable
fun ReadingsList(readings: List<TemperatureReading>) {
    Card(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(readings) { reading ->
                ReadingItem(reading = reading)
            }
        }
    }
}

@Composable
fun ReadingItem(reading: TemperatureReading) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = reading.timestamp,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = String.format("%.2f°F", reading.temperature),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}