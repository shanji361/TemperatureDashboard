package com.example.temperaturedashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

data class TemperatureReading(
    val timestamp: String,
    val temperature: Float,
    val timeMillis: Long = System.currentTimeMillis()
)

data class TemperatureStats(
    val current: Float = 0f,
    val average: Float = 0f,
    val min: Float = 0f,
    val max: Float = 0f
)

data class TemperatureDashboardState(
    val readings: List<TemperatureReading> = emptyList(),
    val stats: TemperatureStats = TemperatureStats(),
    val isSimulating: Boolean = false
)

class MainViewModel : ViewModel() {

    private val _dashboardState = MutableStateFlow(TemperatureDashboardState())
    val dashboardState: StateFlow<TemperatureDashboardState> = _dashboardState.asStateFlow()

    private var simulationJob: Job? = null
    private val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val maxReadings = 20

    private val updateIntervalMs = 2000L

    init {

        startSimulation()
    }

    fun toggleSimulation() {
        if (_dashboardState.value.isSimulating) {
            stopSimulation()
        } else {
            startSimulation()
        }
    }

    private fun startSimulation() {
        if (simulationJob?.isActive == true) return

        _dashboardState.value = _dashboardState.value.copy(isSimulating = true)

        simulationJob = viewModelScope.launch {
            while (true) {
                generateTemperatureReading()
                delay(updateIntervalMs)
            }
        }
    }

    private fun stopSimulation() {
        simulationJob?.cancel()
        simulationJob = null
        _dashboardState.value = _dashboardState.value.copy(isSimulating = false)
    }

    private fun generateTemperatureReading() {

        val temperature = Random.nextDouble(65.0, 85.0).toFloat()

        val timestamp = dateFormatter.format(Date())

        val newReading = TemperatureReading(
            timestamp = timestamp,
            temperature = temperature
        )

        val currentReadings = _dashboardState.value.readings.toMutableList()
        currentReadings.add(0, newReading)

        // keep 20 readings
        if (currentReadings.size > maxReadings) {
            currentReadings.removeAt(currentReadings.size - 1)
        }

        val stats = calculateStats(currentReadings)

        _dashboardState.value = _dashboardState.value.copy(
            readings = currentReadings,
            stats = stats
        )
    }

    private fun calculateStats(readings: List<TemperatureReading>): TemperatureStats {
        if (readings.isEmpty()) {
            return TemperatureStats()
        }

        val temps = readings.map { it.temperature }
        return TemperatureStats(
            current = temps.first(),
            average = temps.average().toFloat(),
            min = temps.minOrNull() ?: 0f,
            max = temps.maxOrNull() ?: 0f //return 0 instead of null
        )
    }

    fun clearReadings() {
        _dashboardState.value = _dashboardState.value.copy(
            readings = emptyList(),
            stats = TemperatureStats()
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopSimulation()
    }
}