package no.rbkss.snaut

import org.joda.time.DateTime

class CacheMgr(basedir : String) {
    var dir : Option[String] = Some(basedir)

    def isCached(location : String, time : DateTime) : Boolean = {
        return false
    }

    def expiresCached(location : String, time : DateTime) : Option[DateTime] = {
        if (!this.isCached(location, time)) return None
        return None // FIXME
    }

}
