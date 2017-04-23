package no.rbkss.snaut.framework

import org.slf4j.{Logger, LoggerFactory}
import akka.actor.{ActorRef, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.Await
import collection.JavaConverters._

object Boot {
    val NAME = "Boot"
    val logger: Logger = LoggerFactory.getLogger(Boot.getClass)
    val system = ActorSystem("snaut")

    var webservice: ActorRef = _
    var webcache: ActorRef = _
    var scheduler: ActorRef = _

    def main(args: Array[String]): Unit = {
        logger.info("initializing")

        val config = ConfigFactory.load().resolve()
        val webPort = config.getInt("snaut.web.port")
        val enableGzip = config.getBoolean("snaut.web.gzip")
        val schedule = config.getStringList("snaut.schedule").asScala.toArray
        val cacheLoc = config.getString("snaut.paths.cache")

        webcache = system.actorOf(Props(classOf[WebCache], cacheLoc), WebCache.NAME)
        webservice = system.actorOf(Props(classOf[WebService], webcache, webPort, enableGzip), WebService.NAME)
        scheduler = system.actorOf(Props(classOf[Scheduler], schedule), Scheduler.NAME)
    }

    def shutdown(): Unit = {
        logger.debug("shutdown hook invoked")
        system.stop(scheduler)
        system.stop(webcache)
        system.stop(webservice)
        system.terminate()
        Await.result(system.whenTerminated, 10 seconds)
    }
}
