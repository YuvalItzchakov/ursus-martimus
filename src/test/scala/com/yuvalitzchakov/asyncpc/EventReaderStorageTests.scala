package com.yuvalitzchakov.asyncpc
import cats.effect.IO
import cats.implicits._

/**
  * Created by Yuval.Itzchakov on 26/07/2018.
  */
class EventReaderStorageTests extends UnitSpec {
  def fixture: EventReaderStorage[IO] = EventReaderStorage.create[IO].unsafeRunSync()

  "EventReaderStorage" must {
    "increment events by type on same event type" in {
      val eventReader = fixture

      val firstEvent = Event("foo", "bla", 1532620746L)
      val secondEvent = Event("foo", "wooho", 1532620746L)

      eventReader.put(firstEvent).unsafeRunSync()
      eventReader.put(secondEvent).unsafeRunSync()

      eventReader.getEventCountByType.unsafeRunSync().get(firstEvent.eventType) mustBe 2.some
    }

    "increment events by data on same event data" in {
      val eventReader = fixture

      val firstEvent = Event("baz", "wooho", 1532620746L)
      val secondEvent = Event("bar", "wooho", 1532620746L)

      eventReader.put(firstEvent).unsafeRunSync()
      eventReader.put(secondEvent).unsafeRunSync()

      eventReader.getEventCountByData.unsafeRunSync().get(firstEvent.data) mustBe 2.some
    }

    "use two separate entries when casing is different on event type" in {
      val eventReader = fixture

      val firstEvent = Event("BaZ", "wooho", 1532620746L)
      val secondEvent = Event("baz", "sfdasdfldsf", 1532620746L)

      eventReader.put(firstEvent).unsafeRunSync()
      eventReader.put(secondEvent).unsafeRunSync()

      eventReader.getEventCountByType.unsafeRunSync().get(firstEvent.eventType) mustBe 1.some
      eventReader.getEventCountByType.unsafeRunSync().get(secondEvent.eventType) mustBe 1.some
    }

    "use two separate entries when casing is different on event data" in {
      val eventReader = fixture

      val firstEvent = Event("BaZ", "WOOHO", 1532620746L)
      val secondEvent = Event("baz", "wooho", 1532620746L)

      eventReader.put(firstEvent).unsafeRunSync()
      eventReader.put(secondEvent).unsafeRunSync()

      eventReader.getEventCountByData.unsafeRunSync().get(firstEvent.data) mustBe 1.some
      eventReader.getEventCountByData.unsafeRunSync().get(secondEvent.data) mustBe 1.some
    }
  }
}
