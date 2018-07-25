package com.yuvalitzchakov.asyncpc

import cats.syntax.option._
import fs2.Sink
import fs2.async.mutable.Topic

/**
  * Created by Yuval.Itzchakov on 24/07/2018.
  */
trait EventSink[F[_]] {
  def sink(): Sink[F, Event]
}

object EventSink {
  def writeStorageSink[F[_]](eventStore: EventWriterStorage[F]): EventSink[F] =
    () => _.evalMap(event => eventStore.put(event))

  def topicEventSink[F[_]](topic: Topic[F, Option[Event]]): EventSink[F] =
    () => _.evalMap(event => topic.publish1(event.some))
}
