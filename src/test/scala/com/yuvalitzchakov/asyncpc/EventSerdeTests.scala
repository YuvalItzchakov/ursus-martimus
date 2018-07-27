package com.yuvalitzchakov.asyncpc

import io.circe.Json
import io.circe.parser._
import io.circe.syntax._
import org.scalacheck.Gen

/**
  * Created by Yuval.Itzchakov on 26/07/2018.
  */
class EventSerdeTests extends UnitSpec {
  "Event deserializer" must {
    "deserialize valid event type" in {
      forAll(Gens.event) { expectedEvent =>
        val eventJson = expectedEvent.asJson.noSpaces
        decode[Event](eventJson) match {
          case Left(error) => fail(s"Deserialization yielded error: $error")
          case Right(actualEvent) => actualEvent mustEqual expectedEvent
        }
      }
    }

    "fail deserialization when eventType is null" in {
      forAll(Gens.event) { expectedEvent =>
        val eventJson =
          expectedEvent.asJson.hcursor
            .downField("event_type")
            .set(Json.Null)
            .top
            .getOrElse(Json.Null)
            .noSpaces

        decode[Event](eventJson) mustBe an[Left[Error, Event]]
      }
    }

    "fail deserialization when data is null" in {
      forAll(Gens.event) { expectedEvent =>
        val eventJson =
          expectedEvent.asJson.hcursor
            .downField("data")
            .set(Json.Null)
            .top
            .getOrElse(Json.Null)
            .noSpaces

        decode[Event](eventJson) mustBe an[Left[Error, Event]]
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
