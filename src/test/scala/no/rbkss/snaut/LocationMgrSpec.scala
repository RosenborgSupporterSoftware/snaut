package no.rbkss.snaut

import org.scalatest._

class LocationMgrSpec extends FlatSpec with Matchers {
    "LocationMgr" should "know location Lerkendal" in {
        LocationMgr.urlForLocation("Lerkendal") shouldBe Some("http://www.yr.no/place/Norway/S%C3%B8r-Tr%C3%B8ndelag/Trondheim/Lerkendal~211228/")
    }
    it should "not know of Myra stadion" in {
        LocationMgr.urlForLocation("Myra") shouldBe None
    }
}
