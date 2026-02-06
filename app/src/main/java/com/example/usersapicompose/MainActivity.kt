package com.example.usersapicompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.usersapicompose.ui.screens.FormScreen
import com.example.usersapicompose.ui.screens.ResultsScreen
import com.example.usersapicompose.ui.theme.UsersApiComposeTheme
import com.example.usersapicompose.ui.viewmodel.UsersViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UsersApiComposeTheme {
                val nav = rememberNavController()
                val vm: UsersViewModel = viewModel()

                NavHost(navController = nav, startDestination = "form") {

                    composable("form") {
                        FormScreen { results, natCsv, incCsv, selectedInfo ->

                            vm.setSelectedApiFields(selectedInfo.map { it.apiValue }.toSet())
                            vm.fetchUsers(results, natCsv, incCsv)
                            nav.navigate("results")
                        }
                    }

                    composable("results") {
                        ResultsScreen(vm = vm)
                    }
                }
            }
        }
    }
}
