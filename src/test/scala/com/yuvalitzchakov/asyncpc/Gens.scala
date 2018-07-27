package com.yuvalitzchakov.asyncpc
import java.time.{ZoneOffset, ZonedDateTime}

import org.scalacheck.Gen

/**
  * Object for grouping together gens
  */
object Gens {
  val event: Gen[Event] = for {
    eventType <- Gen.oneOf("foo", "bar", "baz")
    data <- Gen.alphaStr
    timestamp <- Gen.const(ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond)
  } yield Event(eventType, data, timestamp)
}
