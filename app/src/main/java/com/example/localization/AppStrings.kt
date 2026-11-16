package com.example.localization

import com.example.data.preferences.UserRole

interface AppStrings {
  val appName: String
  val offlineBadge: String

  // Navigation Tabs
  val navDashboard: String
  val navCattle: String
  val navMilk: String
  val navCare: String
  val navReports: String

  // Roles & Settings
  val roleAdmin: String
  val roleFarmer: String
  val switchRole: String
  val adminRoleDesc: String
  val farmerRoleDesc: String
  val settingsTitle: String
  val languageLabel: String
  val currencyLabel: String
  val farmNameLabel: String
  val saveSettings: String
  val dataManagement: String
  val loadSampleData: String
  val clearAllData: String
  val sampleDataLoadedMsg: String
  val allDataClearedMsg: String
  val adminOnlyAction: String
  val switchToAdminBtn: String

  // Dashboard
  val welcomeFarmer: String
  val welcomeAdmin: String
  val todayOverview: String
  val totalCattle: String
  val milkingCows: String
  val todaysMilk: String
  val monthlyProfit: String
  val quickActions: String
  val actionAddCow: String
  val actionLogMilk: String
  val actionManageFeed: String
  val actionVaccines: String
  val actionAddFinance: String
  val actionViewReports: String
  val weeklyMilkTrend: String
  val farmAlerts: String
  val noAlertsMessage: String
  val vaccineDueAlert: String
  val lowFeedAlert: String
  val treatmentAlert: String
  val recentActivity: String
  val litersUnit: String
  val kgUnit: String
  val daysShort: String
  val yearsShort: String
  val monthsShort: String

  // Cattle
  val cattleRoster: String
  val searchCattleHint: String
  val filterAll: String
  val filterHealthy: String
  val filterTreatment: String
  val filterPregnant: String
  val filterMilking: String
  val addCattle: String
  val editCattle: String
  val deleteCattle: String
  val confirmDeleteCattle: String
  val cowIdLabel: String
  val cowNameLabel: String
  val breedLabel: String
  val dateOfBirthLabel: String
  val ageYearsLabel: String
  val ageMonthsLabel: String
  val weightKgLabel: String
  val healthStatusLabel: String
  val lactationStatusLabel: String
  val healthNotesLabel: String
  val selectPhoto: String
  val changePhoto: String
  val cattleDetails: String
  val vitalsSection: String
  val milkHistorySection: String
  val healthHistorySection: String
  val emptyCattleTitle: String
  val emptyCattleSubtitle: String
  val ageLabel: String
  val weightLabel: String

  // Health & Lactation statuses
  fun healthStatusLabel(status: String): String
  fun lactationStatusLabel(status: String): String
  fun breedDisplay(breed: String): String

  // Milk Production
  val dailyMilkTracking: String
  val logMilk: String
  val editMilkLog: String
  val todayLabel: String
  val yesterdayLabel: String
  val morningShift: String
  val eveningShift: String
  val selectCow: String
  val shiftLabel: String
  val quantityLiters: String
  val fatPercentOptional: String
  val notesOptional: String
  val totalYield: String
  val avgPerEntry: String
  val emptyMilkTitle: String
  val emptyMilkSubtitle: String

  // Farm Care (Feed & Health)
  val tabFeed: String
  val tabHealth: String
  val feedInventory: String
  val addFeedItem: String
  val editFeedItem: String
  val feedNameLabel: String
  val feedCategoryLabel: String
  val stockKgLabel: String
  val dailyUsageKgLabel: String
  val reorderLevelKgLabel: String
  val costPerKgLabel: String
  val logFeeding: String
  val restockFeed: String
  val quantityKgLabel: String
  val daysLeftLabel: String
  val lowStockBadge: String
  val inStockBadge: String
  val autoRecordExpense: String
  val recentFeedLogs: String
  val emptyFeedTitle: String
  val emptyFeedSubtitle: String
  fun feedCategoryDisplay(category: String): String

