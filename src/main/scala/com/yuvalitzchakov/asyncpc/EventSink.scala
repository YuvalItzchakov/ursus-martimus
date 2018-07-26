package com.yuvalitzchakov.asyncpc

import fs2.Sink

/**
  * Created by Yuval.Itzchakov on 24/07/2018.
  */
object EventSink {
  def writeStorageSink[F[_]](eventWriterStore: EventWriterStorage[F]): Sink[F, Event] =
    _.evalMap(event => eventWriterStore.put(event))

  def readStorageSink[F[_]](eventReaderStorage: EventReaderStorage[F]): Sink[F, Event] =
    _.evalMap(event => eventReaderStorage.put(event))
}
