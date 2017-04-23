package no.rbkss.snaut

import akka.http.scaladsl.model.HttpCharsets.`UTF-8`
import akka.http.scaladsl.model.{ContentType, MediaType}

package object framework {
    object CustomTypes {
        val `image/svg+xml(UTF-8)`: ContentType = MediaType.customWithFixedCharset("image", "svg+xml", `UTF-8`)
    }
}
