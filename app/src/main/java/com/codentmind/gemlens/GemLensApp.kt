package com.codentmind.gemlens

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import com.codentmind.gemlens.di.module
import com.codentmind.gemlens.utils.AppSession
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

/**
 * @Details GemLens AI App is the Application class.
 * @Author Roshan Bhagat
 */

class GemLensApp : Application(), ImageLoaderFactory {

    internal val sessionData: AppSession by inject()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initKoin()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@GemLensApp)
            androidLogger(Level.DEBUG)
            modules(module)
        }
    }


    /**
     * Since we're displaying SVGs in the app, Coil needs an ImageLoader which supports this
     * format. During Coil's initialization it will call `applicationContext.newImageLoader()` to
     * obtain an ImageLoader.
     *
     * @see https://github.com/coil-kt/coil/blob/main/coil-singleton/src/main/java/coil/Coil.kt#L63
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this).components {
            add(SvgDecoder.Factory())
        }.build()
    }

    override fun onTerminate() {
        super.onTerminate()
        sessionData.clear()
    }


    companion object {
        @Volatile
        private var INSTANCE: GemLensApp? = null

        fun getInstance(): GemLensApp = INSTANCE ?: synchronized(this) {
            INSTANCE ?: GemLensApp().also { INSTANCE = it }
        }
    }
}