package com.yuvalitzchakov.asyncpc

import cats.effect.ConcurrentEffect
import io.circe.parser._

/**
  * Created by Yuval.Itzchakov on 22/07/2018.
  */
object EventStreamApp {
  def program[F[_]](
      dataGeneratorLocation: String,
      eventStorageConfig: EventStorageConfiguration,
      eventWriterStorage: EventWriterStorage[F],
      eventReaderStorage: EventReaderStorage[F])(
      implicit F: ConcurrentEffect[F]): fs2.Stream[F, Event] = {
    EventSource
      .stdinSource[F](dataGeneratorLocation)
      .map(decode[Event])
      .collect { case Right(event) => event }
      .observeAsync(eventStorageConfig.maxQueuedWriterElements)(
        EventSink.writeStorageSink(eventWriterStorage))
      .observeAsync(eventStorageConfig.maxQueuedReaderElements)(EventSink.readStorageSink(
        eventReaderStorage))
  }
}
