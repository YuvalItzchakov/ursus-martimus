package com.yuvalitzchakov.asyncpc
import cats.Functor
import cats.effect.Sync
import cats.effect.concurrent.Ref

/**
  * Created by Yuval.Itzchakov on 26/07/2018.
  */
trait EventReaderStorage[F[_]] {
  def put(event: Event): F[Unit]
  def getEventCountByType: F[Map[String, Int]]
  def getEventCountByData: F[Map[String, Int]]
}

object EventReaderStorage {
  def create[F[_]: Sync](implicit F: Functor[F]): F[EventReaderStorage[F]] = {
    F.map(Ref.of[F, EventState](EventState(Map.empty, Map.empty))) { ref =>
      new EventReaderStorage[F] {
        override def put(event: Event): F[Unit] = ref.update { eventState =>
          val eventsByData = eventState.eventsByData
          val eventsByType = eventState.eventsByType

          val updatedEventByData = eventsByData
            .get(event.data)
            .fold(eventsByData.updated(event.data, 1))(currentCount =>
              eventsByData.updated(event.data, currentCount + 1))

          val updatedEventByType = eventsByType
            .get(event.eventType)
            .fold(eventsByType.updated(event.eventType, 1))(currentCount =>
              eventsByType.updated(event.eventType, currentCount + 1))

          EventState(updatedEventByType, updatedEventByData)
        }

        override def getEventCountByData: F[Map[String, Int]] = {
          F.map(ref.get)(_.eventsByData)
        }
        override def getEventCountByType: F[Map[String, Int]] = {
          F.map(ref.get)(_.eventsByType)
        }
      }
    }
  }
}
