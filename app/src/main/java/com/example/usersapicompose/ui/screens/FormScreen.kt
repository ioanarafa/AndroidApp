package com.example.usersapicompose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

private val NATS = listOf(
    "US" to "USA",
    "GB" to "UK",
    "FR" to "France",
    "DE" to "Germany",
    "RO" to "Romania"
)

enum class InfoField(val label: String, val apiValue: String) {
    GENDER("Gen", "gender"),
    NAME("Nume", "name"),
    LOCATION("Locatie", "location"),
    EMAIL("Email", "email"),
    PICTURE("Poza", "picture")
}

@Composable
fun FormScreen(
    onGenerate: (results: Int, natCsv: String, incCsv: String, selectedInfo: Set<InfoField>) -> Unit
) {
    var resultsText by remember { mutableStateOf("") }
    var selectedNats by remember { mutableStateOf(setOf<String>()) }
    var selectedInfo by remember { mutableStateOf(setOf<InfoField>()) }

    var errResults by remember { mutableStateOf<String?>(null) }
    var errNats by remember { mutableStateOf<String?>(null) }
    var errInfo by remember { mutableStateOf<String?>(null) }
    var errPicture by remember { mutableStateOf<String?>(null) }

    fun validate(): Int? {
        errResults = null; errNats = null; errInfo = null; errPicture = null

        val results = resultsText.toIntOrNull()
        if (results == null || results !in 3..10) {
            errResults = "Numarul de inregistrari trebuie sa fie intre 3 si 10."
        }

        if (selectedNats.size < 2) {
            errNats = "Selecteaza minim 2 nationalitati."
        }

        if (selectedInfo.size < 3) {
            errInfo = "Selecteaza minim 3 tipuri de informatii."
        }

        if (!selectedInfo.contains(InfoField.PICTURE)) {
            errPicture = "Poza este obligatorie."
        }

        return if (errResults == null && errNats == null && errInfo == null && errPicture == null) results else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Generare utilizatori", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = resultsText,
            onValueChange = { resultsText = it },
            label = { Text("Numar de inregistrari (3-10)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = errResults != null
        )
        if (errResults != null) {
            Text(errResults!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        Text("Nationalitati (minim 2)", style = MaterialTheme.typography.titleMedium)
        NATS.forEach { (code, label) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$label ($code)")
                Checkbox(
                    checked = selectedNats.contains(code),
                    onCheckedChange = { checked ->
                        selectedNats = if (checked) selectedNats + code else selectedNats - code
                    }
                )
            }
        }
        if (errNats != null) {
            Text(errNats!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        Text("Informatii (minim 3, poza obligatorie)", style = MaterialTheme.typography.titleMedium)
        InfoField.values().forEach { f ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(f.label)
                Checkbox(
                    checked = selectedInfo.contains(f),
                    onCheckedChange = { checked ->
                        selectedInfo = if (checked) selectedInfo + f else selectedInfo - f
                    }
                )
            }
        }
        if (errInfo != null) {
            Text(errInfo!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        if (errPicture != null) {
            Text(errPicture!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val okResults = validate() ?: return@Button
                val natCsv = selectedNats.joinToString(",")
                val incSet = selectedInfo.map { it.apiValue }.toMutableSet()
                incSet.add("email")
                val incCsv = incSet.joinToString(",")

                onGenerate(okResults, natCsv, incCsv, selectedInfo)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Genereaza utilizatori")
        }
    }
}
