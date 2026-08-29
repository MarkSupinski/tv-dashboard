package com.microserve.batterytv.data

/** Charge direction for a battery, derived from the signed pack current. */
enum class ChargeState { IDLE, CHARGING, DISCHARGING }

/**
 * Current telemetry for one battery, assembled from the Home Assistant
 * `ecoworthy_battery` integration's per-field sensor entities.
 *
 * `address` is the entity-id slug of the battery device, e.g. `eco_worthy_0b_7ad5`.
 */
data class BatteryStatus(
    val address: String,
    val name: String,
    val lastUpdated: String,
    val soc: Double?,
    val soh: Double?,
    val voltage: Double?,
    val current: Double?,
    val powerW: Double?,
    val temperature: Double?,
    val capacityAh: Double?,
    val health: Double?,
    val problemCode: Int?,
    val cells: List<Double>,
) {
    val cellCount: Int get() = cells.size

    /**
     * Current direction with a small dead-band so idle readings don't flicker.
     * Positive current = charging (verified against the live BMS data).
     */
    val chargeState: ChargeState
        get() {
            val c = current ?: 0.0
            return when {
                c > IDLE_CURRENT_A -> ChargeState.CHARGING
                c < -IDLE_CURRENT_A -> ChargeState.DISCHARGING
                else -> ChargeState.IDLE
            }
        }

    companion object {
        /** Amps below this are treated as idle (sensor resolution is 0.1 A). */
        private const val IDLE_CURRENT_A = 0.1
    }
}

/** One sample in a SOC history series recorded by the Home Assistant recorder. */
data class HistoryPoint(
    val ts: String,
    val soc: Double?,
    val voltage: Double?,
    val current: Double?,
)
