package com.yuvalitzchakov.asyncpc
import cats.Monad
import cats.effect.ConcurrentEffect
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

/**
  * Created by Yuval.Itzchakov on 26/07/2018.
  */
class EventsHttpService[F[_]](implicit M: Monad[F], C: ConcurrentEffect[F]) extends Http4sDsl[F] {
  def httpService(readerStorage: EventReaderStorage[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "eventcount" =>
        Ok {
          M.map(readerStorage.getEventCountByType) { eventByType =>
            eventByType
              .map { case (eventType, count) => s"Event type: $eventType, Count: $count" }
              .mkString("\n")
          }
        }

      case GET -> Root / "groupedevents" =>
        Ok {
          M.map(readerStorage.getEventCountByData) { eventByType =>
            eventByType
              .map { case (eventData, count) => s"Event type: $eventData, Count: $count" }
              .mkString("\n")
          }
        }
    }
}
