package no.rbkss.snaut

import com.typesafe.config.ConfigFactory
import scala.collection.mutable.HashMap

object LocationMgr {
    var config = ConfigFactory.load("locations").resolve()
    val baseurl = config.getString("forecasts.baseurl")

    def urlForLocation(location: String): Option[(String, String)] = {
        val path1 = "locations." + location.toLowerCase + ".longterm"
        val path2 = "locations." + location.toLowerCase + ".shortterm"
        if (config.hasPath(path1) && config.hasPath(path2))
            return Some(config.getString(path1), config.getString(path2))
        return None
    }

    def nameForLocation(location: String): Option[String] = {
        val path = "locations." + location.toLowerCase + ".name"
        if (config.hasPath(path))
            return Some(config.getString(path))
        return None
    }
}
