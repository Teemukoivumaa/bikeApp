package com.example.bikeapp.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorageManager(context: Context) {
    private val masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedSharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveCode(code: String) {
        encryptedSharedPreferences.edit { putString("code", code) }
    }

    fun getCode(): String? {
        return encryptedSharedPreferences.getString("code", null)
    }

    fun deleteCode() {
        encryptedSharedPreferences.edit { remove("code") }
    }

    fun saveAccessToken(accessToken: String) {
        encryptedSharedPreferences.edit { putString("access_token", accessToken) }
    }

    fun getAccessToken(): String? {
        return encryptedSharedPreferences.getString("access_token", null)
    }

    fun deleteAccessToken() {
        encryptedSharedPreferences.edit { remove("access_token") }
    }

    fun saveRefreshToken(refreshToken: String) {
        encryptedSharedPreferences.edit { putString("refresh_token", refreshToken) }
    }

    fun getRefreshToken(): String? {
        return encryptedSharedPreferences.getString("refresh_token", null)
    }

    fun deleteRefreshToken() {
        encryptedSharedPreferences.edit { remove("refresh_token") }
    }

    fun saveExpiresAt(expiresAt: Int) {
        encryptedSharedPreferences.edit { putInt("expires_at", expiresAt) }
    }

    fun getExpiresAt(): Int? {
        return encryptedSharedPreferences.getInt("expires_at", -1).takeIf { it != -1 }
    }

    fun deleteExpiresAt() {
        encryptedSharedPreferences.edit { remove("expires_at") }
    }

    fun saveExpiresIn(expiresIn: Int) {
        encryptedSharedPreferences.edit { putInt("expires_in", expiresIn) }
    }

    fun getExpiresIn(): Int? {
        return encryptedSharedPreferences.getInt("expires_in", -1).takeIf { it != -1 }
    }

    fun deleteExpiresIn() {
        encryptedSharedPreferences.edit { remove("expires_in") }
    }

    fun saveAthleteId(athleteId: Int) {
        encryptedSharedPreferences.edit { putInt("athlete_id", athleteId) }
    }

    fun getAthleteId(): Int? {
        return encryptedSharedPreferences.getInt("athlete_id", -1).takeIf { it != -1 }
    }

    fun deleteAthleteId() {
        encryptedSharedPreferences.edit { remove("athlete_id") }
    }
}