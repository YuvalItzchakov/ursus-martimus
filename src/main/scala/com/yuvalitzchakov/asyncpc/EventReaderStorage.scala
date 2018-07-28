package com.yuvalitzchakov.asyncpc
import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.syntax.functor._

/**
  * Reader storage is an abstraction over reading aggregated data over
  * the events received from the data generation source.
  * @tparam F The underlying effect
  */
trait EventReaderStorage[F[_]] {

  /**
    * Adds an event value to the underlying storage
    * @param event The event to add
    */
  def put(event: Event): F[Unit]

  /**
    * Retrieves a key value pair where the key is the event type and
    * the value is the count of occurrences of the event type thus far.
    * @return Event type to count mapping
    */
  def getEventCountByType: F[Map[String, Int]]

  /**
    * Retrieves a key value pair where the key is the event data and
    * the value is the count of occurrences of the event type thus far.
    * @return Event type to count mapping
    */
  def getEventCountByData: F[Map[String, Int]]
}

object EventReaderStorage {
  def create[F[_]: Sync]: F[EventReaderStorage[F]] = {
    Ref.of[F, EventState](EventState(Map.empty, Map.empty)).map { ref =>
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
          ref.get.map(_.eventsByData)
        }

        override def getEventCountByType: F[Map[String, Int]] = {
          ref.get.map(_.eventsByType)
        }
      }
    }
  }
}