  // Health & Vaccination
  val healthAndVaccination: String
  val addHealthRecord: String
  val filterDue: String
  val filterVaccines: String
  val filterMedical: String
  val recordTypeLabel: String
  val recordTitleLabel: String
  val nextDueDaysLabel: String
  val vetNameLabel: String
  val costLabel: String
  val markCompleted: String
  val completedBadge: String
  val dueBadge: String
  val overdueBadge: String
  val emptyHealthTitle: String
  val emptyHealthSubtitle: String
  fun healthRecordTypeDisplay(type: String): String

  // Finance & Reports
  val tabFinance: String
  val tabReports: String
  val incomeAndExpense: String
  val totalIncome: String
  val totalExpense: String
  val netBalance: String
  val addTransaction: String
  val incomeLabel: String
  val expenseLabel: String
  val categoryLabel: String
  val amountLabel: String
  val descriptionLabel: String
  val farmerFinanceNoticeTitle: String
  val farmerFinanceNoticeBody: String
  val emptyFinanceTitle: String
  val emptyFinanceSubtitle: String
  fun transactionCategoryDisplay(category: String): String

  // Reports
  val farmReportsTitle: String
  val shareReport: String
  val milkLast7Days: String
  val incomeVsExpense: String
  val expenseBreakdown: String
  val herdComposition: String
  val breedBreakdown: String
  val healthBreakdown: String
  val avgDailyMilk: String
  val totalMilk7Days: String
  val reportSummaryHeader: String

  // Common Buttons & Validation
  val save: String
  val cancel: String
  val delete: String
  val edit: String
  val close: String
  val confirm: String
  val validationRequired: String
  val validationPositiveNumber: String

  fun roleDisplay(role: UserRole): String = when (role) {
    UserRole.ADMIN -> roleAdmin
    UserRole.FARMER -> roleFarmer
  }
}

object EnglishStrings : AppStrings {
  override val appName = "Smart Dairy Farm"
  override val offlineBadge = "Offline Ready"

  override val navDashboard = "Home"
  override val navCattle = "Cattle"
  override val navMilk = "Milk"
  override val navCare = "Care"
  override val navReports = "Finance"

  override val roleAdmin = "Admin"
  override val roleFarmer = "Farmer"
  override val switchRole = "Active User Role"
  override val adminRoleDesc = "Full access: cattle CRUD, feed restock, health records, income & expenses, reports"
  override val farmerRoleDesc = "Daily operations: view cattle, log milk yields, log daily feeding & routine health notes"
  override val settingsTitle = "Farm Settings"
  override val languageLabel = "App Language"
  override val currencyLabel = "Currency"
  override val farmNameLabel = "Farm Name"
  override val saveSettings = "Save Settings"
  override val dataManagement = "Offline Data Management"
  override val loadSampleData = "Load Sample Data"
  override val clearAllData = "Clear All Data"
  override val sampleDataLoadedMsg = "Sample farm records loaded offline."
  override val allDataClearedMsg = "All farm records cleared."
  override val adminOnlyAction = "Admin access required for this action."
  override val switchToAdminBtn = "Switch to Admin"

  override val welcomeFarmer = "Field Operations"
  override val welcomeAdmin = "Farm Management"
  override val todayOverview = "Today's Farm Pulse"
  override val totalCattle = "Total Cattle"
  override val milkingCows = "Milking Cows"
  override val todaysMilk = "Today's Milk"
  override val monthlyProfit = "Net Balance"
  override val quickActions = "Quick Actions"
  override val actionAddCow = "Add Cow"
  override val actionLogMilk = "Log Milk"
  override val actionManageFeed = "Feed Stock"
  override val actionVaccines = "Vaccines"
  override val actionAddFinance = "Add Ledger"
  override val actionViewReports = "Reports"
  override val weeklyMilkTrend = "7-Day Milk Production"
  override val farmAlerts = "Care & Stock Alerts"
  override val noAlertsMessage = "Everything looks healthy! No low feed or overdue vaccines."
  override val vaccineDueAlert = "Due / Overdue Care"
  override val lowFeedAlert = "Low Feed Stock"
  override val treatmentAlert = "Under Treatment"
  override val recentActivity = "Recent Milk & Health Logs"
  override val litersUnit = "L"
  override val kgUnit = "kg"
  override val daysShort = "d"
  override val yearsShort = "y"
  override val monthsShort = "m"

