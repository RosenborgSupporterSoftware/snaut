package no.rbkss.snaut.framework

import java.io.ByteArrayOutputStream
import java.nio.file.{Files, Paths}
import java.util.zip.GZIPOutputStream

import akka.actor.{Actor, ActorRef}
import akka.http.scaladsl.{Http, server}
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Route, RouteResult}
import akka.http.scaladsl.settings.{ParserSettings, RoutingSettings}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import akka.util.{ByteString, Timeout}
import com.typesafe.scalalogging.LazyLogging
import no.rbkss.snaut.framework.WebCache.NoSuchItem

import scala.concurrent.duration._
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success}


class WebService(cache: ActorRef, port: Int, gzipEnabled: Boolean) extends Actor with LazyLogging {
    var bindingFuture: Future[ServerBinding] = _
    var actorMaterializer: ActorMaterializer = _

    var isUp: Boolean = false

    override def preStart: Unit = {
        logger.info(s"${WebService.NAME} born")
        setUp()
    }

    override def postStop: Unit = {
        takeDown()
        logger.info(s"${WebService.NAME} died")
    }

    def setUp(): Unit = {
        if (!isUp) {
            implicit val system = context.system

            actorMaterializer = ActorMaterializer()(context = context)
            implicit val materializer = actorMaterializer

            // needed for the future flatMap/onComplete in the end
            implicit val executionContext = system.dispatcher
            implicit val timeout = Timeout(5 seconds)

            val route: Route =
                get {
                    extractRequest { req: HttpRequest =>
                        def respond(typ: ContentType, data: Array[Byte], gzip: Boolean): server.Route = {
                            //logger.info("responding to request")
                            if (gzip) {
                                respondWithHeader(RawHeader("Content-Encoding", "gzip")) {
                                    complete(HttpEntity(typ, ByteString(WebService.compress(data))))
                                }
                            }
                            else {
                                complete(HttpEntity(typ, ByteString(data)))
                            }
                        }

                        def isGzipAllowed(req: HttpRequest): Boolean = {
                            req.headers
                              .find({ h: HttpHeader => h.lowercaseName == "accept-encoding" })
                              .map({ h: HttpHeader => h.value.indexOf("gzip") != -1 })
                            match {
                                case Some(true) => gzipEnabled
                                case _ => false
                            }
                        }

                        pathSingleSlash {
                            val data = WebService.loadIndex()
                            respond(ContentTypes.`text/html(UTF-8)`, data, isGzipAllowed(req))
                        } ~
                        path("index.html") {
                            val data = WebService.loadIndex()
                            respond(ContentTypes.`text/html(UTF-8)`, data, isGzipAllowed(req))
                        } ~
                        path("admin" / "stop") {
                            context.system.scheduler.scheduleOnce(100 milliseconds)({ Boot.shutdown() })
                            complete(StatusCodes.NoContent)
                        } ~
                        path("snaut" / RemainingPath) { p =>
                            extractMatchedPath { matched =>
                                val elts = matched.toString().split("/")
                                if (elts.size != 5 || elts(4) != "weather.svg")
                                    complete(StatusCodes.Forbidden, Array[Byte]())
                                else {
                                    val place = elts(2)
                                    val date = elts(3)
                                    val path = place + "/" + date + "/" + "weather.svg"
                                    val gzip = isGzipAllowed(req)
                                    val item: Future[Any] = cache ? WebCache.Fetch(path, gzip)
                                    onComplete(item) {
                                        case Success(NoSuchItem(path)) =>
                                            logger.debug(s"no item ${path}")
                                            reject
                                        case Success(x) =>
                                            val item = x.asInstanceOf[WebCache.Item]
                                            var headers: List[RawHeader] = List()
                                            var payload: Array[Byte] = item.uncompressed
                                            // FIXME: cache-expiration headers
                                            if (gzip && item.compressed.isDefined) {
                                                headers = RawHeader("Content-Encoding", "gzip") :: headers
                                                payload = item.compressed.get
                                            }
                                            respondWithHeaders(headers) {
                                                complete(HttpEntity(item.mime, payload))
                                            }
                                        case Failure(e) =>
                                            logger.warn(s"error accessing cache: ${e}")
                                            reject
                                    }
                                }
                            }
                        }
                    }
                }

            implicit val routingSettings = RoutingSettings(system)
            implicit val parserSettings = ParserSettings(system)
            val routeFlow = RouteResult.route2HandlerFlow(route)

            bindingFuture = Http().bindAndHandle(routeFlow, "::1", port)
            bindingFuture onComplete {
                case Success(x) =>
                    logger.info(s"Web service is online at http://localhost:$port/")
                    isUp = true
                case Failure(e) =>
                    logger.error(s"bind failed - exiting: $e")
                    Boot.shutdown()
            }
        }
    }

    def takeDown(): Unit = {
        if (isUp) {
            implicit val materializer = actorMaterializer
            implicit val executionContext = context.system.dispatcher
            bindingFuture.flatMap(_.unbind())
            logger.info("stopped web service")
            isUp = false
        }
    }

    def receive: Actor.Receive = {
        case x => logger.warn(s"unknown message ${x.getClass}")
    }

}

object WebService {
    val NAME = "WebService"

    def loadIndex(): Array[Byte] = {
        val url = WebService.getClass.getResource("/index.html")
        Files.readAllBytes(Paths.get(url.getPath))
    }

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
