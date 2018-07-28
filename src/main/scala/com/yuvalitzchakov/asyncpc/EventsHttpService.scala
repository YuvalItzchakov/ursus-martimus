package com.yuvalitzchakov.asyncpc

import cats.Monad
import cats.effect.ConcurrentEffect
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.syntax.functor._

/**
  * HTTP endpoint for supplying the aggregated events
  */
class EventsHttpService[F[_] : Monad : ConcurrentEffect] extends Http4sDsl[F] {
  def httpService(readerStorage: EventReaderStorage[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root => Ok("Welcome to the event counter service!")
      case GET -> Root / "eventsbytype" =>
        Ok {
          readerStorage.getEventCountByType.map { eventByType =>
            eventByType
              .map { case (eventType, count) => s"Event type: $eventType, Count: $count" }
              .mkString("\n")
          }
        }

      case GET -> Root / "eventsbydata" =>
        Ok {
          readerStorage.getEventCountByData.map { eventsByData =>
            eventsByData
              .map { case (eventData, count) => s"Event data: $eventData, Count: $count" }
              .mkString("\n")
          }
        }
    }
}