  override val cattleRoster = "Cattle Herd"
  override val searchCattleHint = "Search by Cow ID, name, or breed..."
  override val filterAll = "All"
  override val filterHealthy = "Healthy"
  override val filterTreatment = "Treatment"
  override val filterPregnant = "Pregnant"
  override val filterMilking = "Milking"
  override val addCattle = "Add Cattle"
  override val editCattle = "Edit Cattle"
  override val deleteCattle = "Delete Cattle"
  override val confirmDeleteCattle = "Remove this cow and its profile from local farm storage?"
  override val cowIdLabel = "Cow ID (e.g. COW-105)"
  override val cowNameLabel = "Cow Name"
  override val breedLabel = "Breed"
  override val dateOfBirthLabel = "Date of Birth"
  override val ageYearsLabel = "Age (Years)"
  override val ageMonthsLabel = "Extra Months"
  override val weightKgLabel = "Weight (kg)"
  override val healthStatusLabel = "Health Status"
  override val lactationStatusLabel = "Milk Status"
  override val healthNotesLabel = "Health & Medical Notes"
  override val selectPhoto = "Pick Photo"
  override val changePhoto = "Change Photo"
  override val cattleDetails = "Cattle Profile"
  override val vitalsSection = "Physical & Breed Vitals"
  override val milkHistorySection = "Milk Production History"
  override val healthHistorySection = "Vaccination & Medical History"
  override val emptyCattleTitle = "No cattle registered yet"
  override val emptyCattleSubtitle = "Tap + Add Cattle to register your herd offline."
  override val ageLabel = "Age"
  override val weightLabel = "Weight"

  override fun healthStatusLabel(status: String): String = when (status) {
    "HEALTHY" -> "Healthy"
    "UNDER_TREATMENT" -> "Treatment"
    "PREGNANT" -> "Pregnant"
    "CHECKUP_DUE" -> "Checkup Due"
    else -> status
  }

  override fun lactationStatusLabel(status: String): String = when (status) {
    "MILKING" -> "Milking"
    "DRY" -> "Dry"
    "HEIFER" -> "Heifer"
    else -> status
  }

  override fun breedDisplay(breed: String): String = breed

  override val dailyMilkTracking = "Daily Milk Tracking"
  override val logMilk = "Log Milk"
  override val editMilkLog = "Edit Milk Entry"
  override val todayLabel = "Today"
  override val yesterdayLabel = "Yesterday"
  override val morningShift = "Morning"
  override val eveningShift = "Evening"
  override val selectCow = "Select Cow"
  override val shiftLabel = "Milking Shift"
  override val quantityLiters = "Quantity (Liters)"
  override val fatPercentOptional = "Fat % (Optional)"
  override val notesOptional = "Notes (Optional)"
  override val totalYield = "Total Yield"
  override val avgPerEntry = "Avg / Entry"
  override val emptyMilkTitle = "No milk logged for this date"
  override val emptyMilkSubtitle = "Tap + Log Milk to record morning or evening yield."

