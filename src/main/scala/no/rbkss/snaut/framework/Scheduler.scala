package no.rbkss.snaut.framework

import java.time.temporal.ChronoUnit
import java.time._
import java.util.concurrent.TimeUnit

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import cron4s.Cron
import cron4s.expr.CronExpr
import cron4s.lib.javatime._
import no.rbkss.snaut.framework.Scheduler.ScheduleTrigger

import scala.language.postfixOps
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

class Scheduler(schedules: Array[String]) extends Actor with LazyLogging {
    var cron: Array[CronExpr] = Array[CronExpr]()

    override def preStart(): Unit = {
        logger.info(s"${Scheduler.NAME} born")
        try {
            if (schedules != null)
                cron = schedules.map[CronExpr, Array[CronExpr]]{ Cron(_).right.get }
        } catch {
            case x: Throwable =>
                logger.warn("cron parsing: " + x.getMessage)
                context.system.scheduler.scheduleOnce(300 milliseconds)({ Boot.shutdown() })
        }
        scheduleNext()
    }

    override def postStop(): Unit = {
        logger.info(s"${Scheduler.NAME} died")
    }

    def receive: Actor.Receive = {
        case ScheduleTrigger =>
            logger.warn("triggered")
            scheduleNext()
        case x =>
            logger.warn(s"received unhandled msg ${x}")
    }

    def scheduleNext(): Unit = {
        val now = LocalDateTime.now
        val nextTimes: Array[LocalDateTime] = this.cron.map({ _.next(now).get })
        val nextTime = nextTimes.toStream.min(Ordering[Long].on[LocalDateTime](x => {
            now.until(x, ChronoUnit.MILLIS)
        }))

        context.system.scheduler.scheduleOnce(FiniteDuration(now.until(nextTime, ChronoUnit.MILLIS), TimeUnit.MILLISECONDS),
            self, ScheduleTrigger)
    }
}

object Scheduler {
    val NAME = "Scheduler"

    case object ScheduleTrigger
}
