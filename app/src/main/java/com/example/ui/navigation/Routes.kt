package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

@Serializable
data object CattleListRoute

@Serializable
data class CattleDetailRoute(val cattleId: Long)

@Serializable
data class CattleFormRoute(val cattleId: Long = 0L)

@Serializable
data object MilkTrackerRoute

@Serializable
data class FarmCareRoute(val initialTab: Int = 0)

@Serializable
data class FinanceReportsRoute(val initialTab: Int = 0)
