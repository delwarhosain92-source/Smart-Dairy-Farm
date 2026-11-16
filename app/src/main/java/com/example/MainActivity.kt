package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MedicalServices
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.local.CattleEntity
import com.example.data.preferences.UserRole
import com.example.ui.components.FarmSettingsSheet
import com.example.ui.components.FarmTopBar
import com.example.ui.navigation.CattleDetailRoute
import com.example.ui.navigation.CattleFormRoute
import com.example.ui.navigation.CattleListRoute
import com.example.ui.navigation.DashboardRoute
import com.example.ui.navigation.FarmCareRoute
import com.example.ui.navigation.FinanceReportsRoute
import com.example.ui.navigation.MilkTrackerRoute
import com.example.ui.screens.CattleDetailScreen
import com.example.ui.screens.CattleFormScreen
import com.example.ui.screens.CattleScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FarmCareScreen
import com.example.ui.screens.FinanceReportsScreen
import com.example.ui.screens.HealthRecordModalSheet
import com.example.ui.screens.LogMilkModalSheet
import com.example.ui.screens.MilkScreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.SmartDairyTheme
import com.example.ui.viewmodel.DairyViewModel
import com.example.util.DateUtils

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: DairyViewModel = viewModel()
      val uiState by viewModel.uiState.collectAsStateWithLifecycle()

      SmartDairyTheme(
        language = uiState.settings.language,
        currencySymbol = uiState.settings.currency.symbol,
      ) {
        SmartDairyApp(viewModel = viewModel)
      }
    }
  }
}

private enum class TopLevelTab(val testTag: String) {
  DASHBOARD("nav_dashboard"),
  CATTLE("nav_cattle"),
  MILK("nav_milk"),
  CARE("nav_care"),
  FINANCE("nav_finance"),
}

