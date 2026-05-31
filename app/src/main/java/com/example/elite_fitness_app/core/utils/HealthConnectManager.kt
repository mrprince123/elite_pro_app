package com.example.elite_fitness_app.core.utils

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.TotalCaloriesBurnedRecord
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Data class holding today's health metrics.
 */
data class HealthData(
    val steps: Long = 0L,
    val activeMinutes: Long = 0L,
    val heartPoints: Int = 0,
    val caloriesBurned: Double = 0.0,
    val distanceKm: Double = 0.0,
    val isSimulated: Boolean = false
)

/**
 * Manager for reading health data from Health Connect.
 * Falls back to simulated data when Health Connect is unavailable.
 */
@Singleton
class HealthConnectManager @Inject constructor() {

    companion object {
        val PERMISSIONS = setOf(
            HealthPermission.getReadPermission(StepsRecord::class),
            HealthPermission.getReadPermission(TotalCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class),
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(ExerciseSessionRecord::class),
            HealthPermission.getReadPermission(DistanceRecord::class),
        )
    }

    /**
     * Check if Health Connect is available on this device.
     */
    fun isAvailable(context: Context): Boolean {
        return try {
            val status = HealthConnectClient.getSdkStatus(context)
            status == HealthConnectClient.SDK_AVAILABLE
        } catch (e: Exception) {
            Timber.w(e, "Health Connect availability check failed")
            false
        }
    }

    /**
     * Check if all required permissions are granted.
     */
    suspend fun hasPermissions(context: Context): Boolean {
        return try {
            if (!isAvailable(context)) return false
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()
            PERMISSIONS.all { it in granted }
        } catch (e: Exception) {
            Timber.w(e, "Permission check failed")
            false
        }
    }

    /**
     * Read today's health data from Health Connect.
     * Returns simulated data if Health Connect is unavailable or permissions are missing.
     */
    suspend fun readTodayData(context: Context): HealthData {
        if (!isAvailable(context)) {
            Timber.d("Health Connect unavailable, returning simulated data")
            return generateSimulatedData()
        }

        return try {
            val client = HealthConnectClient.getOrCreate(context)
            val granted = client.permissionController.getGrantedPermissions()

            if (!PERMISSIONS.all { it in granted }) {
                Timber.d("Health Connect permissions not granted, returning simulated data")
                return generateSimulatedData()
            }

            val todayStart = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
            val now = Instant.now()
            val timeRange = TimeRangeFilter.between(todayStart, now)

            // Read Steps
            val stepsResponse = client.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = timeRange
                )
            )
            val totalSteps = stepsResponse.records.sumOf { it.count }

            // Read Calories
            val caloriesResponse = client.readRecords(
                ReadRecordsRequest(
                    recordType = TotalCaloriesBurnedRecord::class,
                    timeRangeFilter = timeRange
                )
            )
            val totalCalories = caloriesResponse.records.sumOf {
                it.energy.inKilocalories
            }

            // Read Exercise Sessions for active minutes
            val exerciseResponse = client.readRecords(
                ReadRecordsRequest(
                    recordType = ExerciseSessionRecord::class,
                    timeRangeFilter = timeRange
                )
            )
            val activeMinutes = exerciseResponse.records.sumOf { session ->
                java.time.Duration.between(session.startTime, session.endTime).toMinutes()
            }

            // Read Heart Rate for heart points approximation
            val heartRateResponse = client.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = timeRange
                )
            )
            // Approximate heart points: each minute above 100bpm = 1 point, above 130bpm = 2 points
            var heartPoints = 0
            heartRateResponse.records.forEach { record ->
                record.samples.forEach { sample ->
                    when {
                        sample.beatsPerMinute >= 130 -> heartPoints += 2
                        sample.beatsPerMinute >= 100 -> heartPoints += 1
                    }
                }
            }

            // Read Distance
            val distanceResponse = client.readRecords(
                ReadRecordsRequest(
                    recordType = DistanceRecord::class,
                    timeRangeFilter = timeRange
                )
            )
            val totalDistanceKm = distanceResponse.records.sumOf {
                it.distance.inMeters / 1000.0
            }

            HealthData(
                steps = totalSteps,
                activeMinutes = activeMinutes,
                heartPoints = heartPoints,
                caloriesBurned = totalCalories,
                distanceKm = totalDistanceKm,
                isSimulated = false
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to read Health Connect data")
            generateSimulatedData()
        }
    }

    /**
     * Generate realistic-looking simulated health data for demo/testing.
     */
    private fun generateSimulatedData(): HealthData {
        val hour = java.time.LocalTime.now().hour
        // Scale data by time of day for realism
        val dayProgress = (hour / 24.0).coerceIn(0.0, 1.0)
        val baseSteps = (Random.nextInt(6000, 12000) * dayProgress).toLong()
        val baseMinutes = (Random.nextInt(30, 90) * dayProgress).toLong()
        val baseHeartPoints = (Random.nextInt(10, 30) * dayProgress).toInt()
        val baseCalories = Random.nextInt(200, 600) * dayProgress
        val baseDistance = (baseSteps * 0.00075) // Rough approximation of step length in km

        return HealthData(
            steps = baseSteps.coerceAtLeast(0),
            activeMinutes = baseMinutes.coerceAtLeast(0),
            heartPoints = baseHeartPoints.coerceAtLeast(0),
            caloriesBurned = baseCalories.coerceAtLeast(0.0),
            distanceKm = baseDistance.coerceAtLeast(0.0),
            isSimulated = true
        )
    }
}
