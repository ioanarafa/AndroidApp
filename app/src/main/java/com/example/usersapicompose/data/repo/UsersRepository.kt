package com.example.usersapicompose.data.repo

import com.example.usersapicompose.data.api.RetrofitClient
import com.example.usersapicompose.data.model.UserDto

class UsersRepository {

    suspend fun fetchUsers(
        results: Int,
        natCsv: String,
        incCsv: String
    ): List<UserDto> {
        val response = RetrofitClient.api.getUsers(
            results = results,
            nat = natCsv,
            inc = incCsv
        )
        return response.results
    }
}