@Composable
private fun SmartDairyApp(viewModel: DairyViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val strings = LocalAppStrings.current
  val navController = rememberNavController()
  val backStackEntry by navController.currentBackStackEntryAsState()
  val currentDestination = backStackEntry?.destination

  var showSettingsSheet by remember { mutableStateOf(false) }

  var showLogMilkSheet by remember { mutableStateOf(false) }
  var preselectedMilkCow by remember { mutableStateOf<CattleEntity?>(null) }
  var logMilkDate by remember { mutableLongStateOf(DateUtils.todayEpochDay()) }

  var showAddHealthSheet by remember { mutableStateOf(false) }
  var preselectedHealthCow by remember { mutableStateOf<CattleEntity?>(null) }

  // Explicit tab state for Care (0 = Feed, 1 = Health & Vaccines) and Finance (0 = Ledger, 1 = Reports)
  var careSelectedTab by remember { mutableIntStateOf(1) }
  var financeSelectedTab by remember { mutableIntStateOf(0) }

  val isDetailScreen = currentDestination?.hasRoute<CattleDetailRoute>() == true
  val isCattleFormScreen = currentDestination?.hasRoute<CattleFormRoute>() == true
  val isSubRoute = isDetailScreen || isCattleFormScreen
  val activeTab = when {
    currentDestination?.hasRoute<CattleListRoute>() == true || isSubRoute -> TopLevelTab.CATTLE
    currentDestination?.hasRoute<MilkTrackerRoute>() == true -> TopLevelTab.MILK
    currentDestination?.hasRoute<FarmCareRoute>() == true -> TopLevelTab.CARE
    currentDestination?.hasRoute<FinanceReportsRoute>() == true -> TopLevelTab.FINANCE
    else -> TopLevelTab.DASHBOARD
  }

  val topBarTitle = when {
    isCattleFormScreen -> strings.addCattle
    isDetailScreen -> strings.cattleDetails
    activeTab == TopLevelTab.CATTLE -> strings.cattleRoster
    activeTab == TopLevelTab.MILK -> strings.dailyMilkTracking
    activeTab == TopLevelTab.CARE -> "${strings.tabFeed} & ${strings.tabHealth}"
    activeTab == TopLevelTab.FINANCE -> strings.navReports
    else -> strings.appName
  }

  fun openDashboard() {
    navController.navigate(DashboardRoute) {
      popUpTo<DashboardRoute> { inclusive = true }
      launchSingleTop = true
    }
  }

  fun openCattle() {
    navController.navigate(CattleListRoute) {
      popUpTo<DashboardRoute> { inclusive = false }
      launchSingleTop = true
    }
  }

  fun openMilk() {
    navController.navigate(MilkTrackerRoute) {
      popUpTo<DashboardRoute> { inclusive = false }
      launchSingleTop = true
    }
  }

  fun openCare(tabIndex: Int) {
    careSelectedTab = tabIndex.coerceIn(0, 1)
    navController.navigate(FarmCareRoute(careSelectedTab)) {
      popUpTo<DashboardRoute> { inclusive = false }
      launchSingleTop = true
    }
  }

  fun openFinance(tabIndex: Int) {
    financeSelectedTab = tabIndex.coerceIn(0, 1)
    navController.navigate(FinanceReportsRoute(financeSelectedTab)) {
      popUpTo<DashboardRoute> { inclusive = false }
      launchSingleTop = true
    }
  }

  fun openTab(tab: TopLevelTab) {
    when (tab) {
      TopLevelTab.DASHBOARD -> openDashboard()
      TopLevelTab.CATTLE -> openCattle()
      TopLevelTab.MILK -> openMilk()
      TopLevelTab.CARE -> openCare(1) // Default Care tab shows Medical & Vaccination info
      TopLevelTab.FINANCE -> openFinance(0) // Default Finance tab shows Income & Expense
    }
  }

  // System Back on any top-level non-Home tab returns to Dashboard
  BackHandler(enabled = activeTab != TopLevelTab.DASHBOARD && !isSubRoute) {
    openDashboard()
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useNavRail = maxWidth >= 600.dp

    Scaffold(
      containerColor = MaterialTheme.colorScheme.background,
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      topBar = {
        FarmTopBar(
          title = topBarTitle,
          userRole = uiState.settings.userRole,
          showBackButton = activeTab != TopLevelTab.DASHBOARD,
          onBackClick = {
            if (isSubRoute) {
              navController.popBackStack()
            } else {
              openDashboard()
            }
          },
          onToggleLanguage = { viewModel.toggleLanguage() },
          onOpenSettings = { showSettingsSheet = true },
        )
      },
      bottomBar = {
        if (!useNavRail && !isSubRoute) {
          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp,
          ) {
            TopLevelTab.entries.forEach { tab ->
              val selected = activeTab == tab
              val label = when (tab) {
                TopLevelTab.DASHBOARD -> strings.navDashboard
                TopLevelTab.CATTLE -> strings.navCattle
                TopLevelTab.MILK -> strings.navMilk
                TopLevelTab.CARE -> strings.navCare
                TopLevelTab.FINANCE -> strings.navReports
              }
              NavigationBarItem(
                selected = selected,
                onClick = { openTab(tab) },
                icon = { TabIcon(tab = tab, selected = selected, contentDescription = label) },
                label = { Text(text = label, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                  selectedIconColor = EmeraldDark,
                  selectedTextColor = EmeraldDark,
                  indicatorColor = MintSoft,
                  unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                  unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                modifier = Modifier.testTag(tab.testTag),
              )
            }
          }
        }
      },
    ) { innerPadding ->
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding),
      ) {
        if (useNavRail && !isSubRoute) {
          NavigationRail(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxHeight(),
          ) {
            TopLevelTab.entries.forEach { tab ->
              val selected = activeTab == tab
              val label = when (tab) {
                TopLevelTab.DASHBOARD -> strings.navDashboard
                TopLevelTab.CATTLE -> strings.navCattle
                TopLevelTab.MILK -> strings.navMilk
                TopLevelTab.CARE -> strings.navCare
                TopLevelTab.FINANCE -> strings.navReports
              }
              NavigationRailItem(
                selected = selected,
                onClick = { openTab(tab) },
                icon = { TabIcon(tab = tab, selected = selected, contentDescription = label) },
                label = { Text(text = label, maxLines = 1) },
                colors = NavigationRailItemDefaults.colors(
                  selectedIconColor = EmeraldDark,
                  selectedTextColor = EmeraldDark,
                  indicatorColor = MintSoft,
                ),
                modifier = Modifier.testTag("${tab.testTag}_rail"),
              )
            }
          }
        }

        Box(modifier = Modifier.weight(1f)) {
          NavHost(
            navController = navController,
            startDestination = DashboardRoute,
          ) {
            composable<DashboardRoute> {
              DashboardScreen(
                uiState = uiState,
                onNavigateCattle = { openCattle() },
                onNavigateMilk = { openMilk() },
                onNavigateFeed = { openCare(0) },
                onNavigateVaccines = { openCare(1) },
                onNavigateFinance = { openFinance(0) },
                onNavigateReports = { openFinance(1) },
                onQuickAddCattle = {
                  openCattle()
                  navController.navigate(CattleFormRoute(0L)) { launchSingleTop = true }
                },
                onQuickLogMilk = {
                  openMilk()
                  if (uiState.cattle.isNotEmpty()) {
                    preselectedMilkCow = null
                    logMilkDate = DateUtils.todayEpochDay()
                    showLogMilkSheet = true
                  }
                },
              )
            }

            composable<CattleListRoute> {
              CattleScreen(
                cattleList = uiState.cattle,
                userRole = uiState.settings.userRole,
                onSelectCattle = { cattleId ->
                  navController.navigate(CattleDetailRoute(cattleId))
                },
                onAddCattleClick = {
                  navController.navigate(CattleFormRoute(0L)) { launchSingleTop = true }
                },
                onEditCattleClick = { cow ->
                  navController.navigate(CattleFormRoute(cow.id)) { launchSingleTop = true }
                },
                onDeleteCattle = { id -> viewModel.deleteCattle(id) },
              )
            }

            composable<CattleFormRoute> { entry ->
              val args = entry.toRoute<CattleFormRoute>()
              val existingCow = remember(args.cattleId, uiState.cattle) {
                if (args.cattleId == 0L) null
                else uiState.cattle.firstOrNull { it.id == args.cattleId }
              }
              CattleFormScreen(
                existingCattle = existingCow,
                onSave = { entity, photoUri ->
                  viewModel.saveCattle(entity, photoUri)
                  navController.popBackStack()
                },
                onCancel = {
                  navController.popBackStack()
                },
              )
            }

            composable<CattleDetailRoute> { entry ->
              val args = entry.toRoute<CattleDetailRoute>()
              val cow by viewModel.observeCattleById(args.cattleId).collectAsStateWithLifecycle(initialValue = null)
              val milkHistory by viewModel.observeMilkForCattle(args.cattleId).collectAsStateWithLifecycle(initialValue = emptyList())
              val healthHistory by viewModel.observeHealthForCattle(args.cattleId).collectAsStateWithLifecycle(initialValue = emptyList())

              CattleDetailScreen(
                cow = cow,
                milkHistory = milkHistory,
                healthHistory = healthHistory,
                userRole = uiState.settings.userRole,
                onEditClick = { target ->
                  navController.navigate(CattleFormRoute(target.id))
                },
                onDeleteConfirm = { id ->
                  viewModel.deleteCattle(id)
                  navController.popBackStack()
                },
                onQuickLogMilk = { target ->
                  preselectedMilkCow = target
                  logMilkDate = DateUtils.todayEpochDay()
                  showLogMilkSheet = true
                },
                onQuickAddHealth = { target ->
                  preselectedHealthCow = target
                  showAddHealthSheet = true
                },
              )
            }

            composable<MilkTrackerRoute> {
              MilkScreen(
                milkRecords = uiState.milkRecords,
                cattleList = uiState.cattle,
                userRole = uiState.settings.userRole,
                onOpenLogMilk = { date ->
                  if (uiState.cattle.isNotEmpty()) {
                    preselectedMilkCow = null
                    logMilkDate = date
                    showLogMilkSheet = true
                  } else {
                    openCattle()
                  }
                },
                onDeleteMilkRecord = { id -> viewModel.deleteMilkRecord(id) },
              )
            }

            composable<FarmCareRoute> {
              FarmCareScreen(
                selectedTab = careSelectedTab,
                onSelectTab = { careSelectedTab = it },
                feedItems = uiState.feedItems,
                feedLogs = uiState.feedLogs,
                healthRecords = uiState.healthRecords,
                cattleList = uiState.cattle,
                userRole = uiState.settings.userRole,
                onSaveFeedItem = { viewModel.saveFeedItem(it) },
                onDeleteFeedItem = { viewModel.deleteFeedItem(it) },
                onLogFeedConsumption = { item, qty, notes ->
                  viewModel.logFeedConsumption(item, qty, notes)
                },
                onRestockFeed = { item, qty, notes, alsoExpense ->
                  viewModel.restockFeedItem(item, qty, notes, alsoExpense)
                },
                onOpenAddHealth = {
                  if (uiState.cattle.isNotEmpty()) {
                    preselectedHealthCow = null
                    showAddHealthSheet = true
                  } else {
                    openCattle()
                  }
                },
                onToggleHealthCompleted = { viewModel.toggleHealthCompleted(it) },
                onDeleteHealthRecord = { viewModel.deleteHealthRecord(it) },
              )
            }

            composable<FinanceReportsRoute> {
              FinanceReportsScreen(
                selectedTab = financeSelectedTab,
                onSelectTab = { financeSelectedTab = it },
                uiState = uiState,
                onSaveTransaction = { viewModel.saveTransaction(it) },
                onDeleteTransaction = { viewModel.deleteTransaction(it) },
                onSwitchToAdmin = { viewModel.setUserRole(UserRole.ADMIN) },
              )
            }
          }
        }
      }
    }
  }

  // Shared Modals
  if (showSettingsSheet) {
    FarmSettingsSheet(
      settings = uiState.settings,
      onSelectRole = { viewModel.setUserRole(it) },
      onSelectLanguage = { viewModel.setLanguage(it) },
      onSelectCurrency = { viewModel.setCurrency(it) },
      onSaveFarmName = { viewModel.setFarmName(it) },
      onLoadSampleData = { viewModel.loadSampleData() },
      onClearAllData = { viewModel.clearAllData() },
      onDismiss = { showSettingsSheet = false },
    )
  }

  if (showLogMilkSheet && uiState.cattle.isNotEmpty()) {
    LogMilkModalSheet(
      cattleList = uiState.cattle,
      preselectedCow = preselectedMilkCow,
      dateEpochDay = logMilkDate,
      onSave = { cow, date, shift, liters, fat, notes ->
        viewModel.saveMilkRecord(
          cattle = cow,
          dateEpochDay = date,
          shift = shift,
          liters = liters,
          fatPercentage = fat,
          notes = notes,
        )
        showLogMilkSheet = false
      },
      onDismiss = { showLogMilkSheet = false },
    )
  }

  if (showAddHealthSheet && uiState.cattle.isNotEmpty()) {
    HealthRecordModalSheet(
      cattleList = uiState.cattle,
      preselectedCow = preselectedHealthCow,
      onSave = { cow, type, title, date, nextDue, vet, cost, notes, completed ->
        viewModel.saveHealthRecord(
          cattle = cow,
          recordType = type,
          title = title,
          dateEpochDay = date,
          nextDueEpochDay = nextDue,
          vetName = vet,
          cost = cost,
          notes = notes,
          isCompleted = completed,
        )
        showAddHealthSheet = false
      },
      onDismiss = { showAddHealthSheet = false },
    )
  }
}

@Composable
private fun TabIcon(
  tab: TopLevelTab,
  selected: Boolean,
  contentDescription: String,
) {
  when (tab) {
    TopLevelTab.DASHBOARD -> Icon(
      imageVector = if (selected) Icons.Rounded.Home else Icons.Outlined.Home,
      contentDescription = contentDescription,
    )
    TopLevelTab.CATTLE -> Icon(
      painter = painterResource(id = R.drawable.ic_cow),
      contentDescription = contentDescription,
      modifier = Modifier.size(22.dp),
    )
    TopLevelTab.MILK -> Icon(
      imageVector = if (selected) Icons.Rounded.WaterDrop else Icons.Outlined.WaterDrop,
      contentDescription = contentDescription,
    )
    TopLevelTab.CARE -> Icon(
      imageVector = if (selected) Icons.Rounded.MedicalServices else Icons.Outlined.MedicalServices,
      contentDescription = contentDescription,
    )
    TopLevelTab.FINANCE -> Icon(
      imageVector = if (selected) Icons.Rounded.Assessment else Icons.Outlined.Assessment,
      contentDescription = contentDescription,
    )
  }
}
