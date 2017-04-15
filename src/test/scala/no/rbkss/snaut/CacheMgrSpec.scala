package no.rbkss.snaut

import org.joda.time.DateTime
import org.scalatest._

class CacheMgrSpec extends FlatSpec with Matchers {

    "CacheMgr" should "take basedir constructor argument" in {
        var mgr = new CacheMgr("/tmp")
    }

    it should "not have cached test entry" in {
        var mgr = new CacheMgr("/tmp")
        mgr.isCached("lerkendal", new DateTime("2016-08-10T16:00")) shouldBe false
    }
}
