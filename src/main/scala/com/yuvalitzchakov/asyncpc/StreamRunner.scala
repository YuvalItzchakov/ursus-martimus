package com.yuvalitzchakov.asyncpc

import java.nio.file.{Files, Paths}

import cats.effect.IO
import cats.implicits._
import fs2.StreamApp
import fs2.StreamApp.ExitCode
import org.http4s.server.blaze.BlazeBuilder
import pureconfig._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Yuval.Itzchakov on 26/07/2018.
  */
object StreamRunner extends StreamApp[IO] {
  override def stream(
      args: List[String],
      requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {
    if (args.size != 1) {
      println(s"""
         |Invalid number of parameters to event stream runner. Please supply the data generator
         |location either via program options in IDEA or via a parameter in the command line.
         |Example usage: java -jar eventstorage <location of data generator>
       """.stripMargin)
      fs2.Stream.emit(ExitCode.Error)
    } else {
      val List(dataGeneratorPath) = args
      val doesGeneratorExist =
        Either.catchNonFatal(Files.exists(Paths.get(dataGeneratorPath))).getOrElse(false)

      if (!doesGeneratorExist) {
        println(
          s"Data generator is invalid. Please make sure the file exists at the specified location")
        fs2.Stream.emit(ExitCode.Error)
      } else bootstrapStream(dataGeneratorPath).unsafeRunSync().drain
    }
  }

  def bootstrapStream(dataGeneratorLocation: String): IO[fs2.Stream[IO, ExitCode]] = {
    val eventStorageConfig: EventStorageConfiguration =
      loadConfig[EventStorageConfiguration]("event-storage")
        .getOrElse(EventStorageConfiguration(2048, 2048))

    val eventWriterStorage = EventWriterStorage.create[IO]
    val eventReaderStorage = EventReaderStorage.create[IO]

    (eventWriterStorage, eventReaderStorage).tupled.map {
      case (eventWriter, eventReader) =>
        val eventStreamApp = new EventStreamApp[IO](
          dataGeneratorLocation,
          eventStorageConfig,
          eventWriter,
          eventReader)

        val eventsHttpService = new EventsHttpService[IO]
        val eventServer = eventsHttpService.httpService(eventReader)

        val serverStream =
          BlazeBuilder[IO].bindHttp(8080, "0.0.0.0").mountService(eventServer).serve

        (eventStreamApp.program concurrently serverStream) >> fs2.Stream.emit(ExitCode.Success)
    }
  }
}
