package com.yuvalitzchakov.asyncpc

import java.nio.file.{Files, Paths}

import cats.effect.IO
import cats.implicits._
import fs2.StreamApp
import fs2.StreamApp.ExitCode
import org.http4s.server.blaze.BlazeBuilder
import pureconfig._

import scala.concurrent.ExecutionContext.Implicits.global

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

  /**
    * Bootstraps all necessary dependencies for running the event stream and the HTTP endpoint stream
    * @param dataGeneratorLocation Location of the data generator executable
    * @return A description of the stream to execute.
    */
  def bootstrapStream(dataGeneratorLocation: String): IO[fs2.Stream[IO, ExitCode]] = {
    val eventStorageConfig: EventStorageConfiguration =
      loadConfig[EventStorageConfiguration]("event-storage")
        .getOrElse(EventStorageConfiguration(2048, 2048))

    val httpServerConfig =
      loadConfig[HttpServerConfiguration].getOrElse(HttpServerConfiguration("0.0.0.0", 8080))

    val eventWriterStorage = EventWriterStorage.create[IO]
    val eventReaderStorage = EventReaderStorage.create[IO]

    (eventWriterStorage, eventReaderStorage).tupled.map {
      case (eventWriter, eventReader) =>
        val eventStreamApp =
          EventStreamApp[IO](dataGeneratorLocation, eventStorageConfig, eventWriter, eventReader)

        val eventsHttpService = new EventsHttpService[IO].httpService(eventReader)

        val serverStream =
          BlazeBuilder[IO]
            .bindHttp(httpServerConfig.port, httpServerConfig.ip)
            .mountService(eventsHttpService)
            .serve

        (eventStreamApp concurrently serverStream) >> fs2.Stream.emit(ExitCode.Success)
    }
  }
}
