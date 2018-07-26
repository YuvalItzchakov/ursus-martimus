package com.yuvalitzchakov.asyncpc
import cats.effect.IO
import fs2.Sink
import io.circe.parser._
import io.circe.syntax._
import org.scalacheck.Gen

import scala.concurrent.ExecutionContext.Implicits._

/**
  * Created by Yuval.Itzchakov on 26/07/2018.
  */
class EventStreamGraphTests extends UnitSpec {
  def eventWriterMockSink: Sink[IO, Event] = _.map(_ => ())
  def eventReaderMockSink: Sink[IO, Event] = _.map(_ => ())

  "Event stream graph" must {
    "produce all events on output" in {
      forAll(
        Gen
          .listOf[Event](Event.evenGen)) { generatedEvents =>
        val events = fs2.Stream
          .emits(generatedEvents.map(_.asJson.noSpaces))
          .map(event => decode[Event](event))
          .collect { case Right(event) => event }
          .observe(eventWriterMockSink)
          .observe(eventReaderMockSink)
          .compile
          .toList
          .unsafeRunSync()

        events must contain theSameElementsAs generatedEvents
      }
    }
  }
}
