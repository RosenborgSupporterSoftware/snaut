package no.rbkss.snaut

import org.scalatest._

class ImageSpec extends FlatSpec with Matchers {
    "Image" should "construct" in {
        var img = new Image()
    }

    it should "set symbols" in {
        var img = new Image()

        img.addCloudSymbol("09")
        img.addTemperature(8)
        img.addLocationHeader("Lerkendal", "24.5.2017 18:00-20:00")
        img.addWind(0, 1)
        img.addText("Klarvær. Flau vind, 1 m/s fra sørøst. 0 mm nedbør.")
        img.addUpdateTime("11:30")
        img.export("/tmp/exported1.svg")

        img = new Image()
        img.addCloudSymbol("mf/05n.91")
        img.addLocationHeader("Lerkendal", "24.5.2017 18:00-20:00")
        img.addTemperature(12)
        img.addYrLogo()
        img.addWind(1, 1)
        img.addText("Klarvær. Flau vind, 1 m/s fra sørøst. 0 mm nedbør.")
        img.addUpdateTime("11:30")
        img.export("/tmp/exported2.svg")

        img = new Image()
        img.addCloudSymbol("03d")
        img.addLocationHeader("Lerkendal", "24.5.2017 18:00-20:00")
        img.addTemperature(-2)
        img.addWind(4, 120)
        img.addYrLogo()
        img.addText("Klarvær. Flau vind, 1 m/s fra sørøst. 0 mm nedbør.")
        img.addUpdateTime("11:30")
        img.export("/tmp/exported3.svg")
    }

    it should "set complete forecast" is pending
}
