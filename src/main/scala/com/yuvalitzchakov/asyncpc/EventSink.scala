package com.yuvalitzchakov.asyncpc

import fs2.Sink

object EventSink {

  /**
    * Creates a sink which puts events in the event writer storage,
    * keeping them in an append only log in raw structure.
    * @param eventWriterStore Storage to add events to
    * @tparam F The effect
    * @return A sink (function from stream => stream) which we can feed into the data pipeline
    */
  def writeStorageSink[F[_]](eventWriterStore: EventWriterStorage[F]): Sink[F, Event] =
    _.evalMap(event => eventWriterStore.put(event))

  /**
    * Creates a sink which puts events into the event reader storage for later
    * retrieval. Events are kept in an aggregated form.
    * @param eventReaderStorage Storage to add events to
    * @tparam F The effect
    * @return A sink (function from stream => stream) which we can feed into the data pipeline
    */
  def readStorageSink[F[_]](eventReaderStorage: EventReaderStorage[F]): Sink[F, Event] =
    _.evalMap(event => eventReaderStorage.put(event))
}