  override val tabFeed = "Feed"
  override val tabHealth = "Health & Vaccines"
  override val feedInventory = "Feed & Fodder Stock"
  override val addFeedItem = "Add Feed Item"
  override val editFeedItem = "Edit Feed Item"
  override val feedNameLabel = "Feed Name"
  override val feedCategoryLabel = "Feed Category"
  override val stockKgLabel = "Current Stock (kg)"
  override val dailyUsageKgLabel = "Daily Herd Usage (kg)"
  override val reorderLevelKgLabel = "Low Alert Level (kg)"
  override val costPerKgLabel = "Cost per kg"
  override val logFeeding = "Log Feeding"
  override val restockFeed = "Restock"
  override val quantityKgLabel = "Quantity (kg)"
  override val daysLeftLabel = "est. left"
  override val lowStockBadge = "Low Stock"
  override val inStockBadge = "In Stock"
  override val autoRecordExpense = "Also record as Expense in Finance"
  override val recentFeedLogs = "Recent Feed Activity"
  override val emptyFeedTitle = "No feed items added"
  override val emptyFeedSubtitle = "Add green fodder, silage, or concentrates to track stock."

  override fun feedCategoryDisplay(category: String): String = when (category) {
    "GREEN_FODDER" -> "Green Fodder"
    "DRY_ROUGHAGE" -> "Dry Roughage"
    "CONCENTRATE" -> "Concentrate"
    "SILAGE" -> "Silage"
    "MINERAL_SUPPLEMENT" -> "Minerals"
    else -> category
  }

  override val healthAndVaccination = "Vaccinations & Medical"
  override val addHealthRecord = "Add Health Record"
  override val filterDue = "Upcoming Due"
  override val filterVaccines = "Vaccinations"
  override val filterMedical = "Medical"
  override val recordTypeLabel = "Record Type"
  override val recordTitleLabel = "Vaccine / Treatment Name"
  override val nextDueDaysLabel = "Next Due in (Days, optional)"
  override val vetNameLabel = "Vet / Technician Name"
  override val costLabel = "Cost"
  override val markCompleted = "Mark Done"
  override val completedBadge = "Completed"
  override val dueBadge = "Due Soon"
  override val overdueBadge = "Overdue"
  override val emptyHealthTitle = "No health records found"
  override val emptyHealthSubtitle = "Track vaccinations, deworming, and vet checkups here."

  override fun healthRecordTypeDisplay(type: String): String = when (type) {
    "VACCINATION" -> "Vaccination"
    "TREATMENT" -> "Treatment"
    "DEWORMING" -> "Deworming"
    "CHECKUP" -> "Checkup"
    else -> type
  }

  override val tabFinance = "Income & Expense"
  override val tabReports = "Reports"
  override val incomeAndExpense = "Farm Financial Ledger"
  override val totalIncome = "Total Income"
  override val totalExpense = "Total Expense"
  override val netBalance = "Net Profit"
  override val addTransaction = "Add Entry"
  override val incomeLabel = "Income"
  override val expenseLabel = "Expense"
  override val categoryLabel = "Category"
  override val amountLabel = "Amount"
  override val descriptionLabel = "Description"
  override val farmerFinanceNoticeTitle = "Farmer View (Read-Only Summary)"
  override val farmerFinanceNoticeBody = "Financial ledger editing is restricted to the Admin role. Switch to Admin to add or remove transactions."
  override val emptyFinanceTitle = "No financial entries yet"
  override val emptyFinanceSubtitle = "Record milk sales, cattle sales, feed bills, and vet costs."

  override fun transactionCategoryDisplay(category: String): String = when (category) {
    "MILK_SALE" -> "Milk Sale"
    "CATTLE_SALE" -> "Cattle Sale"
    "MANURE_SALE" -> "Manure / Compost"
    "FEED_PURCHASE" -> "Feed Purchase"
    "VET_MEDICINE" -> "Vet & Medicine"
    "LABOR" -> "Staff & Labor"
    "UTILITIES" -> "Utilities & Shed"
    "EQUIPMENT" -> "Equipment"
    else -> "Other"
  }

