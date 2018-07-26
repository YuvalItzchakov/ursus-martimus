package com.yuvalitzchakov.asyncpc

import cats.effect.Effect

import scala.sys.process.Process

/**
  * Created by Yuval.Itzchakov on 22/07/2018.
  */
object EventSource {
  def stdinSource[F[_]](generatorPath: String)(implicit E: Effect[F]): SourceResult[F] =
    fs2.Stream.fromIterator[F, String](Process(generatorPath).lineStream.iterator)
}
