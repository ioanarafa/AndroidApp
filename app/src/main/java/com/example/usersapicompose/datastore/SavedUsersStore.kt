package com.example.usersapicompose.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.usersapicompose.data.model.SavedUser
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "saved_users")

class SavedUsersStore(private val context: Context) {

    private val gson = Gson()
    private val KEY_SAVED_USERS_JSON = stringPreferencesKey("saved_users_json")

    val savedUsers: Flow<List<SavedUser>> = context.dataStore.data.map { prefs ->
        val json = prefs[KEY_SAVED_USERS_JSON] ?: "[]"
        val type = object : TypeToken<List<SavedUser>>() {}.type
        runCatching { gson.fromJson<List<SavedUser>>(json, type) }.getOrElse { emptyList() }
    }

    suspend fun add(user: SavedUser) {
        context.dataStore.edit { prefs ->
            val current = getCurrent(prefs)
            val updated = (current.filter { it.email != user.email } + user)
            prefs[KEY_SAVED_USERS_JSON] = gson.toJson(updated)
        }
    }

    suspend fun remove(email: String) {
        context.dataStore.edit { prefs ->
            val current = getCurrent(prefs)
            val updated = current.filter { it.email != email }
            prefs[KEY_SAVED_USERS_JSON] = gson.toJson(updated)
        }
    }

    private fun getCurrent(prefs: androidx.datastore.preferences.core.Preferences): List<SavedUser> {
        val json = prefs[KEY_SAVED_USERS_JSON] ?: "[]"
        val type = object : TypeToken<List<SavedUser>>() {}.type
        return runCatching { gson.fromJson<List<SavedUser>>(json, type) }.getOrElse { emptyList() }
    }
}
