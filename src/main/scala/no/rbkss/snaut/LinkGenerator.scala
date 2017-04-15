package no.rbkss.snaut

object LinkGenerator {
    def getLink(locationPath: String): String = {
        val text = "<a title=\"Weather forecast from yr.no, delivered by the Norwegian Meteorological Institute and the NRK\" href=\"http://www.yr.no/place/" + locationPath + "\">"
        text
    }
}
