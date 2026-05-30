package com.android.rickmortyandroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.rickmortyandroid.feature.characters.ui.detail.CharacterDetailScreen
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
            CharacterListScreen(
                onNavigateToDetail = { characterId ->
                    navController.navigate("character_detail/$characterId")
                }
            )
        }

        composable(
            route = "character_detail/{characterId}",
            arguments = listOf(
                navArgument("characterId") { type = NavType.IntType }
            )
        ) {
            CharacterDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
