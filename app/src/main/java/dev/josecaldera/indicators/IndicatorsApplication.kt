package dev.josecaldera.indicators

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IndicatorsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
