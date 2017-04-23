package no.rbkss.snaut

import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime

import scala.xml._
import scala.xml.transform._
import org.apache.commons.io.FileUtils

// TODO: set up root <svg>-element manually to avoid the initial required setFragment()

class Image {
    var svg: Option[Node] = None

    def setFragment(node: Node): Unit = {
        svg = Some(node)
    }

    def addFragment(node: Node): Unit = {
        if (svg.isEmpty) {

        }
        else {
            svg = svg.get match {
                case Elem(prefix, label, attribs, scope, child@_*) =>
                    Some(Elem(prefix, label, attribs, scope, false, child ++ node: _*))
                case x: Elem => Some(x)
                case x: Node => Some(x)
            }
        }
    }

    def addFragment(filename: String): Unit = {
        val data: Node = XML.loadFile(s"/Users/larsa/Code/rbk/snaut/src/main/resources/sym/svg/${filename}.svg")
        if (svg.isEmpty) {
            setFragment(data)
        } else {
            for (child <- data.child)
                addFragment(child)
        }
    }

    def addFragment(filename: String, transform: String): Unit = {
        val data: Node = XML.loadFile(s"/Users/larsa/Code/rbk/snaut/src/main/resources/sym/svg/${filename}.svg")
        if (svg.isEmpty) {
            setFragment(<g transform={transform}>{data}</g>)
        } else {
            for (child <- data.child)
                addFragment(<g transform={transform}>{child}</g>)
        }
    }

    def addYrLogo(): Unit = {
        val data: Node = XML.loadFile(s"/Users/larsa/Code/rbk/snaut/src/main/resources/yr-logo.svg")
        addFragment(data)
    }

    def addCloudSymbol(variant: String): Unit = {
        if (variant.startsWith("mf/")) {
            val frac = variant.split('.')(1)
            val sym = variant.split('/')(1).substring(0,3)
            addFragment(sym) // TODO: remove - can't add with transform as first item yet
            addFragment("mf/01n." + frac, "translate(10,10) scale(0.6,0.6)")
            addFragment(sym)
        } else {
            addFragment(variant)
        }
    }

    def addTemperature(temp: Float): Unit = {
        val fill = if (temp >= 0) "red" else "blue"
        val tempstr = f"${temp}%.1g˚"
        addFragment(<text x="100" y="100" fill={fill} text-anchor="end" font-size="30" font-family="Arial, Helvetica, sans-serif">{tempstr}</text>)
    }

    def addWind(speed: Float, direction: Float): Unit = {
        var arrowFile = "/Users/larsa/Code/rbk/snaut/src/main/resources/sym/svg/wind/"
        Forecast.getWindSpeedName(speed) match {
            case "stille" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowCalm_00.svg"
            case "flau vind" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_01.svg"
            case "svak vind" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_02.svg"
            case "lett bris" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_03.svg"
            case "laber bris" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_04.svg"
            case "frisk bris" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_05.svg"
            case "liten kuling" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_06.svg"
            case "sterk kuling" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_07.svg"
            case "liten storm" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_08.svg"
            case "full storm" => arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_09.svg"
            case _ =>
                if (speed <= 28.4)
                    arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_10.svg"
                else
                    arrowFile = arrowFile + "WeatherSymbol_WMO_WindArrowNH_11.svg"
        }
        val xml = XML.loadFile(arrowFile)
        val element = xml.child(xml.child.size-2)
        var dir = direction + 90.0
        if (dir > 360) dir = dir - 360
        val replacement = if (speed == 0) "translate(25,89) scale(0.67,0.67)" else f"translate(30,89) rotate(${dir}%.1f) translate(12,0) scale(0.5,0.5)"
        val rule1 = new RewriteRule {
            override def transform(n: Node) = n match {
                case e @ <g>{_*}</g> => e.asInstanceOf[Elem] %
                  Attribute(null, "transform", replacement, Null)
                case _ => n
            }
        }
        val rewritten = new RuleTransformer(rule1).transform(element)
        addFragment(rewritten(0))
    }

    def addLocationHeader(location: String, timerange: String): Unit = {
        addFragment(<text x="5" y="13" fill="black" textWidth="90" text-anchor="beginning" font-size="13" font-family="Arial, Helvetica, sans-serif">{location}</text>)
        addFragment(<text x="5" y="22" fill="black" textWidth="90" text-anchor="beginning" font-size="8" font-family="Arial, Helvetica, sans-serif">{timerange}</text>)
    }

    def addText(forecast: String): Unit = {
        addFragment(<foreignObject x="5" y="95" width="90" height="55">
            <p xmlns="http://www.w3.org/1999/xhtml" style="line-height:60%;">
                <font size="1">{forecast}</font>
            </p>
        </foreignObject>)
    }

    def addUpdateTime(time: String): Unit = {
        val text = "Oppdatert " + time
        addFragment(<text x="100" y="149" text-anchor="end" font-size="6" font-family="Arial, Helvetica, sans" fill="lightgrey">{text}</text>)
    }

    def addUpdateTime(): Unit = {
        val now = LocalDateTime.now()
        val formatter = new SimpleDateFormat("HH:mm")
        addUpdateTime(formatter.format(now))
    }

    //def setFromForecast(forecast: Forecast): Unit = {
    //}

    def export(filename: String): Boolean = {
        val rule1 = new RewriteRule {
            override def transform(n: Node) = n match {
                case e @ <svg>{_*}</svg> => e.asInstanceOf[Elem] %
                  Attribute(null, "height", "150",
                      Attribute(null, "viewbox", "0 0 100 150", Null))
                case _ => n
            }
        }

        val rewritten = new RuleTransformer(rule1).transform(svg.get)
        val prettyPrinter = new PrettyPrinter(80, 4)
        val prettyXml = prettyPrinter.format(rewritten(0)).replaceAll("stroke-width:1.", "stroke-width:2.")
        FileUtils.write(new File(s"${filename}"), prettyXml, "UTF-8")
        return true
    }
}
