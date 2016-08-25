package no.rbkss.snaut

import scala.xml.XML
import scala.xml.Elem
import scala.xml.Node

import java.util.Date
import java.net.URL
import grizzled.slf4j.Logging
import org.joda.time.DateTime

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
