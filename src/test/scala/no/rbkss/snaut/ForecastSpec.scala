package no.rbkss.snaut

import org.joda.time.DateTime
import org.scalatest._

class ForecastSpec extends FlatSpec with Matchers {
    "Forecast" should "load forecasts from file" in {
        var f = new Forecast().withURL(getClass.getResource("/lerkendal_varsel.xml"))
        var g = new Forecast().withURL(getClass.getResource("/lerkendal_varsel_time_for_time.xml"))
    }
    it should "not find date 2015-08-11 14:00 in the test-file" in {
        var f = new Forecast().withURL(getClass.getResource("/lerkendal_varsel.xml"))
        f.containsDate(DateTime.parse("2015-08-11T14:00")) shouldBe false
    }
    it should "find date 2016-08-11 14:00 in the test-file" in {
        var f = new Forecast().withURL(getClass.getResource("/lerkendal_varsel.xml"))
        f.containsDate(DateTime.parse("2016-08-11T14:00")) shouldBe true
    }
    it should "be 10 Celsius at 2016-08-11 14:00 in the test-file" in {
        var f = new Forecast().withURL(getClass.getResource("/lerkendal_varsel.xml"))
        f.getSnapshot(DateTime.parse("2016-08-11T14:00")).get.temperature.value shouldBe 10.0
    }
}
