package com.yuvalitzchakov.asyncpc
import cats.effect.IO
import org.scalacheck.Gen

/**
  * Created by Yuval.Itzchakov on 26/07/2018.
  */
class EventWriterStorageTests extends UnitSpec {
  def fixture: EventWriterStorage[IO] = EventWriterStorage.create[IO].unsafeRunSync()

  "EventWriterStorage" must {
    "append each event preserving write order" in {
      forAll(Gen.containerOf[Vector, Event](Event.evenGen).suchThat(_ != Vector.empty)) { events =>
        val eventWriter = fixture

        events.foreach(event => eventWriter.put(event).unsafeRunSync())
        val writtenEvents = eventWriter.get.unsafeRunSync()

        writtenEvents must contain theSameElementsInOrderAs events
      }
    }
  }
}
