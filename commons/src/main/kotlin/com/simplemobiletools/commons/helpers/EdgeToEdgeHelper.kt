package com.simplemobiletools.commons.helpers

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

object EdgeToEdgeHelper {

    /**
     * Initializes Toolbar as ActionBar for the activity, and applies window insets.
     * Status bar inset padding is added to the toolbar (or top of root).
     * Navigation bar inset padding is added to the bottom layout/view if provided.
     */
    @JvmStatic
    @JvmOverloads
    fun setupToolbarInsetsSystemBars(
        activity: AppCompatActivity,
        toolbar: Toolbar? = null,
        title: CharSequence? = null,
        showHomeAsUp: Boolean = true,
        topView: View? = null,
        bottomView: View? = null,
    ): Toolbar? {
        if (toolbar != null) {
            activity.setSupportActionBar(toolbar)
            val resolvedTitle = if (!title.isNullOrBlank()) {
                title
            } else if (!activity.title.isNullOrBlank()) {
                activity.title
            } else {
                null
            }
            activity.supportActionBar?.apply {
                if (resolvedTitle != null) {
                    this.title = resolvedTitle
                }
                setDisplayHomeAsUpEnabled(showHomeAsUp)
            }
        }

        applyWindowInsets(
            topView = topView ?: toolbar,
            topViewHorizontalZeroInsets = toolbar != null,
            bottomView = bottomView,
        )

        setSystemBarsThemeMode(activity)
        return toolbar
    }

    /**
     * Applies WindowInsets for edge-to-edge display:
     * - topView receives status bar top inset as extra top padding.
     * - topViewZeroInsetsHorizontalPadding: toolbar이면 true로 해서, 패딩을 넣지 않도록 한다.
     * - bottomView receives navigation bar bottom inset as extra bottom padding.
     */
    @JvmStatic
    fun applyWindowInsets(topView: View?, topViewHorizontalZeroInsets: Boolean, bottomView: View?) {
        if (!isAndroid15OrHigher()) return
        if (topView != null && topView === bottomView) {
            val leftInitialPadding = topView.paddingLeft
            val topInitialPadding = topView.paddingTop
            val bottomInitialPadding = topView.paddingBottom
            val rightInitialPadding = topView.paddingRight
            ViewCompat.setOnApplyWindowInsetsListener(topView) { v, windowInsets ->
                val insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                v.setPadding(
                    leftInitialPadding + if (topViewHorizontalZeroInsets) 0 else insets.left,
                    topInitialPadding + insets.top,
                    rightInitialPadding + if (topViewHorizontalZeroInsets) 0 else insets.right,
                    bottomInitialPadding + insets.bottom
                )
                windowInsets
            }
            return
        }

        if (topView != null) {
            val leftInitialPadding = topView.paddingLeft
            val topInitialPadding = topView.paddingTop
            val rightInitialPadding = topView.paddingRight
            ViewCompat.setOnApplyWindowInsetsListener(topView) { v, windowInsets ->
                val insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                )
                v.setPadding(
                    leftInitialPadding + if (topViewHorizontalZeroInsets) 0 else insets.left,
                    topInitialPadding + insets.top,
                    rightInitialPadding + if (topViewHorizontalZeroInsets) 0 else insets.right,
                    v.paddingBottom
                )
                windowInsets
            }
        }

        if (bottomView != null) {
            val leftInitialPadding = bottomView.paddingLeft
            val bottomInitialPadding = bottomView.paddingBottom
            val rightInitialPadding = bottomView.paddingRight
            ViewCompat.setOnApplyWindowInsetsListener(bottomView) { v, windowInsets ->
                val insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )
                v.setPadding(
                    leftInitialPadding + insets.left,
                    v.paddingTop,
                    rightInitialPadding + insets.right,
                    bottomInitialPadding + insets.bottom
                )
                windowInsets
            }
        }
    }

    /**
     * Applies top and bottom WindowInsets to a drawer pane view (e.g., drawer_view).
     * The drawer container (drawer_view) remains transparent in the status bar area,
     * while the inner content (R.id.drawer_content or the view itself) is offset below the status bar.
     * drawerContent: drawerView안의 content view로 이 값이 있으면 top padding이 아니라, inner content의 top margin으로 , 결과는 사실 같다.
     */
    @JvmStatic
    fun applyDrawerInsets(drawerView: View?, drawerContent: View? = null) {
        if (!isAndroid15OrHigher()) return
        if (drawerView == null) return
        val leftInitialPadding = drawerView.paddingLeft
        val topInitialPadding = drawerView.paddingTop
        val bottomInitialPadding = drawerView.paddingBottom
        val rightInitialPadding = drawerView.paddingRight
        ViewCompat.setOnApplyWindowInsetsListener(drawerView) { v, windowInsets ->
            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            if (drawerContent != null) {
                // If drawer_content exists, drawerView padding keeps the top transparent,
                // or we apply top margin to drawerContent so status bar area is transparent.
                val lp = drawerContent.layoutParams as? ViewGroup.MarginLayoutParams
                if (lp != null) {
                    lp.topMargin = insets.top
                    drawerContent.layoutParams = lp
                }
                v.setPadding(
                    leftInitialPadding + insets.left,
                    topInitialPadding,
                    rightInitialPadding + insets.right,
                    bottomInitialPadding + insets.bottom
                )
            } else {
                v.setPadding(
                    leftInitialPadding + insets.left,
                    topInitialPadding + insets.top,
                    rightInitialPadding + insets.right,
                    bottomInitialPadding + insets.bottom
                )
            }
            windowInsets
        }
    }

    @JvmStatic
    fun setSystemBarsThemeMode(
        activity: Activity,
        isStatusLightMode: Boolean, // AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES, 는 실제 앱 테마를 불러오지는 않는다.
        isNavigationLightMode: Boolean,
    ) {
        if (!isAndroid15OrHigher()) return
        val compat = WindowInsetsControllerCompat(activity.window, activity.window.decorView)
        compat.isAppearanceLightStatusBars = isStatusLightMode
        compat.isAppearanceLightNavigationBars = isNavigationLightMode
    }

    // MyApp에서 한 번 초기화하고 계속 쓰자..
    var isAppearanceLightStatusBars: Boolean? = false
    var isAppearanceLightNavigationBars: Boolean? = null
    var isNavigationBarContrastEnforced: Boolean? = false   //앱 테마를 지원하면은 false로 유지하는게 좋다.

    @JvmStatic
    fun setSystemBarsThemeMode(activity: Activity) {
        if (!isAndroid15OrHigher()) return
        val compat = WindowInsetsControllerCompat(activity.window, activity.window.decorView)

        isAppearanceLightStatusBars?.let {
            compat.isAppearanceLightStatusBars = it
        }

        //  네비게이션바 테마
        val isNavigationLight = isAppearanceLightNavigationBars ?: run {
            val isDark = (activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
            !isDark
        }
        compat.isAppearanceLightNavigationBars = isNavigationLight

        // 네비게이션바위에 뿌연 것을 넣을지 말지. false이면 완전 투명하게 된다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            isNavigationBarContrastEnforced?.let {
                activity.window.isNavigationBarContrastEnforced = it
            }
        }
    }

    private fun isAndroid15OrHigher() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM //안드로이드15이상
}
