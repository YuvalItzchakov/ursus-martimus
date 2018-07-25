package com.yuvalitzchakov.asyncpc

import cats.effect.{Concurrent, IO}
import cats.implicits._
import fs2.StreamApp
import fs2.StreamApp.ExitCode
import fs2.async.mutable.Topic
import io.circe.parser._
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.server.blaze.BlazeBuilder
import pureconfig._
import pureconfig.error.ConfigReaderFailures

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Yuval.Itzchakov on 22/07/2018.
  */
object StreamRunner extends StreamApp[IO] {

  val writeStorage: EventWriterStorage[IO] = EventWriterStorage.create[IO].unsafeRunSync()

  override def stream(
      args: List[String],
      requestShutdown: IO[Unit]): fs2.Stream[IO, StreamApp.ExitCode] = {

    val producerConsumerConfig: Either[ConfigReaderFailures, ProducerConsumerConfiguration] =
      loadConfig[ProducerConsumerConfiguration]("producer-consumer")

    val eventTopic = createOutputTopic[IO]()
//    val eventQueue =
//      fs2.Stream.eval(fs2.async.unboundedQueue[IO, Option[Event]])
//    val subscriber = eventTopic.flatMap(topic => topic.subscribe(10))
    val sink = EventSink.writeStorageSink[IO](writeStorage).sink()

    val res = producerConsumerConfig.map { config =>
      for {
        topic <- eventTopic
        stream <- EventSource
          .stdinSource[IO](config.dataGeneratorLocation)
          .produceEventLines()
          .map(decode[Event])
          .collect { case Right(r) => r }
          .map(event => { println(event); event })
          .observeAsync(100)(sink)
      } yield stream
    }

    val serv: HttpRoutes[IO] = httpService()

    val serverStream =
      BlazeBuilder[IO].bindHttp(8080, "0.0.0.0").mountService(serv).serve

    res match {
      case Left(configError) =>
        println(configError)
        fs2.Stream.emit(ExitCode.Error)
      case Right(resStream) =>
        (resStream concurrently serverStream).drain >> fs2.Stream.emit(ExitCode.Success)
    }
  }

  def createOutputTopic[F[_]: Concurrent](): fs2.Stream[F, Topic[F, Option[Event]]] = {
    fs2.Stream.eval(fs2.async.topic[F, Option[Event]](None))
  }

  def addPublisher[F[_]](topic: Topic[F, Option[Event]], event: Event): fs2.Stream[F, Unit] = {
    fs2.Stream.emit(event.some).covary[F].repeat.to(topic.publish)
  }

  def addSubscriber[F[_]](topic: Topic[F, Option[Event]]): fs2.Stream[F, Option[Event]] = {
    topic.subscribe(10)
  }

  def httpService(): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "eventcount" =>
      Ok(
        writeStorage.get.map(
          vec =>
            vec
              .map(event => s"Event Type: ${event.eventType}, Data: ${event.data}")
              .mkString("\n")))

    case GET -> Root / "groupedevents" => Ok("yes")
  }
}
