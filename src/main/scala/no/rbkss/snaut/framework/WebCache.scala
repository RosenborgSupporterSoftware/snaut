package no.rbkss.snaut.framework

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

import akka.actor.Actor
import akka.http.scaladsl.model.ContentType
import com.typesafe.scalalogging.LazyLogging
import no.rbkss.snaut.framework.WebCache.{Fetch, Item, NoSuchItem, Store}
import org.joda.time.{DateTime, Duration}

import scala.collection.mutable

// TODO: store to disk
// TODO: scan disk on bootup, load from disk on demand

class WebCache(cacheLoc: String) extends Actor with LazyLogging {
    private val cache = mutable.Map[String, Item]()

    override def preStart(): Unit = {
        logger.info(s"${WebCache.NAME} born")
    }

    override def postStop(): Unit = {
        logger.info(s"${WebCache.NAME} died")
    }

    def receive: Actor.Receive = {
        case f: Fetch =>
            var item = cache.getOrElse(f.path, NoSuchItem(f.path))
            if (f.compressed && item.isInstanceOf[Item] && item.asInstanceOf[Item].compressed.isEmpty) {
                val orig = item.asInstanceOf[Item]
                item = Item(orig.path, orig.mime, orig.uncompressed, Some(WebCache.compress(orig.uncompressed)),
                    orig.timestamp, orig.expires)
                cache.put(orig.path, item.asInstanceOf[Item])
            }
            sender ! item
        case item: Store =>
            cache.put(item.path, Item(item.path, item.mime, item.data, None, item.timestamp, item.expires))
        case x =>
            logger.warn(s"received unhandled message ${x}")
    }
}

object WebCache {
    val NAME = "WebCache"

    case class Fetch(path: String, compressed: Boolean)

    case class Store(path: String,
                     mime: ContentType,
                     data: Array[Byte],
                     timestamp: DateTime,
                     expires: DateTime)

    case class Item(path: String,
                    mime: ContentType,
                    uncompressed: Array[Byte],
                    compressed: Option[Array[Byte]],
                    timestamp: DateTime,
                    expires: DateTime) {

    }

    case class NoSuchItem(path: String)

    def compress(input: Array[Byte]): Array[Byte] = {
        val bos = new ByteArrayOutputStream(input.length)
        val gzip = new GZIPOutputStream(bos)
        gzip.write(input)
        gzip.close()
        val compressed = bos.toByteArray
        bos.close()
        compressed
    }
}
