package com.yuvalitzchakov.asyncpc

import cats.effect.ConcurrentEffect
import io.circe.parser._

/**
  * The event data pipeline
  */
object EventStreamApp {
  def apply[F[_] : ConcurrentEffect](
      dataGeneratorLocation: String,
      eventStorageConfig: EventStorageConfiguration,
      eventWriterStorage: EventWriterStorage[F],
      eventReaderStorage: EventReaderStorage[F]): fs2.Stream[F, Event] = {
    EventSource
      .standardInputSource[F](dataGeneratorLocation)
      .map(decode[Event])
      .collect { case Right(event) => event }
      .observeAsync(eventStorageConfig.maxQueuedWriterElements)(
        EventSink.writeStorageSink(eventWriterStorage))
      .observeAsync(eventStorageConfig.maxQueuedReaderElements)(EventSink.readStorageSink(
        eventReaderStorage))
  }
}
