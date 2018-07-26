package com.yuvalitzchakov.asyncpc

import io.circe.parser._
import io.circe.syntax._
import org.scalacheck.Gen

/**
  * Created by Yuval.Itzchakov on 26/07/2018.
  */
class EventSerdeTests extends UnitSpec {
  "Event deserializer" must {
    "deserialize valid event type" in {
      forAll((Event.evenGen, "event")) { expectedEvent =>
        val eventJson = expectedEvent.asJson.noSpaces
        decode[Event](eventJson) match {
          case Left(error) => fail(s"Deserialization yielded error: $error")
          case Right(actualEvent) => actualEvent mustEqual expectedEvent
        }
      }
    }

    "fail on invalid payload" in {
      forAll(
        Gen.oneOf(
          List("""{ \"���s�p{;ŵ""", """{"��/���ƛ�K""", """{ "-T�♣U��A$""", """{ "�\J�0�%�O�"""))) {
        invalidPayload =>
          decode[Event](invalidPayload) mustBe an[Left[Error, Event]]
      }
    }
  }
}
