package dev.josecaldera.indicators

import android.app.Application
import dev.josecaldera.indicators.di.applicationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class IndicatorsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@IndicatorsApplication)
            modules(
                applicationModule
            )
        }
    }
}
