package com.yuvalitzchakov.asyncpc

import cats.effect.{Async, Effect}

import scala.sys.process.Process

/**
  * Created by Yuval.Itzchakov on 22/07/2018.
  */
trait EventSource[F[_]] {
  implicit val async: Async[F]

  def produceEventLines(): SourceResult[F]
}

object EventSource {
  def stdinSource[F[_]](generatorPath: String)(
      implicit A: Async[F],
      E: Effect[F]): EventSource[F] =
    new EventSource[F] {
      override implicit val async: Async[F] = A

      override def produceEventLines(): SourceResult[F] = {
        val res = fs2.Stream.fromIterator[F, String](
          Process(generatorPath).lineStream.iterator)

        res
      }
    }
}
