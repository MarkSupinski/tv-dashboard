package com.microserve.batterytv.data

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
}

/** One sample in a SOC history series recorded by the Home Assistant recorder. */
data class HistoryPoint(
    val ts: String,
    val soc: Double?,
    val voltage: Double?,
    val current: Double?,
)
