package com.android.rickmortyandroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.rickmortyandroid.feature.characters.ui.list.CharacterListScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "character_list"
    ) {
        composable("character_list") {
            CharacterListScreen()
        }
    }
}
