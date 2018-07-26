package com.yuvalitzchakov.asyncpc

import java.time.{ZoneOffset, ZonedDateTime}

import io.circe.{Decoder, Encoder}
import org.scalacheck.Gen

/**
  * Represents an event as generated by the data generator process
  * @param eventType Event type field
  * @param data Payload of the event
  * @param timestamp Timestamp of the generated event
  */
final case class Event(eventType: String, data: String, timestamp: Long)
object Event {
  implicit val eventDecoder: Decoder[Event] =
    Decoder.forProduct3("event_type", "data", "timestamp")(Event.apply)

  implicit val eventEncoder: Encoder[Event] =
    Encoder.forProduct3("event_type", "data", "timestamp")(event =>
      (event.eventType, event.data, event.timestamp))

  implicit val evenGen: Gen[Event] = for {
    eventType <- Gen.oneOf("foo", "bar", "baz")
    data <- Gen.alphaStr
    timestamp <- Gen.const(ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond)
  } yield Event(eventType, data, timestamp)
}
