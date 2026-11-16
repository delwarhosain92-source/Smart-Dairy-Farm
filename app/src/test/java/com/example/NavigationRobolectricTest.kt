package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import com.example.data.local.SmartDairyDatabase
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NavigationRobolectricTest {

  private val composeTestRule = createAndroidComposeRule<MainActivity>()

  @get:Rule
  val ruleChain: RuleChain = RuleChain
    .outerRule(object : TestWatcher() {
      override fun starting(description: Description) {
        SmartDairyDatabase.resetForTesting()
      }

      override fun finished(description: Description) {
        SmartDairyDatabase.resetForTesting()
      }
    })
    .around(composeTestRule)

  @Test
  fun bottomNavAndTopBarBack_navigatesToAllPagesAndReturnsHome() {
    composeTestRule.waitForIdle()

    // 1. Dashboard shown initially
    composeTestRule.onNodeWithTag("kpi_total_cattle").assertIsDisplayed()

    // 2. Cattle tab opens cattle list & Add Cow controls, then back to Home
    composeTestRule.onNodeWithTag("nav_cattle").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cattle_search_input").assertIsDisplayed()
    composeTestRule.onNodeWithTag("add_cattle_top_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("top_bar_back_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("kpi_total_cattle").assertIsDisplayed()

    // 3. Milk tab opens milk tracking & Log Milk controls, then Home tab returns
    composeTestRule.onNodeWithTag("nav_milk").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("milk_prev_day").assertIsDisplayed()
    composeTestRule.onNodeWithTag("log_milk_top_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_dashboard").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("kpi_total_cattle").assertIsDisplayed()

    // 4. Care tab opens Vaccination & Medical info directly, then back to Home
    composeTestRule.onNodeWithTag("nav_care").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("tab_health").assertIsDisplayed()
    composeTestRule.onNodeWithTag("top_bar_back_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("kpi_total_cattle").assertIsDisplayed()

    // 5. Finance tab opens Income & Expense ledger directly, then Home tab returns
    composeTestRule.onNodeWithTag("nav_finance").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("tab_finance").assertIsDisplayed()
    composeTestRule.onNodeWithTag("fab_add_transaction").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_dashboard").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("kpi_total_cattle").assertIsDisplayed()
  }

  @Test
  fun addCowButtons_openFormSaveNewCowAndCancelCleanly() {
    composeTestRule.waitForIdle()

    // Navigate to Cattle page
    composeTestRule.onNodeWithTag("nav_cattle").performClick()
    composeTestRule.waitForIdle()

    // 1. Click top Add Cow (+) button -> Form opens -> Cancel returns to Cattle list
    composeTestRule.onNodeWithTag("add_cattle_top_button").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cattle_id_input").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cattle_dob_button").performScrollTo().assertIsDisplayed()
    composeTestRule.onNodeWithTag("save_cattle_button").performScrollTo()
    composeTestRule.onNodeWithTag("milk_status_chip_MILKING").assertIsDisplayed()
    composeTestRule.onNodeWithTag("cancel_cattle_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cattle_search_input").assertIsDisplayed()

    // 2. Click bottom-right FAB Add Cow (+) button -> Form opens -> Enter fields & Save -> Cow appears in list
    composeTestRule.onNodeWithTag("fab_add_cattle").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cattle_id_input").performScrollTo().performTextReplacement("COW-777")
    composeTestRule.onNodeWithTag("cattle_name_input").performScrollTo().performTextReplacement("Tara")
    composeTestRule.onNodeWithTag("cattle_breed_input").performScrollTo().performTextReplacement("Jersey")
    composeTestRule.onNodeWithTag("cattle_weight_input").performScrollTo().performTextReplacement("465")
    composeTestRule.onNodeWithTag("cattle_notes_input").performScrollTo()
    composeTestRule.onNodeWithTag("health_chip_HEALTHY").performClick()
    composeTestRule.onNodeWithTag("milk_status_chip_MILKING").performClick()
    composeTestRule.onNodeWithTag("save_cattle_button").performScrollTo().performClick()
    composeTestRule.waitForIdle()

    // Verify we returned to Cattle list and new cow is displayed
    composeTestRule.onNodeWithTag("cattle_search_input").assertIsDisplayed()
    composeTestRule.waitUntil(timeoutMillis = 10000) {
      val foundDirectly = composeTestRule.onAllNodesWithTag("cattle_card_COW-777").fetchSemanticsNodes().isNotEmpty()
      if (foundDirectly) {
        true
      } else {
        runCatching {
          composeTestRule.onNodeWithTag("cattle_list").performScrollToNode(hasTestTag("cattle_card_COW-777"))
        }.isSuccess
      }
    }
    composeTestRule.onNodeWithTag("cattle_list").performScrollToNode(hasTestTag("cattle_card_COW-777"))
    composeTestRule.onNodeWithText("Tara").assertIsDisplayed()
  }

  @Test
  fun dashboardQuickActions_openTargetPagesAndTabs() {
    composeTestRule.waitForIdle()

    // Wait for Room initial seed
    composeTestRule.waitUntil(timeoutMillis = 5000) {
      composeTestRule.onAllNodesWithTag("kpi_total_cattle").fetchSemanticsNodes().isNotEmpty()
    }

    // Quick Action: Feed Stock -> Care Feed tab
    composeTestRule.onNodeWithTag("dashboard_list").performScrollToNode(hasTestTag("quick_feed"))
    composeTestRule.onNodeWithTag("quick_feed").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("fab_add_feed").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_dashboard").performClick()
    composeTestRule.waitForIdle()

    // Quick Action: Vaccines -> Care Health tab
    composeTestRule.onNodeWithTag("dashboard_list").performScrollToNode(hasTestTag("quick_vaccines"))
    composeTestRule.onNodeWithTag("quick_vaccines").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("tab_health").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_dashboard").performClick()
    composeTestRule.waitForIdle()

    // Quick Action: Add Ledger -> Finance tab
    composeTestRule.onNodeWithTag("dashboard_list").performScrollToNode(hasTestTag("quick_finance"))
    composeTestRule.onNodeWithTag("quick_finance").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("fab_add_transaction").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_dashboard").performClick()
    composeTestRule.waitForIdle()

    // Quick Action: Reports -> Reports tab
    composeTestRule.onNodeWithTag("dashboard_list").performScrollToNode(hasTestTag("quick_reports"))
    composeTestRule.onNodeWithTag("quick_reports").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("share_report_button").assertIsDisplayed()
    composeTestRule.onNodeWithTag("nav_dashboard").performClick()
    composeTestRule.waitForIdle()

    // Quick Action: Add Cow -> Cattle form screen
    composeTestRule.onNodeWithTag("dashboard_list").performScrollToNode(hasTestTag("quick_add_cow"))
    composeTestRule.onNodeWithTag("quick_add_cow").performClick()
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("cattle_id_input").assertIsDisplayed()
  }
}
