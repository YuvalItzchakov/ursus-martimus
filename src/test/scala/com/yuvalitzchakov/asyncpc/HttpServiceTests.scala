package com.yuvalitzchakov.asyncpc

import cats.Eq
import cats.effect.IO
import cats.implicits._
import org.http4s.dsl.io._
import org.http4s._

import scala.concurrent.ExecutionContext.Implicits._

/**
  * Created by Yuval.Itzchakov on 28/07/2018.
  */
class HttpServiceTests extends UnitSpec {
  private val mockEventReader: EventReaderStorage[IO] = new EventReaderStorage[IO] {
    val data: IO[Map[String, Int]] = IO.pure(Map("foo" -> 1, "bar" -> 2))

    override def put(event: Event): IO[Unit] = IO.unit
    override def getEventCountByType: IO[Map[String, Int]] = data
    override def getEventCountByData: IO[Map[String, Int]] = data
  }

  private val service: HttpRoutes[IO] = new EventsHttpService[IO].httpService(mockEventReader)

  "Http Service" must {
    "return 200 on eventsbytype path" in {
      val response = service.orNotFound
        .run(
          Request(method = Method.GET, uri = Uri.uri("/eventsbytype"))
        )

      check[String](
        response,
        Status.Ok,
        mockEventReader.getEventCountByType
          .unsafeRunSync()
          .map {
            case (event, count) => s"Event type: $event, Count: $count"
          }
          .mkString("\n")
          .some
      )
    }

    "return 200 on eventsbydata path" in {
      val response = service.orNotFound
        .run(
          Request(method = Method.GET, uri = Uri.uri("/eventsbydata"))
        )

      check[String](
        response,
        Status.Ok,
        mockEventReader.getEventCountByData
          .unsafeRunSync()
          .map {
            case (event, count) => s"Event data: $event, Count: $count"
          }
          .mkString("\n")
          .some
      )
    }
  }

  private def check[A](actual: IO[Response[IO]], expectedStatus: Status, expectedBody: Option[A])(
      implicit D: EntityDecoder[IO, A],
      E: Eq[A]
  ): Boolean = {
    val actualResp = actual.unsafeRunSync
    val statusCheck = actualResp.status == expectedStatus
    val bodyCheck =
      expectedBody.fold[Boolean](actualResp.body.compile.toVector.unsafeRunSync.isEmpty)(expected =>
        E.eqv(actualResp.as[A].unsafeRunSync, expected))
    statusCheck && bodyCheck
  }
}