  override val farmReportsTitle = "Farm Analytics & Reports"
  override val shareReport = "Share Report"
  override val milkLast7Days = "Milk Output (Last 7 Days)"
  override val incomeVsExpense = "Income vs Expense Summary"
  override val expenseBreakdown = "Expense by Category"
  override val herdComposition = "Herd Health Status"
  override val breedBreakdown = "Breed Distribution"
  override val healthBreakdown = "Health Overview"
  override val avgDailyMilk = "7-Day Avg / Day"
  override val totalMilk7Days = "7-Day Total Milk"
  override val reportSummaryHeader = "Smart Dairy Farm Summary Report"

  override val save = "Save"
  override val cancel = "Cancel"
  override val delete = "Delete"
  override val edit = "Edit"
  override val close = "Close"
  override val confirm = "Confirm"
  override val validationRequired = "Please fill in all required fields."
  override val validationPositiveNumber = "Please enter a valid number greater than 0."
}

object BanglaStrings : AppStrings {
  override val appName = "স্মার্ট ডেইরি ফার্ম"
  override val offlineBadge = "অফলাইন সচল"

  override val navDashboard = "হোম"
  override val navCattle = "গরু"
  override val navMilk = "দুধ"
  override val navCare = "যত্ন"
  override val navReports = "হিসাব"

  override val roleAdmin = "অ্যাডমিন"
  override val roleFarmer = "খামারি"
  override val switchRole = "ব্যবহারকারীর ভূমিকা"
  override val adminRoleDesc = "সম্পূর্ণ নিয়ন্ত্রণ: গরু যোগ/মুছা, খাদ্য স্টক, টিকা, আয়-ব্যয় এবং রিপোর্ট"
  override val farmerRoleDesc = "দৈনন্দিন কাজ: গরু দেখা, দৈনিক দুধ ও খাদ্য হিসাব লেখা, সাধারণ স্বাস্থ্য তথ্য দেখা"
  override val settingsTitle = "খামার সেটিংস"
  override val languageLabel = "অ্যাপের ভাষা"
  override val currencyLabel = "মুদ্রা"
  override val farmNameLabel = "খামারের নাম"
  override val saveSettings = "সেটিংস সংরক্ষণ করুন"
  override val dataManagement = "অফলাইন ডেটা ব্যবস্থাপনা"
  override val loadSampleData = "নমুনা ডেটা লোড করুন"
  override val clearAllData = "সব ডেটা মুছুন"
  override val sampleDataLoadedMsg = "নমুনা খামার তথ্য অফলাইনে লোড হয়েছে।"
  override val allDataClearedMsg = "সব খামার তথ্য মুছে ফেলা হয়েছে।"
  override val adminOnlyAction = "এই কাজটি করতে অ্যাডমিন অনুমতি প্রয়োজন।"
  override val switchToAdminBtn = "অ্যাডমিনে পরিবর্তন করুন"

  override val welcomeFarmer = "মাঠ পর্যায়ের কাজ"
  override val welcomeAdmin = "খামার ব্যবস্থাপনা"
  override val todayOverview = "আজকের খামারের অবস্থা"
  override val totalCattle = "মোট গবাদি পশু"
  override val milkingCows = "দুগ্ধবতী গাভী"
  override val todaysMilk = "আজকের দুধ"
  override val monthlyProfit = "নিট মুনাফা"
  override val quickActions = "দ্রুত কাজ"
  override val actionAddCow = "গরু যোগ"
  override val actionLogMilk = "দুধ হিসাব"
  override val actionManageFeed = "খাদ্য স্টক"
  override val actionVaccines = "টিকা ও চিকিৎসা"
  override val actionAddFinance = "আয়-ব্যয়"
  override val actionViewReports = "রিপোর্ট"
  override val weeklyMilkTrend = "গত ৭ দিনের দুধ উৎপাদন"
  override val farmAlerts = "সতর্কতা ও করণীয়"
  override val noAlertsMessage = "সবকিছু ভালো আছে! কোনো খাদ্য ঘাটতি বা বকেয়া টিকা নেই।"
  override val vaccineDueAlert = "বকেয়া / আসন্ন টিকা"
  override val lowFeedAlert = "খাদ্য স্টক কম"
  override val treatmentAlert = "চিকিৎসাধীন গরু"
  override val recentActivity = "সাম্প্রতিক দুধ ও স্বাস্থ্য তথ্য"
  override val litersUnit = "লিটার"
  override val kgUnit = "কেজি"
  override val daysShort = "দিন"
  override val yearsShort = "বছর"
  override val monthsShort = "মাস"

