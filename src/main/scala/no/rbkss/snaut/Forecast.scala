package no.rbkss.snaut

import scala.xml.XML
import scala.xml.Elem
import scala.xml.Node

import java.util.Date
import java.net.URL
import grizzled.slf4j.Logging
import org.joda.time.DateTime

case class ForecastSnapshot(from: DateTime,
                            to: DateTime,
                            temperature: Float,
                            percipitation: Float,
                            symbol: Integer)

class Forecast extends Logging {
    var forecast: Option[Elem] = None

    def withURL(url: URL): Forecast = {
        forecast = Some(XML.load(url))
        return this
    }

    def withFile(file: String): Forecast = {
        forecast = Some(XML.loadFile(file))
        return this
    }

    def withString(string: String): Forecast = {
        forecast = Some(XML.loadString(string))
        return this
    }

    private def getTabularTime(date: DateTime): Option[Node] = {
        for (time <- (forecast.get \\ "weatherdata" \ "forecast" \ "tabular" \ "time")) {
            var from = DateTime.parse((time \ "@from") text)
            var to = DateTime.parse((time \ "@to") text)
            if (from.getMillis <= date.getMillis && date.getMillis <= to.getMillis)
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
        return getTabularTime(date) match {
            case Some(time) => {
                var from = DateTime.parse((time \ "@from") text)
                var to = DateTime.parse((time \ "@to") text)
                var temperature = ((time \ "temperature" \ "@value") text).toFloat
                var precipitation = ((time \ "precipitation" \ "@value") text).toFloat
                var minprecipitation = ((time \ "precipitation" \ "@minvalue") text).toFloat
                var maxprecipitation = ((time \ "precipitation" \ "@maxvalue") text).toFloat
                var symbol = ((time \ "symbol" \ "@number") text).toInt
                Some(new ForecastSnapshot(from, to, temperature, precipitation, symbol))
            }
            case _ => None
        }
    }

//    def getSnapshot(when: Date): ForecastSnapshot = {
//        null
//    }
}

object Main extends App {
    val f = new Forecast
}

