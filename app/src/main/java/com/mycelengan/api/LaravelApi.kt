package com.mycelengan.api

import android.content.Context
import com.mycelengan.TransactionDraft
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object LaravelApi {
    private const val BASE_URL = "http://192.168.137.1:8000/api/"
    private const val PREFS_NAME = "my_celengan_session"
    private const val TOKEN_KEY = "token"

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun hasToken(): Boolean = !token().isNullOrBlank()

    fun saveToken(token: String?) {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.putString(TOKEN_KEY, token.orEmpty())
            ?.apply()
    }

    fun clearToken() {
        appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.edit()
            ?.remove(TOKEN_KEY)
            ?.apply()
    }

    fun login(email: String, password: String): AuthResponse {
        val json = request(
            method = "POST",
            path = "login",
            body = JSONObject()
                .put("email", email)
                .put("password", password)
        )
        return parseAuthResponse(json)
    }

    fun register(email: String, password: String, username: String): AuthResponse {
        val json = request(
            method = "POST",
            path = "register",
            body = JSONObject()
                .put("name", username)
                .put("username", username)
                .put("email", email)
                .put("password", password)
        )
        return parseAuthResponse(json)
    }

    fun logout() {
        runCatching { request("POST", "logout") }
        clearToken()
    }

    fun me(): UserDto {
        val json = request("GET", "me")
        return parseUser(json.optJSONObject("user") ?: json.optJSONObject("data") ?: json)
    }

    fun updateProfile(username: String): UserDto {
        val json = request(
            method = "PUT",
            path = "profile",
            body = JSONObject()
                .put("name", username)
                .put("username", username)
        )
        return parseUser(json.optJSONObject("user") ?: json.optJSONObject("data") ?: json)
    }

    fun changePassword(password: String) {
        request(
            method = "PUT",
            path = "change-password",
            body = JSONObject()
                .put("password", password)
                .put("password_confirmation", password)
        )
    }

    fun transactions(): List<Map<String, Any>> {
        val json = request("GET", "transactions")
        return jsonArray(json, "transactions").mapObjects(::transactionMap)
    }

    fun addTransaction(amount: Int, desc: String, date: String, type: String, icon: String) {
        request(
            method = "POST",
            path = "transactions",
            body = JSONObject()
                .put("amount", amount)
                .put("desc", desc)
                .put("date", date)
                .put("type", type)
                .put("icon", icon)
        )
    }

    fun addTransactions(transactions: List<TransactionDraft>) {
        val body = JSONObject().put(
            "transactions",
            JSONArray().apply {
                transactions.forEach { transaction ->
                    put(
                        JSONObject()
                            .put("amount", transaction.amount)
                            .put("desc", transaction.desc)
                            .put("date", transaction.date)
                            .put("type", transaction.type)
                            .put("icon", transaction.icon)
                    )
                }
            }
        )
        request(method = "POST", path = "transactions/bulk", body = body)
    }

    fun deleteTransaction(id: String) {
        request("DELETE", "transactions/$id")
    }

    fun targets(): List<TargetDto> {
        val json = request("GET", "targets")
        return jsonArray(json, "targets").mapTargetObjects(::parseTarget)
    }

    fun target(id: String): TargetDto {
        val json = request("GET", "targets/$id")
        return parseTarget(json.optJSONObject("target") ?: json.optJSONObject("data") ?: json)
    }

    fun addTarget(
        title: String,
        subtitle: String,
        icon: String,
        targetAmount: Int,
        perMonth: Int,
        createdAt: String
    ) {
        request(
            method = "POST",
            path = "targets",
            body = JSONObject()
                .put("title", title)
                .put("subtitle", subtitle)
                .put("icon", icon)
                .put("target_amount", targetAmount)
                .put("targetAmount", targetAmount)
                .put("per_month", perMonth)
                .put("perMonth", perMonth)
                .put("created_at_label", createdAt)
                .put("createdAt", createdAt)
        )
    }

    fun updateTargetName(id: String, title: String) {
        request(
            method = "PUT",
            path = "targets/$id",
            body = JSONObject().put("title", title)
        )
    }

    fun deleteTarget(id: String) {
        request("DELETE", "targets/$id")
    }

    fun addTargetEntry(id: String, amount: Int, desc: String, isAdd: Boolean) {
        request(
            method = "POST",
            path = "targets/$id/entries",
            body = JSONObject()
                .put("amount", amount)
                .put("desc", desc)
                .put("type", if (isAdd) "add" else "remove")
                .put("is_add", isAdd)
        )
    }

    fun targetHistory(id: String): List<Map<String, Any>> {
        val json = request("GET", "targets/$id/history")
        return jsonArray(json, "history").mapObjects(::historyMap)
    }

    fun targetEntries(id: String): List<Map<String, Any>> {
        val json = request("GET", "targets/$id/entries")
        return jsonArray(json, "entries").mapObjects(::historyMap)
    }

    private fun request(method: String, path: String, body: JSONObject? = null): JSONObject {
        val connection = (URL(BASE_URL + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json")
            token()?.takeIf { it.isNotBlank() }?.let {
                setRequestProperty("Authorization", "Bearer $it")
            }
            if (body != null) {
                doOutput = true
            }
        }

        if (body != null) {
            OutputStreamWriter(connection.outputStream).use { it.write(body.toString()) }
        }

        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val response = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()
        connection.disconnect()

        val json = if (response.isBlank()) JSONObject() else JSONObject(response)
        if (status !in 200..299) {
            throw ApiException(json.optString("message", "Request gagal ($status)"))
        }
        return json
    }

    private fun token(): String? {
        return appContext
            ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?.getString(TOKEN_KEY, null)
    }

    private fun parseAuthResponse(json: JSONObject): AuthResponse {
        val token = json.optString("token").ifBlank {
            json.optString("access_token").ifBlank {
                json.optJSONObject("data")?.optString("token").orEmpty()
            }
        }
        val userJson = json.optJSONObject("user")
            ?: json.optJSONObject("data")?.optJSONObject("user")
            ?: JSONObject()
        return AuthResponse(token = token, user = parseUser(userJson))
    }

    private fun parseUser(json: JSONObject): UserDto {
        val username = json.optString("username").ifBlank { json.optString("name") }
        return UserDto(
            id = json.optString("id"),
            email = json.optString("email"),
            username = username,
            photoUrl = json.optString("photo_url").ifBlank {
                json.optString("photoUrl").ifBlank {
                    username.firstOrNull()?.uppercase().orEmpty()
                }
            },
            saldo = json.optIntFlexible("saldo"),
            totalIncome = json.optIntFlexible("total_income", "totalIncome"),
            totalExpense = json.optIntFlexible("total_expense", "totalExpense")
        )
    }

    private fun parseTarget(json: JSONObject): TargetDto {
        return TargetDto(
            id = json.optString("id"),
            title = json.optString("title"),
            subtitle = json.optString("subtitle"),
            icon = json.optString("icon"),
            targetAmount = json.optIntFlexible("target_amount", "targetAmount"),
            currentAmount = json.optIntFlexible("current_amount", "currentAmount"),
            perMonth = json.optIntFlexible("per_month", "perMonth"),
            createdAt = json.optString("created_at_label").ifBlank {
                json.optString("createdAt").ifBlank { json.optString("created_at") }
            }
        )
    }

    private fun transactionMap(json: JSONObject): Map<String, Any> = mapOf(
        "id" to json.optString("id"),
        "amount" to json.optIntFlexible("amount"),
        "desc" to json.optString("desc").ifBlank { json.optString("description") },
        "date" to json.optString("date"),
        "type" to json.optString("type"),
        "icon" to json.optString("icon"),
        "timestamp" to json.optString("created_at")
    )

    private fun historyMap(json: JSONObject): Map<String, Any> = mapOf(
        "id" to json.optString("id"),
        "amount" to json.optIntFlexible("amount").toLong(),
        "desc" to json.optString("desc").ifBlank { json.optString("description") },
        "type" to json.optString("type"),
        "timestamp" to json.optString("created_at")
    )

    private fun jsonArray(json: JSONObject, key: String): JSONArray {
        return json.optJSONArray(key)
            ?: json.optJSONArray("data")
            ?: json.optJSONObject("data")?.optJSONArray(key)
            ?: JSONArray()
    }

    private fun JSONArray.mapObjects(mapper: (JSONObject) -> Map<String, Any>): List<Map<String, Any>> {
        return (0 until length()).mapNotNull { index -> optJSONObject(index)?.let(mapper) }
    }

    private fun JSONArray.mapTargetObjects(mapper: (JSONObject) -> TargetDto): List<TargetDto> {
        return (0 until length()).mapNotNull { index -> optJSONObject(index)?.let(mapper) }
    }

    private fun JSONObject.optIntFlexible(vararg keys: String): Int {
        keys.forEach { key ->
            if (has(key) && !isNull(key)) {
                val value = opt(key)
                return when (value) {
                    is Number -> value.toInt()
                    is String -> value.toIntOrNull() ?: 0
                    else -> 0
                }
            }
        }
        return 0
    }
}

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val email: String,
    val username: String,
    val photoUrl: String,
    val saldo: Int,
    val totalIncome: Int,
    val totalExpense: Int
) {
    fun toMap(): Map<String, Any> = mapOf(
        "uid" to id,
        "id" to id,
        "email" to email,
        "username" to username,
        "photoUrl" to photoUrl,
        "saldo" to saldo,
        "totalIncome" to totalIncome,
        "totalExpense" to totalExpense
    )
}

data class TargetDto(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val targetAmount: Int,
    val currentAmount: Int,
    val perMonth: Int,
    val createdAt: String
)

class ApiException(message: String) : Exception(message)
