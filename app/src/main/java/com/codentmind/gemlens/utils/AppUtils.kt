package com.codentmind.gemlens.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.statusBars
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import com.codentmind.gemlens.GemLensApp
import com.codentmind.gemlens.R
import timber.log.Timber
import java.util.Locale

/**
 * @Details AppUtils: Common Utility Class to handle the utils functions
 * @Author Roshan Bhagat
 */

/**
 * Is network connected check and return the n/w availability of user's device
 *
 * @param context
 * @return boolean true isNetworkConnected else false
 */
fun isNetworkConnected(): Boolean {
    val connectivityManager =
        GemLensApp.getInstance()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
        NetworkCapabilities.TRANSPORT_CELLULAR
    ) || capabilities.hasTransport(
        NetworkCapabilities.TRANSPORT_VPN
    ))
}

/**
 * Show log
 *
 * @param tagName
 * @param message
 */
fun showLog(tagName: String, message: String) {
    if (!TextUtils.isEmpty(message)) {
        val maxLogSize = message.length + 1
        for (i in 0..message.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > message.length)
                message.length
            else end
            Timber.tag(tagName).v(message.substring(start, end))
        }
    }
}

/**
 * Log exception
 *
 * @param t
 */
fun logException(t: Throwable?) {
    Timber.tag("").e(Log.getStackTraceString(t))
}

/**
 * Is list not empty
 *
 * @param list
 */
fun isListNotEmpty(list: List<Any>?) = !(list?.isEmpty() ?: true)

/**
 * Is valid string
 *
 * @param value
 */
fun String?.isValidString() = !TextUtils.isEmpty(this)


/**
 * Navigate with Arguments
 *
 * @receiver [NavController]
 * @param route Route
 * @param args Args
 * @param navOptions Nav options
 * @param navigatorExtras Navigator extras
 */
fun NavController.navigateWithArgs(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val routeLink = NavDeepLinkRequest
        .Builder
        .fromUri(NavDestination.createRoute(route).toUri())
        .build()

    val deepLinkMatch = graph.matchDeepLink(routeLink)
    if (deepLinkMatch != null) {
        val destination = deepLinkMatch.destination
        val id = destination.id
        navigate(id, args, navOptions, navigatorExtras)
    } else {
        navigate(route, navOptions, navigatorExtras)
    }
}

fun Context.shareText(text: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.share))
    intent.putExtra(Intent.EXTRA_TEXT, text)
    this.startActivity(
        Intent.createChooser(
            intent,
            this.getString(R.string.share_by)
        )
    )
}

fun String.toCamelCase(): String {
    val space = " "
    val splitedStr = this.split(space)
    return splitedStr.joinToString(space) { str ->
        str.lowercase().replaceFirstChar {
            if (it.isLowerCase()) {
                val dd = it.uppercaseChar().toString()
                dd
            } else {
                it.toString()
            }
        }
    }
}

/**
 * Speak to add
 *
 * @param speakLauncher
 */
fun Context.speakToAdd(speakLauncher: ActivityResultLauncher<Intent>?) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
    intent.putExtra(
        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
    )
    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    if (packageManager.resolveActivity(intent, 0) != null) {
        speakLauncher?.launch(intent)
    } else {
        Toast.makeText(
            this,
            getString(R.string.speech_not_support), Toast.LENGTH_SHORT
        ).show()
    }
}

/**
 * Register activity for result
 *
 * @param block
 */
inline fun ComponentActivity.registerActivityForResult(
    crossinline block: (Intent) -> Unit
) = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    block(intent)
}

inline fun ComponentActivity.postDel1ay(
    delayInMs: Long,
    crossinline block: () -> Unit,
) = lifecycleScope.postDelay(delayInMs) {
    block()
}


fun Activity.hideSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controller.hide(WindowInsetsCompat.Type.systemBars())
    window?.let {
        WindowCompat.setDecorFitsSystemWindows(
            it,
            false
        ) //Make sure the window can expand behind system bars
        WindowCompat.getInsetsController(it, it.decorView).apply {
            hide(statusBars())
            isAppearanceLightStatusBars = false // Set to false for dark text, true for light
            isAppearanceLightNavigationBars = false
        }
    }
}

fun Activity.showSystemBars() {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.show(WindowInsetsCompat.Type.systemBars())
    window?.let {
        //WindowCompat.setDecorFitsWindows(it, true)
        WindowCompat.setDecorFitsSystemWindows(
            it,
            true
        ) //Make sure the window can expand behind system bars
        WindowCompat.getInsetsController(it, it.decorView).apply {
            show(statusBars())
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
    }
}


fun setStatusBarColor(window: Window, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
        window.decorView.setOnApplyWindowInsetsListener { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
            view.setBackgroundColor(color)

            // Adjust padding to avoid overlap
            view.setPadding(0, statusBarInsets.top, 0, 0)
            insets
        }
    } else {
        // For Android 14 and below
        window.statusBarColor = color
    }
}
