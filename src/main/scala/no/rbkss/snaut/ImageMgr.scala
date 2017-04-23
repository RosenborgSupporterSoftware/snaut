package no.rbkss.snaut

import java.text.SimpleDateFormat
import java.time.LocalDateTime

import org.joda.time.DateTime

class ImageMgr(symbolsdir: String) {
    val symbolsDir: String = symbolsdir

    def imageForForecast(location: String, time: String, datetime: DateTime, forecast: Forecast): Option[Image] = {
        val snapshot = forecast.getSnapshot(datetime)
        if (snapshot.isEmpty)
            imageForNothing(location, time)
        else {
            val Some(current) = snapshot
            val img = new Image()
            img.addCloudSymbol(current.symbol.variant)
            img.addLocationHeader(location, time)
            img.addTemperature(current.temperature.value)
            img.addWind(current.windSpeed.value, current.windDirection.degrees)
            img.addUpdateTime()
        }
        return None
    }

    def imageForNothing(location: String, time: String): Option[Image] = {
        val img = new Image()
        img.addCloudSymbol("01d")                    // sunny
        img.addLocationHeader(location, time)
        img.addTemperature(16)                       // 16˚
        img.addWind(0, 0)                            // no wind
        img.addText("Klarvær. Stille, 0m/s vind. 0mm nedbør")
        val now = LocalDateTime.now()
        val formatter = new SimpleDateFormat("HH:mm")
        img.addUpdateTime(formatter.format(now))     // update timestamp
        // no yr.no image since prognosis is fake
        Some(img)
    }

}