  override val cattleRoster = "গবাদি পশুর তালিকা"
  override val searchCattleHint = "আইডি, নাম বা জাত দিয়ে খুঁজুন..."
  override val filterAll = "সব"
  override val filterHealthy = "সুস্থ"
  override val filterTreatment = "চিকিৎসাধীন"
  override val filterPregnant = "গর্ভবতী"
  override val filterMilking = "দুগ্ধবতী"
  override val addCattle = "নতুন গরু যোগ"
  override val editCattle = "গরুর তথ্য সম্পাদনা"
  override val deleteCattle = "গরু মুছে ফেলুন"
  override val confirmDeleteCattle = "আপনি কি নিশ্চিতভাবে এই গরুর তথ্য লোকাল স্টোরেজ থেকে মুছতে চান?"
  override val cowIdLabel = "গরুর আইডি (যেমন COW-105)"
  override val cowNameLabel = "গরুর নাম"
  override val breedLabel = "জাত"
  override val dateOfBirthLabel = "জন্ম তারিখ"
  override val ageYearsLabel = "বয়স (বছর)"
  override val ageMonthsLabel = "অতিরিক্ত মাস"
  override val weightKgLabel = "ওজন (কেজি)"
  override val healthStatusLabel = "স্বাস্থ্যের অবস্থা"
  override val lactationStatusLabel = "দুধের অবস্থা"
  override val healthNotesLabel = "স্বাস্থ্য ও চিকিৎসা বিবরণ"
  override val selectPhoto = "ছবি বাছাই"
  override val changePhoto = "ছবি পরিবর্তন"
  override val cattleDetails = "গরুর বিস্তারিত তথ্য"
  override val vitalsSection = "শারীরিক ও জাতের তথ্য"
  override val milkHistorySection = "দুধ উৎপাদনের ইতিহাস"
  override val healthHistorySection = "টিকা ও চিকিৎসার ইতিহাস"
  override val emptyCattleTitle = "কোনো গরু যুক্ত করা হয়নি"
  override val emptyCattleSubtitle = "অফলাইনে গরু নিবন্ধন করতে + নতুন গরু যোগ বোতামে চাপ দিন।"
  override val ageLabel = "বয়স"
  override val weightLabel = "ওজন"

  override fun healthStatusLabel(status: String): String = when (status) {
    "HEALTHY" -> "সুস্থ"
    "UNDER_TREATMENT" -> "চিকিৎসাধীন"
    "PREGNANT" -> "গর্ভবতী"
    "CHECKUP_DUE" -> "পরীক্ষা বাকি"
    else -> status
  }

  override fun lactationStatusLabel(status: String): String = when (status) {
    "MILKING" -> "দুগ্ধবতী"
    "DRY" -> "শুষ্ক"
    "HEIFER" -> "বকনা"
    else -> status
  }

  override fun breedDisplay(breed: String): String = when (breed) {
    "Holstein Friesian" -> "হলস্টেইন ফ্রিজিয়ান"
    "Sahiwal" -> "শাহীওয়াল"
    "Jersey" -> "জার্সি"
    "Red Chittagong" -> "রেড চিটাগাং"
    "Crossbred" -> "সংকর জাত"
    else -> breed
  }

