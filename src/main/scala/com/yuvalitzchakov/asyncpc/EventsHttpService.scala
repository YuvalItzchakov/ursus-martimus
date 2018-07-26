package com.yuvalitzchakov.asyncpc

import cats.Monad
import cats.effect.ConcurrentEffect
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

/**
  * HTTP endpoint for supplying the aggregated events
  */
class EventsHttpService[F[_]](implicit M: Monad[F], C: ConcurrentEffect[F]) extends Http4sDsl[F] {
  def httpService(readerStorage: EventReaderStorage[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "eventsbytype" =>
        Ok {
          M.map(readerStorage.getEventCountByType) { eventByType =>
            eventByType
              .map { case (eventType, count) => s"Event type: $eventType, Count: $count" }
              .mkString("\n")
          }
        }

      case GET -> Root / "eventsbydata" =>
        Ok {
          M.map(readerStorage.getEventCountByData) { eventByType =>
            eventByType
              .map { case (eventData, count) => s"Event type: $eventData, Count: $count" }
              .mkString("\n")
          }
        }
    }
}
