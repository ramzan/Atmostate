package ca.ramzan.atmostate

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory

@Suppress("unused")
class App : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .build()
    }
}
