package com.microserve.batterytv.data

import org.json.JSONObject

/**
 * Current telemetry for one battery, as served by the battery server's
 * `GET /api/batteries` endpoint.
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

    companion object {
        fun fromJson(o: JSONObject): BatteryStatus {
            val cellsObj = o.optJSONObject("cells") ?: JSONObject()
            val sortedKeys = cellsObj.keys().asSequence().toList().sortedWith(
                compareBy({ it.substringBefore("_").toIntOrNull() ?: 0 },
                    { it.substringAfter("_").toIntOrNull() ?: 0 })
            )
            val cells = sortedKeys.mapNotNull { k ->
                cellsObj.optDouble(k, Double.NaN).takeIf { !it.isNaN() }
            }
            return BatteryStatus(
                address = o.optString("address", ""),
                name = o.optString("name", "Unknown"),
                lastUpdated = o.optString("last_updated", ""),
                soc = optDoubleOrNull(o, "soc"),
                soh = optDoubleOrNull(o, "soh"),
                voltage = optDoubleOrNull(o, "voltage"),
                current = optDoubleOrNull(o, "current"),
                powerW = optDoubleOrNull(o, "power_w"),
                temperature = optDoubleOrNull(o, "temperature"),
                capacityAh = optDoubleOrNull(o, "capacity_ah"),
                health = optDoubleOrNull(o, "health"),
                problemCode = if (o.isNull("problem_code")) null else o.optInt("problem_code"),
                cells = cells,
            )
        }

        private fun optDoubleOrNull(o: JSONObject, key: String): Double? =
            if (!o.has(key) || o.isNull(key)) {
                null
            } else {
                o.optDouble(key, Double.NaN).takeIf { !it.isNaN() }
            }
    }
}

/** One sample in a SOC/voltage/current history series. */
data class HistoryPoint(
    val ts: String,
    val soc: Double?,
    val voltage: Double?,
    val current: Double?,
) {
    companion object {
        fun fromJson(o: JSONObject) = HistoryPoint(
            ts = o.optString("ts", ""),
            soc = optDoubleOrNull(o, "soc"),
            voltage = optDoubleOrNull(o, "voltage"),
            current = optDoubleOrNull(o, "current"),
        )

        private fun optDoubleOrNull(o: JSONObject, key: String): Double? =
            if (!o.has(key) || o.isNull(key)) {
                null
            } else {
                o.optDouble(key, Double.NaN).takeIf { !it.isNaN() }
            }
    }
}
