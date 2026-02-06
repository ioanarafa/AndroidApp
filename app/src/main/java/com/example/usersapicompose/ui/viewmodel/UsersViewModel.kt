package com.example.usersapicompose.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.usersapicompose.data.model.SavedUser
import com.example.usersapicompose.data.model.UserDto
import com.example.usersapicompose.data.repo.UsersRepository
import com.example.usersapicompose.datastore.SavedUsersStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class UserMark { NONE, REPORTED, SAVED }
enum class TopFilter { LAST_QUERY, ONLY_SAVED }

data class UserUi(
    val user: UserDto,
    val mark: UserMark = UserMark.NONE
)

class UsersViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = UsersRepository()
    private val store = SavedUsersStore(app.applicationContext)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _topFilter = MutableStateFlow(TopFilter.LAST_QUERY)
    val topFilter: StateFlow<TopFilter> = _topFilter.asStateFlow()

    private val _lastQueryUsers = MutableStateFlow<List<UserUi>>(emptyList())
    val lastQueryUsers: StateFlow<List<UserUi>> = _lastQueryUsers.asStateFlow()

    private val _savedUsers = MutableStateFlow<List<SavedUser>>(emptyList())
    val savedUsers: StateFlow<List<SavedUser>> = _savedUsers.asStateFlow()

    private val _selectedApiFields = MutableStateFlow<Set<String>>(
        setOf("gender", "name", "location", "email", "picture")
    )
    val selectedApiFields: StateFlow<Set<String>> = _selectedApiFields.asStateFlow()

    init {
        viewModelScope.launch {
            store.savedUsers.collect { list ->
                _savedUsers.value = list
                val savedEmails = list.map { it.email }.toSet()

                _lastQueryUsers.value = _lastQueryUsers.value.map { ui ->
                    val email = ui.user.email.orEmpty()
                    if (email.isNotBlank() && savedEmails.contains(email) && ui.mark != UserMark.REPORTED) {
                        ui.copy(mark = UserMark.SAVED)
                    } else ui
                }
            }
        }
    }

    fun setSelectedApiFields(fields: Set<String>) {
        _selectedApiFields.value = fields
    }

    fun setTopFilter(filter: TopFilter) {
        _topFilter.value = filter
    }

    fun fetchUsers(results: Int, natCsv: String, incCsv: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val start = System.currentTimeMillis()

            val users = try {
                repo.fetchUsers(results, natCsv, incCsv)
            } catch (e: Exception) {
                emptyList()
            }


            val elapsed = System.currentTimeMillis() - start
            val remaining = 2000L - elapsed
            if (remaining > 0) delay(remaining)

            val savedEmails = _savedUsers.value.map { it.email }.toSet()

            _lastQueryUsers.value = users.map { u ->
                val email = u.email.orEmpty()
                val mark = if (email.isNotBlank() && savedEmails.contains(email)) UserMark.SAVED else UserMark.NONE
                UserUi(user = u, mark = mark)
            }

            _topFilter.value = TopFilter.LAST_QUERY
            _isLoading.value = false
        }
    }

    fun reportUser(key: String) {
        _lastQueryUsers.value = _lastQueryUsers.value.map { ui ->
            val email = ui.user.email.orEmpty()
            val name = ui.user.fullName()
            if (email == key || name == key) ui.copy(mark = UserMark.REPORTED) else ui
        }
    }

    fun resetUser(key: String) {

        val emailToRemove = _lastQueryUsers.value.firstOrNull { ui ->
            ui.user.email.orEmpty() == key || ui.user.fullName() == key
        }?.user?.email.orEmpty()

        viewModelScope.launch {
            if (emailToRemove.isNotBlank()) {
                store.remove(emailToRemove)
            }
        }

        _lastQueryUsers.value = _lastQueryUsers.value.map { ui ->
            val email = ui.user.email.orEmpty()
            val name = ui.user.fullName()
            if (email == key || name == key) ui.copy(mark = UserMark.NONE) else ui
        }
    }

    fun saveUser(user: UserDto) {
        viewModelScope.launch {
            val email = user.email.orEmpty()
            if (email.isBlank()) return@launch

            val saved = SavedUser(
                email = email,
                gender = user.gender.orEmpty(),
                fullName = user.fullName(),
                location = user.fullLocation(),
                pictureUrl = user.pictureUrl()
            )
            store.add(saved)

            _lastQueryUsers.value = _lastQueryUsers.value.map { ui ->
                if (ui.user.email == email && ui.mark != UserMark.REPORTED) ui.copy(mark = UserMark.SAVED) else ui
            }
        }
    }
}
