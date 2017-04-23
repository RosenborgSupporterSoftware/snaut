package no.rbkss.snaut

object LinkGenerator {
    def getLinkTitle(): String = {
        "Weather forecast from yr.no, delivered by the Norwegian Meteorological Institute and the NRK."
    }

    def getLinkLocation(locationPath: String): String = {
        "http://www.yr.no/place/" + locationPath
    }

    def getLink(locationPath: String): String = {
        val title = getLinkTitle()
        val location = getLinkLocation(locationPath)
        "<a title=\"" + title + "\" href=\"" + location + "\">"
    }
}