  override val dailyMilkTracking = "দৈনিক দুধ উৎপাদন"
  override val logMilk = "দুধের হিসাব লিখুন"
  override val editMilkLog = "দুধের তথ্য পরিবর্তন"
  override val todayLabel = "আজ"
  override val yesterdayLabel = "গতকাল"
  override val morningShift = "সকাল"
  override val eveningShift = "বিকেল"
  override val selectCow = "গরু নির্বাচন করুন"
  override val shiftLabel = "দুধ দোহনের সময়"
  override val quantityLiters = "পরিমাণ (লিটার)"
  override val fatPercentOptional = "ফ্যাট % (ঐচ্ছিক)"
  override val notesOptional = "মন্তব্য (ঐচ্ছিক)"
  override val totalYield = "মোট দুধ"
  override val avgPerEntry = "গড় / দোহন"
  override val emptyMilkTitle = "এই তারিখে কোনো দুধের হিসাব নেই"
  override val emptyMilkSubtitle = "সকাল বা বিকেলের দুধ যোগ করতে + দুধের হিসাব লিখুন চাপুন।"

  override val tabFeed = "খাদ্য"
  override val tabHealth = "টিকা ও স্বাস্থ্য"
  override val feedInventory = "গো-খাদ্য ও মজুদ"
  override val addFeedItem = "খাদ্য যোগ করুন"
  override val editFeedItem = "খাদ্য সম্পাদনা"
  override val feedNameLabel = "খাদ্যের নাম"
  override val feedCategoryLabel = "খাদ্যের ধরন"
  override val stockKgLabel = "বর্তমান মজুদ (কেজি)"
  override val dailyUsageKgLabel = "দৈনিক ব্যবহার (কেজি)"
  override val reorderLevelKgLabel = "সতর্কতা সীমা (কেজি)"
  override val costPerKgLabel = "প্রতি কেজি মূল্য"
  override val logFeeding = "খাদ্য খাওয়ানো লিখুন"
  override val restockFeed = "মজুদ বাড়ান"
  override val quantityKgLabel = "পরিমাণ (কেজি)"
  override val daysLeftLabel = "দিন চলবে"
  override val lowStockBadge = "মজুদ কম"
  override val inStockBadge = "পর্যাপ্ত মজুদ"
  override val autoRecordExpense = "আয়-ব্যয়ে খরচ হিসেবে স্বয়ংক্রিয়ভাবে লিখুন"
  override val recentFeedLogs = "সাম্প্রতিক খাদ্য ব্যবহার"
  override val emptyFeedTitle = "কোনো খাদ্য তালিকা নেই"
  override val emptyFeedSubtitle = "সবুজ ঘাস, খড় বা দানাদার খাদ্য যোগ করুন।"

  override fun feedCategoryDisplay(category: String): String = when (category) {
    "GREEN_FODDER" -> "সবুজ ঘাস"
    "DRY_ROUGHAGE" -> "শুকনো খড়"
    "CONCENTRATE" -> "দানাদার খাদ্য"
    "SILAGE" -> "সাইলেজ"
    "MINERAL_SUPPLEMENT" -> "খনিজ ও ভিটামিন"
    else -> category
  }

  override val healthAndVaccination = "টিকা ও চিকিৎসা রেকর্ড"
  override val addHealthRecord = "স্বাস্থ্য তথ্য যোগ"
  override val filterDue = "বকেয়া / আসন্ন"
  override val filterVaccines = "টিকা"
  override val filterMedical = "চিকিৎসা"
  override val recordTypeLabel = "রেকর্ডের ধরন"
  override val recordTitleLabel = "টিকা বা চিকিৎসার নাম"
  override val nextDueDaysLabel = "পরবর্তী তারিখ (কত দিন পর, ঐচ্ছিক)"
  override val vetNameLabel = "পশু চিকিৎসকের নাম"
  override val costLabel = "খরচ"
  override val markCompleted = "সম্পন্ন করুন"
  override val completedBadge = "সম্পন্ন"
  override val dueBadge = "শীঘ্রই প্রদেয়"
  override val overdueBadge = "সময় পার"
  override val emptyHealthTitle = "কোনো স্বাস্থ্য রেকর্ড পাওয়া যায়নি"
  override val emptyHealthSubtitle = "টিকা, কৃমিনাশক ও চিকিৎসার তথ্য সংরক্ষণ করুন।"

