package com.microserve.batterytv.data

import com.microserve.batterytv.BuildConfig
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

/** Thin HTTP client for the ECOWORTHY battery telemetry server. */
object ApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    val baseUrl: String =
        "http://${BuildConfig.BATTERY_SERVER_HOST}:${BuildConfig.BATTERY_SERVER_PORT}"

    private suspend fun getJson(path: String): JSONObject = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(baseUrl + path).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code} from $path")
            }
            val body = response.body?.string() ?: throw IOException("empty body")
            JSONObject(body)
        }
    }

    suspend fun fetchBatteries(): List<BatteryStatus> {
        val json = getJson("/api/batteries")
        val arr = json.getJSONArray("batteries")
        return (0 until arr.length()).map { BatteryStatus.fromJson(arr.getJSONObject(it)) }
    }

    suspend fun fetchHistory(address: String, range: String): List<HistoryPoint> {
        // Addresses contain only path-safe characters (hex, ':' and '-').
        val json = getJson("/api/batteries/$address/history?range=$range")
        val arr = json.getJSONArray("points")
        return (0 until arr.length()).map { HistoryPoint.fromJson(arr.getJSONObject(it)) }
    }
}

/** Repository boundary between the ViewModel and the network layer. */
class BatteryRepository {
    suspend fun getBatteries(): List<BatteryStatus> = ApiClient.fetchBatteries()

    suspend fun getHistory(address: String, range: String): List<HistoryPoint> =
        ApiClient.fetchHistory(address, range)
}
