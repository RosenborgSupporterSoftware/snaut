package no.rbkss.snaut

import com.typesafe.config.ConfigFactory
import scala.collection.mutable.HashMap

object LocationMgr {
    var config = ConfigFactory.load()
    val baseurl = config.getString("forecasts.baseurl")

    def urlForLocation(location: String): Option[String] = {
        val path = "locations." + location.toLowerCase
        if (config.hasPath(path))
            return Some(baseurl + config.getString(path))
        return None
    }
}