  override fun healthRecordTypeDisplay(type: String): String = when (type) {
    "VACCINATION" -> "টিকা"
    "TREATMENT" -> "চিকিৎসা"
    "DEWORMING" -> "কৃমিনাশক"
    "CHECKUP" -> "স্বাস্থ্য পরীক্ষা"
    else -> type
  }

  override val tabFinance = "আয় ও ব্যয়"
  override val tabReports = "রিপোর্ট"
  override val incomeAndExpense = "খামারের আয়-ব্যয় খাতা"
  override val totalIncome = "মোট আয়"
  override val totalExpense = "মোট ব্যয়"
  override val netBalance = "নিট লাভ"
  override val addTransaction = "হিসাব যোগ"
  override val incomeLabel = "আয়"
  override val expenseLabel = "ব্যয়"
  override val categoryLabel = "খাত"
  override val amountLabel = "টাকার পরিমাণ"
  override val descriptionLabel = "বিবরণ"
  override val farmerFinanceNoticeTitle = "খামারি মোড (শুধুমাত্র দেখার জন্য)"
  override val farmerFinanceNoticeBody = "আয়-ব্যয় যোগ বা মুছতে অ্যাডমিন ভূমিকা প্রয়োজন। হিসাব পরিবর্তন করতে অ্যাডমিন মোডে যান।"
  override val emptyFinanceTitle = "কোনো লেনদেন নেই"
  override val emptyFinanceSubtitle = "দুধ বিক্রি, গরু বিক্রি, খাদ্য কেনা ও চিকিৎসার খরচ লিখুন।"

  override fun transactionCategoryDisplay(category: String): String = when (category) {
    "MILK_SALE" -> "দুধ বিক্রি"
    "CATTLE_SALE" -> "গরু বিক্রি"
    "MANURE_SALE" -> "গোবর / জৈব সার"
    "FEED_PURCHASE" -> "খাদ্য ক্রয়"
    "VET_MEDICINE" -> "চিকিৎসা ও ঔষধ"
    "LABOR" -> "শ্রমিক বেতন"
    "UTILITIES" -> "বিদ্যুৎ ও শেড"
    "EQUIPMENT" -> "যন্ত্রপাতি"
    else -> "অন্যান্য"
  }

  override val farmReportsTitle = "খামার পরিসংখ্যান ও রিপোর্ট"
  override val shareReport = "রিপোর্ট শেয়ার"
  override val milkLast7Days = "দুধ উৎপাদন (গত ৭ দিন)"
  override val incomeVsExpense = "আয় বনাম ব্যয় তুলনা"
  override val expenseBreakdown = "খাতভিত্তিক ব্যয়"
  override val herdComposition = "গবাদি পশুর স্বাস্থ্য অবস্থা"
  override val breedBreakdown = "জাত অনুযায়ী গরু"
  override val healthBreakdown = "স্বাস্থ্য সারসংক্ষেপ"
  override val avgDailyMilk = "৭ দিনের দৈনিক গড়"
  override val totalMilk7Days = "৭ দিনের মোট দুধ"
  override val reportSummaryHeader = "স্মার্ট ডেইরি ফার্ম সারসংক্ষেপ রিপোর্ট"

  override val save = "সংরক্ষণ"
  override val cancel = "বাতিল"
  override val delete = "মুছুন"
  override val edit = "সম্পাদনা"
  override val close = "বন্ধ"
  override val confirm = "নিশ্চিত করুন"
  override val validationRequired = "অনুগ্রহ করে প্রয়োজনীয় তথ্য পূরণ করুন।"
  override val validationPositiveNumber = "অনুগ্রহ করে ০-এর বেশি সঠিক সংখ্যা লিখুন।"
}
