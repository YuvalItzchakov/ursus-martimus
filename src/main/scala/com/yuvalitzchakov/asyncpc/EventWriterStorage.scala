package com.yuvalitzchakov.asyncpc

import cats.effect.Sync
import cats.effect.concurrent.Ref
import cats.syntax.functor._

trait EventWriterStorage[F[_]] {
  def put(event: Event): F[Unit]
  def get: F[Vector[Event]]
}

object EventWriterStorage {
  def create[F[_]: Sync]: F[EventWriterStorage[F]] = {
    Ref.of[F, Vector[Event]](Vector.empty).map { ref =>
      new EventWriterStorage[F] {
        override def put(event: Event): F[Unit] = ref.update(_ :+ event)
        override def get: F[Vector[Event]] = ref.get
      }
    }
  }
}
