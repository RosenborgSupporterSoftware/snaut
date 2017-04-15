package no.rbkss.snaut

import scala.xml.XML
import scala.xml.Elem
import scala.xml.Node

import java.util.Date
import java.net.URL
import grizzled.slf4j.Logging
import org.joda.time.DateTime
import scala.language.postfixOps

case class Symbol(symbol: Integer, symbolExtended: Integer, variant: String, name: String)
case class Precipitation(value: Float, minimum: Float, maximum: Float)
case class WindDirection(degrees: Float, code: String, name: String)
case class WindSpeed(value: Float, unit: String, name: String)
case class Temperature(value: Float, unit: String)
case class Pressure(value: Float, unit: String)

case class ForecastSnapshot(from: DateTime,
                            to: DateTime,
                            symbol: Symbol,
                            percipitation: Precipitation,
                            windDirection: WindDirection,
                            windSpeed: WindSpeed,
                            temperature: Temperature,
                            pressure: Pressure)

class Forecast extends Logging {
    var forecast: Option[Elem] = None

    def loadURL(url: URL): Forecast = {
        forecast = Some(XML.load(url))
        return this
    }

    def loadFile(file: String): Forecast = {
        forecast = Some(XML.loadFile(file))
        return this
    }

    def loadString(string: String): Forecast = {
        forecast = Some(XML.loadString(string))
        return this
    }

    private def getTabularTime(date: DateTime): Option[Node] = {
        // NB: there is a two-hour gap in the forecast data that will be missed for now
        // (it's during nighttime, so no conflict with football games)
        for (time <- (forecast.get \\ "weatherdata" \ "forecast" \ "tabular" \ "time")) {
            if (DateTime.parse((time \ "@from") text).getMillis <= date.getMillis &&
                date.getMillis <= DateTime.parse((time \ "@to") text).getMillis)
                return Some(time)
        }
        return None
    }

    def containsDate(date: DateTime): Boolean = {
        return getTabularTime(date) match {
            case Some(time) => true
            case _ => false
        }
    }

    def getTextForecast(date: DateTime): String = {
        val Some(forecast) = getSnapshot(date)
        var clouds = Forecast.getWeatherName(forecast.symbol.name)
        var direction = Forecast.getWindDirectionName(forecast.windDirection.code)
        val windtype = Forecast.getWindSpeedName(forecast.windSpeed.value)
        var wind = s"${windtype}, ${forecast.windSpeed.value} m/s fra ${direction}.".capitalize
        var rain = s"${forecast.percipitation.value} mm nedbør."
        s"${clouds} ${wind} ${rain}"
    }

    def getSnapshot(date: DateTime): Option[ForecastSnapshot] = {
        implicit class PipedObject[A](value: A) {
           def |>[B](f: A => B): B = f(value)
        }
        return getTabularTime(date) match {
            case Some(time) => {
                var from = DateTime.parse((time \ "@from") text)
                var to = DateTime.parse((time \ "@to") text)
                var symbol = (time \ "symbol") |> (tag =>
                    Symbol(((tag \ "@number") text).toInt,
                           ((tag \ "@numberEx") text).toInt,
                           (tag \ "@var") text,
                           (tag \ "@name") text))
                var precipitation = (time \ "precipitation") |> (tag =>
                    Precipitation(((tag \ "@value") text).toFloat,
                                  ((tag \ "@minvalue") text).toFloat,
                                  ((tag \ "@maxvalue") text).toFloat))
                var windDirection = (time \ "windDirection") |> (tag =>
                    WindDirection(((tag \ "@deg") text).toFloat,
                                  (tag \ "@code") text,
                                  (tag \ "@name") text))
                var windSpeed = (time \ "windSpeed") |> (tag =>
                    WindSpeed(((tag \ "@mps") text).toFloat,
                              "mps",
                              (tag \ "@name") text))
                var temperature = (time \ "temperature") |> (tag =>
                    Temperature(((tag \ "@value") text).toFloat,
                                (tag \ "@unit") text))
                var pressure = (time \ "pressure") |> (tag =>
                    Pressure(((tag \ "@value") text).toFloat,
                             (tag \ "@unit") text))
                Some(ForecastSnapshot(from, to, symbol, precipitation, windDirection, windSpeed, temperature, pressure))
            }
            case _ => None
        }
    }
}

object Forecast {
    // http://om.yr.no/forklaring/symbol/

    //def getCloudSymbol(name: String): String = {
    //    name match {
    //        case "clear sky" => "01d"
    //    }
    //}

    def getWindDirectionName(code: String): String = {
        code match {
            case "N" => "nord"
            case "NNW" => "nord-nordvest"
            case "NW" => "nordvest"
            case "WNW" => "vest-nordvest"
            case "W" => "vest"
            case "WSW" => "vest-sørvest"
            case "SW" => "sørvest"
            case "SSW" => "sør-sørvest"
            case "S" => "sør"
            case "SSE" => "sør-sørøst"
            case "SE" => "sørøst"
            case "ESE" => "øst-sørøst"
            case "E" => "øst"
            case "ENE" => "øst-nordøst"
            case "NE" => "nordøst"
            case "NNE" => "nord-nordøst"
            case _ => "ukjent"
        }
    }

    def getWindSpeedName(speed: Float): String = {
        if (speed <= 0.2) "stille"
        else if (speed <= 1.5) "flau vind"
        else if (speed <= 3.3) "svak vind"
        else if (speed <= 5.4) "lett bris"
        else if (speed <= 7.9) "laber bris"
        else if (speed <= 10.7) "frisk bris"
        else if (speed <= 13.8) "liten kuling"
        else if (speed <= 17.1) "stiv kuling"
        else if (speed <= 20.7) "sterk kuling"
        else if (speed <= 24.4) "liten storm"
        else if (speed <= 28.4) "full storm"
        else "ukjent"
    }

    def getWeatherName(symbol: String): String = {
        symbol match {
            case "Clear sky" => "Sol/klarvær"
            case "Fair" => "Lettskyet"
            case "Partly cloudy" => "Delvis skyet"
            case "Cloudy" => "Skyet"
            case "Light rain showers" => "Lette regnbyger"
            case "Rain showers" => "Regnbyger"
            case "Heavy rain showers" => "Kraftige regnbyger"
            case "Light rain showers and thunder" => "Lette regnbyger og torden"
            case "Rain showers AND thunder" => "Regnbyger og torden"
            case "Heavy rain showers and thunder" => "Kraftige regnbyger og torden"
            case "Light sleet showers" => "Lette sluddbyger"
            case "Sleet showers" => "Sluddbyger"
            case "Heavy sleet showers" => "Kraftige sluddbyger"
            case _ => "ukjent"
        }
    }
}
