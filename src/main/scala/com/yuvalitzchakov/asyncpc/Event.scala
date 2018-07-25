package com.yuvalitzchakov.asyncpc

import java.time.{ZoneOffset, ZonedDateTime}

import io.circe.Decoder
import org.scalacheck.Gen

/**
  * Created by Yuval.Itzchakov on 23/07/2018.
  */
final case class Event(eventType: String, data: String, timestamp: Long)
object Event {
  implicit val eventDecoder: Decoder[Event] =
    Decoder.forProduct3("event_type", "data", "timestamp")(Event.apply)

  implicit val evenGen: Gen[Event] = for {
    eventType <- Gen.oneOf("foo", "bar", "baz")
    data <- Gen.alphaStr
    timestamp <- Gen.const(ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond)
  } yield Event(eventType, data, timestamp)
}
