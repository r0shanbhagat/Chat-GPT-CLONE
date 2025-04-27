package com.codentmind.gemlens.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
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
import java.io.IOException
import java.util.Locale

/**
 * @Details AppUtils: Common Utility Class to handle the utils functions
 * @Author Roshan Bhagat
 */

/**
 * Is network connected check and return the n/w availability of user's device
 * @param context
 * @return boolean true isNetworkConnected else false
 */
fun isNetworkConnected(): Boolean {
    val connectivityManager =
        GemLensApp.getInstance()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}


/**
 * Show log
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
 * @param t
 */
fun logException(t: Throwable?) {
    Timber.tag("").e(Log.getStackTraceString(t))
}

/**
 * Checks if a list is not empty.
 * @param list
 */
fun isListNotEmpty(list: List<Any>?) = !(list?.isEmpty() ?: true)

/**
 * Is valid string
 * Checks if string is valid.
 * @param value
 */
fun String?.isValidString() = !TextUtils.isEmpty(this)


/**
 * Navigate with Arguments
 * @receiver [NavController]
 * @param route Route
 * @param args Args
 * @param navOptions Nav options
 * @param navigatorExtras Navigator extras
 */
@SuppressLint("RestrictedApi")
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


/**
shares text content with other apps.
 * @param text
 */
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

/***
 * The input string is split into words, and each word is capitalized.
 * @return The camel case version of the input string.
 */
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
 * Launches speech recognition to add text.
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
 * Registers an activity for result using the StartActivityForResult contract.
 * @param block
 */
inline fun ComponentActivity.registerActivityForResult(
    crossinline block: (Intent) -> Unit
) = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    block(intent)
}

/**
 * This function uses the lifecycleScope to ensure that the coroutine is cancelled when the
 * associated lifecycle is destroyed.
 *
 * @param delayInMs The delay in milliseconds.
 */
inline fun ComponentActivity.postDel1ay(
    delayInMs: Long,
    crossinline block: () -> Unit,
) = lifecycleScope.postDelay(delayInMs) {
    block()
}

/**
 * Hides the system bars (status bar, navigation bar).
 */
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

/**
 * Shows the system bars (status bar, navigation bar). *
 */
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

/**
Sets the status bar color.
 * @param window
 * @param color
 */
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

/**
 * Gets the system vibrator service.
 *
 * @return Vibrator
 */
fun Context.vibrator(): Vibrator {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(VIBRATOR_SERVICE) as Vibrator
    }
}

/**
 * Triggers a short vibration using the default amplitude.
 *
 * This function is an extension function for the Vibrator class.
 */
fun Context.vibrate() {
    vibrator().vibrate(
        VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
    )
}

/**
 * Reads a JSON file from the assets directory.
 *
 * This function reads a JSON file located in the assets directory and returns its contents as a
 * String.
 *
 * @param fileName The name of the file to read.
 */
fun readJsonFromAssets(context: Context, fileName: String): String? {
    return try {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (ex: IOException) {
        Timber.e(ex.message)
        null
    }
}


/***
 * This function takes a drawable name as a string and returns its corresponding resource ID.
 *
 * @param name The name of the drawable resource.
 * @return The drawable resource ID, or 0 if the resource is not found.
 */
@SuppressLint("DiscouragedApi")
fun Context.getDrawableResId(name: String): Int {
    if (!name.isValidString()) return 0

    return try {
        this.resources.getIdentifier(name, "drawable", this.packageName)
    } catch (rne: Exception) {
        Timber.e(rne.message)
        0
    }
}


