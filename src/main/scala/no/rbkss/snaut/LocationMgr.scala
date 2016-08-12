package no.rbkss.snaut

import scala.collection.mutable.HashMap

object LocationMgr {
    val yrbase = "http://www.yr.no/place/"
    var locations = new HashMap[String, String]()
    locations += ("Lerkendal" -> "Norway/S%C3%B8r-Tr%C3%B8ndelag/Trondheim/Lerkendal~211228")

    def urlForLocation(location: String): Option[String] = { // to limit use to rbkweb-usage for now
        locations.get(location) match {
            case Some(yrloc) => Some(yrbase + yrloc + "/")
            case None => None
        }
    }
}
