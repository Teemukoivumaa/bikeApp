package com.example.bikeapp.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object AuthenticationStateKeys {
    const val UNAUTHENTICATED = "unauthenticated"
    const val STRAVA_AUTH_STARTED = "strava_auth_started"
    const val STRAVA_AUTH_FINISHED = "strava_auth_finished"
    const val AUTHENTICATED = "authenticated"
}

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

    fun saveAuthenticationState(state: String) {
        encryptedSharedPreferences.edit { putString("authentication_state", state) }
    }

    fun getAuthenticationState(): String? {
        return encryptedSharedPreferences.getString("authentication_state", AuthenticationStateKeys.UNAUTHENTICATED)
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

    fun saveScope(scope: String) {
        encryptedSharedPreferences.edit { putString("scope", scope) }
    }
}