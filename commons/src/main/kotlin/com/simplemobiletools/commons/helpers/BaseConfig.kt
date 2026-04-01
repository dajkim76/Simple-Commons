package com.simplemobiletools.commons.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Environment
import android.text.format.DateFormat
import androidx.core.content.ContextCompat
import com.simplemobiletools.commons.R
import com.simplemobiletools.commons.extensions.getInternalStoragePath
import com.simplemobiletools.commons.extensions.getSDCardPath
import com.simplemobiletools.commons.extensions.getSharedPrefs
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.LinkedList
import java.util.Locale

// Two BaseConfig instances are created due to the Config in the app module. To receive notifications of value changes in both instances, implement it as a singleton.
object ConfigChangeBus {
    val flow = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 64, onBufferOverflow = BufferOverflow.DROP_OLDEST)
}

// BaseConfig is singleton by getInstance(), But App module's Config has BaseConfig
open class BaseConfig(val context: Context) {
    protected val mmkv: MMKV = MMKV.defaultMMKV()
    private val changeNotifier get() = ConfigChangeBus.flow

    // Migrate SharedPreferences to MMKV
    protected fun migrate(context: Context) {
        val prefs = context.getSharedPrefs()
        if (prefs.all.isNotEmpty()) {
            mmkv.importFromSharedPreferences(prefs)
            prefs.edit().clear().apply()
        }

        // app module's config
        val scanConfig = context.getSharedPreferences("scan_config", Context.MODE_PRIVATE)
        if (scanConfig.all.isNotEmpty()) {
            mmkv.importFromSharedPreferences(scanConfig)
            scanConfig.edit().clear().apply()
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: BaseConfig? = null

        @Synchronized
        fun getInstance(context: Context): BaseConfig {
            if (MMKV.getRootDir() == null) MMKV.initialize(context)
            return instance ?: BaseConfig(context.applicationContext).also {
                instance = it
                it.migrate(context)
            }
        }
    }

    var appRunCount: Int
        get() = mmkv.decodeInt(APP_RUN_COUNT, 0)
        set(appRunCount) {
            mmkv.encode(APP_RUN_COUNT, appRunCount)
        }

    var lastVersion: Int
        get() = mmkv.decodeInt(LAST_VERSION, 0)
        set(lastVersion) {
            mmkv.encode(LAST_VERSION, lastVersion)
        }

    var primaryAndroidDataTreeUri: String
        get() = mmkv.decodeString(PRIMARY_ANDROID_DATA_TREE_URI, "")!!
        set(uri) {
            mmkv.encode(PRIMARY_ANDROID_DATA_TREE_URI, uri)
        }

    var sdAndroidDataTreeUri: String
        get() = mmkv.decodeString(SD_ANDROID_DATA_TREE_URI, "")!!
        set(uri) {
            mmkv.encode(SD_ANDROID_DATA_TREE_URI, uri)
        }

    var otgAndroidDataTreeUri: String
        get() = mmkv.decodeString(OTG_ANDROID_DATA_TREE_URI, "")!!
        set(uri) {
            mmkv.encode(OTG_ANDROID_DATA_TREE_URI, uri)
        }

    var primaryAndroidObbTreeUri: String
        get() = mmkv.decodeString(PRIMARY_ANDROID_OBB_TREE_URI, "")!!
        set(uri) {
            mmkv.encode(PRIMARY_ANDROID_OBB_TREE_URI, uri)
        }

    var sdAndroidObbTreeUri: String
        get() = mmkv.decodeString(SD_ANDROID_OBB_TREE_URI, "")!!
        set(uri) {
            mmkv.encode(SD_ANDROID_OBB_TREE_URI, uri)
        }

    var otgAndroidObbTreeUri: String
        get() = mmkv.decodeString(OTG_ANDROID_OBB_TREE_URI, "")!!
        set(uri) {
            mmkv.encode(OTG_ANDROID_OBB_TREE_URI, uri)
        }

    var sdTreeUri: String
        get() = mmkv.decodeString(SD_TREE_URI, "")!!
        set(uri) {
            mmkv.encode(SD_TREE_URI, uri)
        }

    var OTGTreeUri: String
        get() = mmkv.decodeString(OTG_TREE_URI, "")!!
        set(OTGTreeUri) {
            mmkv.encode(OTG_TREE_URI, OTGTreeUri)
        }

    var OTGPartition: String
        get() = mmkv.decodeString(OTG_PARTITION, "")!!
        set(OTGPartition) {
            mmkv.encode(OTG_PARTITION, OTGPartition)
        }

    var OTGPath: String
        get() = mmkv.decodeString(OTG_REAL_PATH, "")!!
        set(OTGPath) {
            mmkv.encode(OTG_REAL_PATH, OTGPath)
        }

    var sdCardPath: String
        get() = mmkv.decodeString(SD_CARD_PATH, getDefaultSDCardPath())!!
        set(sdCardPath) {
            mmkv.encode(SD_CARD_PATH, sdCardPath)
        }

    private fun getDefaultSDCardPath() = if (mmkv.contains(SD_CARD_PATH)) "" else context.getSDCardPath()

    var internalStoragePath: String
        get() = mmkv.decodeString(INTERNAL_STORAGE_PATH, getDefaultInternalPath())!!
        set(internalStoragePath) {
            mmkv.encode(INTERNAL_STORAGE_PATH, internalStoragePath)
        }

    private fun getDefaultInternalPath() = if (mmkv.contains(INTERNAL_STORAGE_PATH)) "" else context.getInternalStoragePath()

    var textColor: Int
        get() = mmkv.decodeInt(TEXT_COLOR, ContextCompat.getColor(context, R.color.default_text_color))
        set(textColor) {
            mmkv.encode(TEXT_COLOR, textColor)
        }

    var backgroundColor: Int
        get() = mmkv.decodeInt(BACKGROUND_COLOR, ContextCompat.getColor(context, R.color.default_background_color))
        set(backgroundColor) {
            mmkv.encode(BACKGROUND_COLOR, backgroundColor)
        }

    var primaryColor: Int
        get() = mmkv.decodeInt(PRIMARY_COLOR, ContextCompat.getColor(context, R.color.default_primary_color))
        set(primaryColor) {
            mmkv.encode(PRIMARY_COLOR, primaryColor)
        }

    var accentColor: Int
        get() = mmkv.decodeInt(ACCENT_COLOR, ContextCompat.getColor(context, R.color.default_accent_color))
        set(accentColor) {
            mmkv.encode(ACCENT_COLOR, accentColor)
        }

    var lastHandledShortcutColor: Int
        get() = mmkv.decodeInt(LAST_HANDLED_SHORTCUT_COLOR, 1)
        set(lastHandledShortcutColor) {
            mmkv.encode(LAST_HANDLED_SHORTCUT_COLOR, lastHandledShortcutColor)
        }

    var appIconColor: Int
        get() = mmkv.decodeInt(APP_ICON_COLOR, ContextCompat.getColor(context, R.color.default_app_icon_color))
        set(appIconColor) {
            isUsingModifiedAppIcon = appIconColor != ContextCompat.getColor(context, R.color.color_primary)
            mmkv.encode(APP_ICON_COLOR, appIconColor)
        }

    var lastIconColor: Int
        get() = mmkv.decodeInt(LAST_ICON_COLOR, ContextCompat.getColor(context, R.color.color_primary))
        set(lastIconColor) {
            mmkv.encode(LAST_ICON_COLOR, lastIconColor)
        }

    var customTextColor: Int
        get() = mmkv.decodeInt(CUSTOM_TEXT_COLOR, textColor)
        set(customTextColor) {
            mmkv.encode(CUSTOM_TEXT_COLOR, customTextColor)
        }

    var customBackgroundColor: Int
        get() = mmkv.decodeInt(CUSTOM_BACKGROUND_COLOR, backgroundColor)
        set(customBackgroundColor) {
            mmkv.encode(CUSTOM_BACKGROUND_COLOR, customBackgroundColor)
        }

    var customPrimaryColor: Int
        get() = mmkv.decodeInt(CUSTOM_PRIMARY_COLOR, primaryColor)
        set(customPrimaryColor) {
            mmkv.encode(CUSTOM_PRIMARY_COLOR, customPrimaryColor)
        }

    var customAccentColor: Int
        get() = mmkv.decodeInt(CUSTOM_ACCENT_COLOR, accentColor)
        set(customAccentColor) {
            mmkv.encode(CUSTOM_ACCENT_COLOR, customAccentColor)
        }

    var customAppIconColor: Int
        get() = mmkv.decodeInt(CUSTOM_APP_ICON_COLOR, appIconColor)
        set(customAppIconColor) {
            mmkv.encode(CUSTOM_APP_ICON_COLOR, customAppIconColor)
        }

    var widgetBgColor: Int
        get() = mmkv.decodeInt(WIDGET_BG_COLOR, ContextCompat.getColor(context, R.color.default_widget_bg_color))
        set(widgetBgColor) {
            mmkv.encode(WIDGET_BG_COLOR, widgetBgColor)
        }

    var widgetTextColor: Int
        get() = mmkv.decodeInt(WIDGET_TEXT_COLOR, ContextCompat.getColor(context, R.color.default_widget_text_color))
        set(widgetTextColor) {
            mmkv.encode(WIDGET_TEXT_COLOR, widgetTextColor)
        }

    // hidden folder visibility protection
    var isHiddenPasswordProtectionOn: Boolean
        get() = mmkv.decodeBool(PASSWORD_PROTECTION, false)
        set(isHiddenPasswordProtectionOn) {
            mmkv.encode(PASSWORD_PROTECTION, isHiddenPasswordProtectionOn)
        }

    var hiddenPasswordHash: String
        get() = mmkv.decodeString(PASSWORD_HASH, "")!!
        set(hiddenPasswordHash) {
            mmkv.encode(PASSWORD_HASH, hiddenPasswordHash)
        }

    var hiddenProtectionType: Int
        get() = mmkv.decodeInt(PROTECTION_TYPE, PROTECTION_PATTERN)
        set(hiddenProtectionType) {
            mmkv.encode(PROTECTION_TYPE, hiddenProtectionType)
        }

    // whole app launch protection
    var isAppPasswordProtectionOn: Boolean
        get() = mmkv.decodeBool(APP_PASSWORD_PROTECTION, false)
        set(isAppPasswordProtectionOn) {
            mmkv.encode(APP_PASSWORD_PROTECTION, isAppPasswordProtectionOn)
        }

    var appPasswordHash: String
        get() = mmkv.decodeString(APP_PASSWORD_HASH, "")!!
        set(appPasswordHash) {
            mmkv.encode(APP_PASSWORD_HASH, appPasswordHash)
        }

    var appProtectionType: Int
        get() = mmkv.decodeInt(APP_PROTECTION_TYPE, PROTECTION_PATTERN)
        set(appProtectionType) {
            mmkv.encode(APP_PROTECTION_TYPE, appProtectionType)
        }

    // file delete and move protection
    var isDeletePasswordProtectionOn: Boolean
        get() = mmkv.decodeBool(DELETE_PASSWORD_PROTECTION, false)
        set(isDeletePasswordProtectionOn) {
            mmkv.encode(DELETE_PASSWORD_PROTECTION, isDeletePasswordProtectionOn)
        }

    var deletePasswordHash: String
        get() = mmkv.decodeString(DELETE_PASSWORD_HASH, "")!!
        set(deletePasswordHash) {
            mmkv.encode(DELETE_PASSWORD_HASH, deletePasswordHash)
        }

    var deleteProtectionType: Int
        get() = mmkv.decodeInt(DELETE_PROTECTION_TYPE, PROTECTION_PATTERN)
        set(deleteProtectionType) {
            mmkv.encode(DELETE_PROTECTION_TYPE, deleteProtectionType)
        }

    // folder locking
    fun addFolderProtection(path: String, hash: String, type: Int) {
        mmkv.encode("$PROTECTED_FOLDER_HASH$path", hash)
        mmkv.encode("$PROTECTED_FOLDER_TYPE$path", type)
    }

    fun removeFolderProtection(path: String) {
        mmkv.remove("$PROTECTED_FOLDER_HASH$path")
        mmkv.remove("$PROTECTED_FOLDER_TYPE$path")
    }

    fun isFolderProtected(path: String) = getFolderProtectionType(path) != PROTECTION_NONE

    fun getFolderProtectionHash(path: String) = mmkv.decodeString("$PROTECTED_FOLDER_HASH$path", "") ?: ""

    fun getFolderProtectionType(path: String) = mmkv.decodeInt("$PROTECTED_FOLDER_TYPE$path", PROTECTION_NONE)

    var lastCopyPath: String
        get() = mmkv.decodeString(LAST_COPY_PATH, "")!!
        set(lastCopyPath) {
            mmkv.encode(LAST_COPY_PATH, lastCopyPath)
        }

    var keepLastModified: Boolean
        get() = mmkv.decodeBool(KEEP_LAST_MODIFIED, true)
        set(keepLastModified) {
            mmkv.encode(KEEP_LAST_MODIFIED, keepLastModified)
        }

    var useEnglish: Boolean
        get() = mmkv.decodeBool(USE_ENGLISH, false)
        set(useEnglish) {
            wasUseEnglishToggled = true
            mmkv.encode(USE_ENGLISH, useEnglish)
            changeNotifier.tryEmit(USE_ENGLISH)
        }

    val useEnglishFlow: Flow<Boolean>
        get() = changeNotifier
            .filter { it == USE_ENGLISH }
            .conflate()
            .map { useEnglish }
            .onStart { emit(useEnglish) }
            .distinctUntilChanged()


    var wasUseEnglishToggled: Boolean
        get() = mmkv.decodeBool(WAS_USE_ENGLISH_TOGGLED, false)
        set(wasUseEnglishToggled) {
            mmkv.encode(WAS_USE_ENGLISH_TOGGLED, wasUseEnglishToggled)
            changeNotifier.tryEmit(WAS_USE_ENGLISH_TOGGLED)
        }

    val wasUseEnglishToggledFlow: Flow<Boolean>
        get() = changeNotifier
            .filter { it == WAS_USE_ENGLISH_TOGGLED }
            .conflate()
            .map { wasUseEnglishToggled }
            .onStart { emit(wasUseEnglishToggled) }
            .distinctUntilChanged()

    var wasSharedThemeEverActivated: Boolean
        get() = mmkv.decodeBool(WAS_SHARED_THEME_EVER_ACTIVATED, false)
        set(wasSharedThemeEverActivated) {
            mmkv.encode(WAS_SHARED_THEME_EVER_ACTIVATED, wasSharedThemeEverActivated)
        }

    var isUsingSharedTheme: Boolean
        get() = mmkv.decodeBool(IS_USING_SHARED_THEME, false)
        set(isUsingSharedTheme) {
            mmkv.encode(IS_USING_SHARED_THEME, isUsingSharedTheme)
        }

    // used by Simple Thank You, stop using shared Shared Theme if it has been changed in it
    var shouldUseSharedTheme: Boolean
        get() = mmkv.decodeBool(SHOULD_USE_SHARED_THEME, false)
        set(shouldUseSharedTheme) {
            mmkv.encode(SHOULD_USE_SHARED_THEME, shouldUseSharedTheme)
        }

    var isUsingAutoTheme: Boolean
        get() = mmkv.decodeBool(IS_USING_AUTO_THEME, false)
        set(isUsingAutoTheme) {
            mmkv.encode(IS_USING_AUTO_THEME, isUsingAutoTheme)
        }

    var isUsingSystemTheme: Boolean
        get() = mmkv.decodeBool(IS_USING_SYSTEM_THEME, isSPlus())
        set(isUsingSystemTheme) {
            mmkv.encode(IS_USING_SYSTEM_THEME, isUsingSystemTheme)
        }

    var wasCustomThemeSwitchDescriptionShown: Boolean
        get() = mmkv.decodeBool(WAS_CUSTOM_THEME_SWITCH_DESCRIPTION_SHOWN, false)
        set(wasCustomThemeSwitchDescriptionShown) {
            mmkv.encode(WAS_CUSTOM_THEME_SWITCH_DESCRIPTION_SHOWN, wasCustomThemeSwitchDescriptionShown)
        }

    var wasSharedThemeForced: Boolean
        get() = mmkv.decodeBool(WAS_SHARED_THEME_FORCED, false)
        set(wasSharedThemeForced) {
            mmkv.encode(WAS_SHARED_THEME_FORCED, wasSharedThemeForced)
        }

    var showInfoBubble: Boolean
        get() = mmkv.decodeBool(SHOW_INFO_BUBBLE, true)
        set(showInfoBubble) {
            mmkv.encode(SHOW_INFO_BUBBLE, showInfoBubble)
        }

    var lastConflictApplyToAll: Boolean
        get() = mmkv.decodeBool(LAST_CONFLICT_APPLY_TO_ALL, true)
        set(lastConflictApplyToAll) {
            mmkv.encode(LAST_CONFLICT_APPLY_TO_ALL, lastConflictApplyToAll)
        }

    var lastConflictResolution: Int
        get() = mmkv.decodeInt(LAST_CONFLICT_RESOLUTION, CONFLICT_SKIP)
        set(lastConflictResolution) {
            mmkv.encode(LAST_CONFLICT_RESOLUTION, lastConflictResolution)
        }

    var sorting: Int
        get() = mmkv.decodeInt(SORT_ORDER, context.resources.getInteger(R.integer.default_sorting))
        set(sorting) {
            mmkv.encode(SORT_ORDER, sorting)
        }

    fun saveCustomSorting(path: String, value: Int) {
        if (path.isEmpty()) {
            sorting = value
        } else {
            mmkv.encode(SORT_FOLDER_PREFIX + path.lowercase(), value)
        }
    }

    fun getFolderSorting(path: String) = mmkv.decodeInt(SORT_FOLDER_PREFIX + path.lowercase(), sorting)

    fun removeCustomSorting(path: String) {
        mmkv.remove(SORT_FOLDER_PREFIX + path.lowercase())
    }

    fun hasCustomSorting(path: String) = mmkv.contains(SORT_FOLDER_PREFIX + path.lowercase())

    var hadThankYouInstalled: Boolean
        get() = mmkv.decodeBool(HAD_THANK_YOU_INSTALLED, false)
        set(hadThankYouInstalled) {
            mmkv.encode(HAD_THANK_YOU_INSTALLED, hadThankYouInstalled)
        }

    var skipDeleteConfirmation: Boolean
        get() = mmkv.decodeBool(SKIP_DELETE_CONFIRMATION, false)
        set(skipDeleteConfirmation) {
            mmkv.encode(SKIP_DELETE_CONFIRMATION, skipDeleteConfirmation)
        }

    var enablePullToRefresh: Boolean
        get() = mmkv.decodeBool(ENABLE_PULL_TO_REFRESH, true)
        set(enablePullToRefresh) {
            mmkv.encode(ENABLE_PULL_TO_REFRESH, enablePullToRefresh)
        }

    var scrollHorizontally: Boolean
        get() = mmkv.decodeBool(SCROLL_HORIZONTALLY, false)
        set(scrollHorizontally) {
            mmkv.encode(SCROLL_HORIZONTALLY, scrollHorizontally)
        }

    var preventPhoneFromSleeping: Boolean
        get() = mmkv.decodeBool(PREVENT_PHONE_FROM_SLEEPING, true)
        set(preventPhoneFromSleeping) {
            mmkv.encode(PREVENT_PHONE_FROM_SLEEPING, preventPhoneFromSleeping)
        }

    var lastUsedViewPagerPage: Int
        get() = mmkv.decodeInt(LAST_USED_VIEW_PAGER_PAGE, context.resources.getInteger(R.integer.default_viewpager_page))
        set(lastUsedViewPagerPage) {
            mmkv.encode(LAST_USED_VIEW_PAGER_PAGE, lastUsedViewPagerPage)
        }

    var use24HourFormat: Boolean
        get() = mmkv.decodeBool(USE_24_HOUR_FORMAT, DateFormat.is24HourFormat(context))
        set(use24HourFormat) {
            mmkv.encode(USE_24_HOUR_FORMAT, use24HourFormat)
        }

    var isSundayFirst: Boolean
        get() {
            val isSundayFirst = Calendar.getInstance(Locale.getDefault()).firstDayOfWeek == Calendar.SUNDAY
            return mmkv.decodeBool(SUNDAY_FIRST, isSundayFirst)
        }
        set(sundayFirst) {
            mmkv.encode(SUNDAY_FIRST, sundayFirst)
        }

    var wasAlarmWarningShown: Boolean
        get() = mmkv.decodeBool(WAS_ALARM_WARNING_SHOWN, false)
        set(wasAlarmWarningShown) {
            mmkv.encode(WAS_ALARM_WARNING_SHOWN, wasAlarmWarningShown)
        }

    var wasReminderWarningShown: Boolean
        get() = mmkv.decodeBool(WAS_REMINDER_WARNING_SHOWN, false)
        set(wasReminderWarningShown) {
            mmkv.encode(WAS_REMINDER_WARNING_SHOWN, wasReminderWarningShown)
        }

    var useSameSnooze: Boolean
        get() = mmkv.decodeBool(USE_SAME_SNOOZE, true)
        set(useSameSnooze) {
            mmkv.encode(USE_SAME_SNOOZE, useSameSnooze)
        }

    var snoozeTime: Int
        get() = mmkv.decodeInt(SNOOZE_TIME, 10)
        set(snoozeDelay) {
            mmkv.encode(SNOOZE_TIME, snoozeDelay)
        }

    var vibrateOnButtonPress: Boolean
        get() = mmkv.decodeBool(VIBRATE_ON_BUTTON_PRESS, context.resources.getBoolean(R.bool.default_vibrate_on_press))
        set(vibrateOnButton) {
            mmkv.encode(VIBRATE_ON_BUTTON_PRESS, vibrateOnButton)
        }

    var yourAlarmSounds: String
        get() = mmkv.decodeString(YOUR_ALARM_SOUNDS, "")!!
        set(yourAlarmSounds) {
            mmkv.encode(YOUR_ALARM_SOUNDS, yourAlarmSounds)
        }

    var isUsingModifiedAppIcon: Boolean
        get() = mmkv.decodeBool(IS_USING_MODIFIED_APP_ICON, false)
        set(isUsingModifiedAppIcon) {
            mmkv.encode(IS_USING_MODIFIED_APP_ICON, isUsingModifiedAppIcon)
        }

    var appId: String
        get() = mmkv.decodeString(APP_ID, "")!!
        set(appId) {
            mmkv.encode(APP_ID, appId)
        }

    var initialWidgetHeight: Int
        get() = mmkv.decodeInt(INITIAL_WIDGET_HEIGHT, 0)
        set(initialWidgetHeight) {
            mmkv.encode(INITIAL_WIDGET_HEIGHT, initialWidgetHeight)
        }

    var widgetIdToMeasure: Int
        get() = mmkv.decodeInt(WIDGET_ID_TO_MEASURE, 0)
        set(widgetIdToMeasure) {
            mmkv.encode(WIDGET_ID_TO_MEASURE, widgetIdToMeasure)
        }

    var wasOrangeIconChecked: Boolean
        get() = mmkv.decodeBool(WAS_ORANGE_ICON_CHECKED, false)
        set(wasOrangeIconChecked) {
            mmkv.encode(WAS_ORANGE_ICON_CHECKED, wasOrangeIconChecked)
        }

    var wasAppOnSDShown: Boolean
        get() = mmkv.decodeBool(WAS_APP_ON_SD_SHOWN, false)
        set(wasAppOnSDShown) {
            mmkv.encode(WAS_APP_ON_SD_SHOWN, wasAppOnSDShown)
        }

    var wasBeforeAskingShown: Boolean
        get() = mmkv.decodeBool(WAS_BEFORE_ASKING_SHOWN, false)
        set(wasBeforeAskingShown) {
            mmkv.encode(WAS_BEFORE_ASKING_SHOWN, wasBeforeAskingShown)
        }

    var wasBeforeRateShown: Boolean
        get() = mmkv.decodeBool(WAS_BEFORE_RATE_SHOWN, false)
        set(wasBeforeRateShown) {
            mmkv.encode(WAS_BEFORE_RATE_SHOWN, wasBeforeRateShown)
        }

    var wasInitialUpgradeToProShown: Boolean
        get() = mmkv.decodeBool(WAS_INITIAL_UPGRADE_TO_PRO_SHOWN, false)
        set(wasInitialUpgradeToProShown) {
            mmkv.encode(WAS_INITIAL_UPGRADE_TO_PRO_SHOWN, wasInitialUpgradeToProShown)
        }

    var wasAppIconCustomizationWarningShown: Boolean
        get() = mmkv.decodeBool(WAS_APP_ICON_CUSTOMIZATION_WARNING_SHOWN, false)
        set(wasAppIconCustomizationWarningShown) {
            mmkv.encode(WAS_APP_ICON_CUSTOMIZATION_WARNING_SHOWN, wasAppIconCustomizationWarningShown)
        }

    var appSideloadingStatus: Int
        get() = mmkv.decodeInt(APP_SIDELOADING_STATUS, SIDELOADING_UNCHECKED)
        set(appSideloadingStatus) {
            mmkv.encode(APP_SIDELOADING_STATUS, appSideloadingStatus)
        }

    var dateFormat: String
        get() = mmkv.decodeString(DATE_FORMAT, getDefaultDateFormat())!!
        set(dateFormat) {
            mmkv.encode(DATE_FORMAT, dateFormat)
        }

    private fun getDefaultDateFormat(): String {
        val format = DateFormat.getDateFormat(context)
        val pattern = (format as SimpleDateFormat).toLocalizedPattern()
        return when (pattern.lowercase().replace(" ", "")) {
            "d.M.y" -> DATE_FORMAT_ONE
            "dd/mm/y" -> DATE_FORMAT_TWO
            "mm/dd/y" -> DATE_FORMAT_THREE
            "y-mm-dd" -> DATE_FORMAT_FOUR
            "dmmmmy" -> DATE_FORMAT_FIVE
            "mmmmdy" -> DATE_FORMAT_SIX
            "mm-dd-y" -> DATE_FORMAT_SEVEN
            "dd-mm-y" -> DATE_FORMAT_EIGHT
            else -> DATE_FORMAT_ONE
        }
    }

    var wasOTGHandled: Boolean
        get() = mmkv.decodeBool(WAS_OTG_HANDLED, false)
        set(wasOTGHandled) {
            mmkv.encode(WAS_OTG_HANDLED, wasOTGHandled)
        }

    var wasUpgradedFromFreeShown: Boolean
        get() = mmkv.decodeBool(WAS_UPGRADED_FROM_FREE_SHOWN, false)
        set(wasUpgradedFromFreeShown) {
            mmkv.encode(WAS_UPGRADED_FROM_FREE_SHOWN, wasUpgradedFromFreeShown)
        }

    var wasRateUsPromptShown: Boolean
        get() = mmkv.decodeBool(WAS_RATE_US_PROMPT_SHOWN, false)
        set(wasRateUsPromptShown) {
            mmkv.encode(WAS_RATE_US_PROMPT_SHOWN, wasRateUsPromptShown)
        }

    var wasAppRated: Boolean
        get() = mmkv.decodeBool(WAS_APP_RATED, false)
        set(wasAppRated) {
            mmkv.encode(WAS_APP_RATED, wasAppRated)
        }

    var wasSortingByNumericValueAdded: Boolean
        get() = mmkv.decodeBool(WAS_SORTING_BY_NUMERIC_VALUE_ADDED, false)
        set(wasSortingByNumericValueAdded) {
            mmkv.encode(WAS_SORTING_BY_NUMERIC_VALUE_ADDED, wasSortingByNumericValueAdded)
        }

    var wasFolderLockingNoticeShown: Boolean
        get() = mmkv.decodeBool(WAS_FOLDER_LOCKING_NOTICE_SHOWN, false)
        set(wasFolderLockingNoticeShown) {
            mmkv.encode(WAS_FOLDER_LOCKING_NOTICE_SHOWN, wasFolderLockingNoticeShown)
        }

    var lastRenameUsed: Int
        get() = mmkv.decodeInt(LAST_RENAME_USED, RENAME_SIMPLE)
        set(lastRenameUsed) {
            mmkv.encode(LAST_RENAME_USED, lastRenameUsed)
        }

    var lastRenamePatternUsed: String
        get() = mmkv.decodeString(LAST_RENAME_PATTERN_USED, "")!!
        set(lastRenamePatternUsed) {
            mmkv.encode(LAST_RENAME_PATTERN_USED, lastRenamePatternUsed)
        }

    var lastExportedSettingsFolder: String
        get() = mmkv.decodeString(LAST_EXPORTED_SETTINGS_FOLDER, "")!!
        set(lastExportedSettingsFolder) {
            mmkv.encode(LAST_EXPORTED_SETTINGS_FOLDER, lastExportedSettingsFolder)
        }

    var lastBlockedNumbersExportPath: String
        get() = mmkv.decodeString(LAST_BLOCKED_NUMBERS_EXPORT_PATH, "")!!
        set(lastBlockedNumbersExportPath) {
            mmkv.encode(LAST_BLOCKED_NUMBERS_EXPORT_PATH, lastBlockedNumbersExportPath)
        }

    var blockUnknownNumbers: Boolean
        get() = mmkv.decodeBool(BLOCK_UNKNOWN_NUMBERS, false)
        set(blockUnknownNumbers) {
            mmkv.encode(BLOCK_UNKNOWN_NUMBERS, blockUnknownNumbers)
            changeNotifier.tryEmit(BLOCK_UNKNOWN_NUMBERS)
        }

    val isBlockingUnknownNumbers: Flow<Boolean>
        get() = changeNotifier
            .filter { it == BLOCK_UNKNOWN_NUMBERS }
            .conflate()
            .map { blockUnknownNumbers }
            .onStart { emit(blockUnknownNumbers) }
            .distinctUntilChanged()

    var blockHiddenNumbers: Boolean
        get() = mmkv.decodeBool(BLOCK_HIDDEN_NUMBERS, false)
        set(blockHiddenNumbers) {
            mmkv.encode(BLOCK_HIDDEN_NUMBERS, blockHiddenNumbers)
            changeNotifier.tryEmit(BLOCK_HIDDEN_NUMBERS)
        }

    val isBlockingHiddenNumbers: Flow<Boolean>
        get() = changeNotifier
            .filter { it == BLOCK_HIDDEN_NUMBERS }
            .conflate()
            .map { blockHiddenNumbers }
            .onStart { emit(blockHiddenNumbers) }
            .distinctUntilChanged()

    var fontSize: Int
        get() = mmkv.decodeInt(FONT_SIZE, context.resources.getInteger(R.integer.default_font_size))
        set(size) {
            mmkv.encode(FONT_SIZE, size)
        }

    // notify the users about new SMS Messenger and Voice Recorder released
    var wasMessengerRecorderShown: Boolean
        get() = mmkv.decodeBool(WAS_MESSENGER_RECORDER_SHOWN, false)
        set(wasMessengerRecorderShown) {
            mmkv.encode(WAS_MESSENGER_RECORDER_SHOWN, wasMessengerRecorderShown)
        }

    var defaultTab: Int
        get() = mmkv.decodeInt(DEFAULT_TAB, TAB_LAST_USED)
        set(defaultTab) {
            mmkv.encode(DEFAULT_TAB, defaultTab)
        }

    var startNameWithSurname: Boolean
        get() = mmkv.decodeBool(START_NAME_WITH_SURNAME, false)
        set(startNameWithSurname) {
            mmkv.encode(START_NAME_WITH_SURNAME, startNameWithSurname)
        }

    var favorites: MutableSet<String>
        get() = mmkv.decodeStringSet(FAVORITES, HashSet())!!
        set(favorites) {
            mmkv.putStringSet(FAVORITES, favorites)
        }

    var showCallConfirmation: Boolean
        get() = mmkv.decodeBool(SHOW_CALL_CONFIRMATION, false)
        set(showCallConfirmation) {
            mmkv.encode(SHOW_CALL_CONFIRMATION, showCallConfirmation)
        }

    // color picker last used colors
    var colorPickerRecentColors: LinkedList<Int>
        get(): LinkedList<Int> {
            val defaultList = arrayListOf(
                ContextCompat.getColor(context, R.color.md_red_700),
                ContextCompat.getColor(context, R.color.md_blue_700),
                ContextCompat.getColor(context, R.color.md_green_700),
                ContextCompat.getColor(context, R.color.md_yellow_700),
                ContextCompat.getColor(context, R.color.md_orange_700)
            )
            return LinkedList(mmkv.decodeString(COLOR_PICKER_RECENT_COLORS, null)?.lines()?.map { it.toInt() } ?: defaultList)
        }
        set(recentColors) {
            mmkv.encode(COLOR_PICKER_RECENT_COLORS, recentColors.joinToString(separator = "\n"))
            changeNotifier.tryEmit(COLOR_PICKER_RECENT_COLORS)
        }

    val colorPickerRecentColorsFlow: Flow<LinkedList<Int>>
        get() = changeNotifier
            .filter { it == COLOR_PICKER_RECENT_COLORS }
            .conflate()
            .map { colorPickerRecentColors }
            .onStart { emit(colorPickerRecentColors) }
            .distinctUntilChanged()

    var ignoredContactSources: HashSet<String>
        get() = mmkv.decodeStringSet(IGNORED_CONTACT_SOURCES, hashSetOf(".")) as HashSet
        set(ignoreContactSources) {
            mmkv.putStringSet(IGNORED_CONTACT_SOURCES, ignoreContactSources)
        }

    var showContactThumbnails: Boolean
        get() = mmkv.decodeBool(SHOW_CONTACT_THUMBNAILS, true)
        set(showContactThumbnails) {
            mmkv.encode(SHOW_CONTACT_THUMBNAILS, showContactThumbnails)
        }

    var showPhoneNumbers: Boolean
        get() = mmkv.decodeBool(SHOW_PHONE_NUMBERS, false)
        set(showPhoneNumbers) {
            mmkv.encode(SHOW_PHONE_NUMBERS, showPhoneNumbers)
        }

    var showOnlyContactsWithNumbers: Boolean
        get() = mmkv.decodeBool(SHOW_ONLY_CONTACTS_WITH_NUMBERS, false)
        set(showOnlyContactsWithNumbers) {
            mmkv.encode(SHOW_ONLY_CONTACTS_WITH_NUMBERS, showOnlyContactsWithNumbers)
        }

    var lastUsedContactSource: String
        get() = mmkv.decodeString(LAST_USED_CONTACT_SOURCE, "")!!
        set(lastUsedContactSource) {
            mmkv.encode(LAST_USED_CONTACT_SOURCE, lastUsedContactSource)
        }

    var onContactClick: Int
        get() = mmkv.decodeInt(ON_CONTACT_CLICK, ON_CLICK_VIEW_CONTACT)
        set(onContactClick) {
            mmkv.encode(ON_CONTACT_CLICK, onContactClick)
        }

    var showContactFields: Int
        get() = mmkv.decodeInt(
            SHOW_CONTACT_FIELDS,
            SHOW_FIRST_NAME_FIELD or SHOW_SURNAME_FIELD or SHOW_PHONE_NUMBERS_FIELD or SHOW_EMAILS_FIELD or
                SHOW_ADDRESSES_FIELD or SHOW_EVENTS_FIELD or SHOW_NOTES_FIELD or SHOW_GROUPS_FIELD or SHOW_CONTACT_SOURCE_FIELD
        )
        set(showContactFields) {
            mmkv.encode(SHOW_CONTACT_FIELDS, showContactFields)
        }

    var showDialpadButton: Boolean
        get() = mmkv.decodeBool(SHOW_DIALPAD_BUTTON, true)
        set(showDialpadButton) {
            mmkv.encode(SHOW_DIALPAD_BUTTON, showDialpadButton)
        }

    var wasLocalAccountInitialized: Boolean
        get() = mmkv.decodeBool(WAS_LOCAL_ACCOUNT_INITIALIZED, false)
        set(wasLocalAccountInitialized) {
            mmkv.encode(WAS_LOCAL_ACCOUNT_INITIALIZED, wasLocalAccountInitialized)
        }

    var lastExportPath: String
        get() = mmkv.decodeString(LAST_EXPORT_PATH, "")!!
        set(lastExportPath) {
            mmkv.encode(LAST_EXPORT_PATH, lastExportPath)
        }

    var speedDial: String
        get() = mmkv.decodeString(SPEED_DIAL, "")!!
        set(speedDial) {
            mmkv.encode(SPEED_DIAL, speedDial)
        }

    var showPrivateContacts: Boolean
        get() = mmkv.decodeBool(SHOW_PRIVATE_CONTACTS, true)
        set(showPrivateContacts) {
            mmkv.encode(SHOW_PRIVATE_CONTACTS, showPrivateContacts)
        }

    var mergeDuplicateContacts: Boolean
        get() = mmkv.decodeBool(MERGE_DUPLICATE_CONTACTS, true)
        set(mergeDuplicateContacts) {
            mmkv.encode(MERGE_DUPLICATE_CONTACTS, mergeDuplicateContacts)
        }

    var favoritesContactsOrder: String
        get() = mmkv.decodeString(FAVORITES_CONTACTS_ORDER, "")!!
        set(order) {
            mmkv.encode(FAVORITES_CONTACTS_ORDER, order)
        }

    var isCustomOrderSelected: Boolean
        get() = mmkv.decodeBool(FAVORITES_CUSTOM_ORDER_SELECTED, false)
        set(selected) {
            mmkv.encode(FAVORITES_CUSTOM_ORDER_SELECTED, selected)
        }

    var viewType: Int
        get() = mmkv.decodeInt(VIEW_TYPE, VIEW_TYPE_LIST)
        set(viewType) {
            mmkv.encode(VIEW_TYPE, viewType)
        }

    var contactsGridColumnCount: Int
        get() = mmkv.decodeInt(CONTACTS_GRID_COLUMN_COUNT, getDefaultContactColumnsCount())
        set(contactsGridColumnCount) {
            mmkv.encode(CONTACTS_GRID_COLUMN_COUNT, contactsGridColumnCount)
        }

    private fun getDefaultContactColumnsCount(): Int {
        val isPortrait = context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        return if (isPortrait) {
            context.resources.getInteger(R.integer.contacts_grid_columns_count_portrait)
        } else {
            context.resources.getInteger(R.integer.contacts_grid_columns_count_landscape)
        }
    }

    var autoBackup: Boolean
        get() = mmkv.decodeBool(AUTO_BACKUP, false)
        set(autoBackup) {
            mmkv.encode(AUTO_BACKUP, autoBackup)
        }

    var autoBackupFolder: String
        get() = mmkv.decodeString(AUTO_BACKUP_FOLDER, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)!!
        set(autoBackupFolder) {
            mmkv.encode(AUTO_BACKUP_FOLDER, autoBackupFolder)
        }

    var autoBackupFilename: String
        get() = mmkv.decodeString(AUTO_BACKUP_FILENAME, "")!!
        set(autoBackupFilename) {
            mmkv.encode(AUTO_BACKUP_FILENAME, autoBackupFilename)
        }

    var lastAutoBackupTime: Long
        get() = mmkv.decodeLong(LAST_AUTO_BACKUP_TIME, 0L)
        set(lastAutoBackupTime) {
            mmkv.encode(LAST_AUTO_BACKUP_TIME, lastAutoBackupTime)
        }


    var passwordRetryCount: Int
        get() = mmkv.decodeInt(PASSWORD_RETRY_COUNT, 0)
        set(passwordRetryCount) {
            mmkv.encode(PASSWORD_RETRY_COUNT, passwordRetryCount)
        }

    var passwordCountdownStartMs: Long
        get() = mmkv.decodeLong(PASSWORD_COUNTDOWN_START_MS, 0L)
        set(passwordCountdownStartMs) {
            mmkv.encode(PASSWORD_COUNTDOWN_START_MS, passwordCountdownStartMs)
        }
}
