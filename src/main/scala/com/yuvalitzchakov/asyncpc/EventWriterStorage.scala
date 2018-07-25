package com.yuvalitzchakov.asyncpc

import cats.Functor
import cats.effect.Concurrent
import cats.effect.concurrent.Ref

/**
  * Created by Yuval.Itzchakov on 25/07/2018.
  */
trait EventWriterStorage[F[_]] {
  def put(event: Event): F[Unit]
  def get: F[Vector[Event]]
}

object EventWriterStorage {
  def create[F[_]: Concurrent](implicit F: Functor[F]): F[EventWriterStorage[F]] = {
    F.map(Ref.of[F, Vector[Event]](Vector.empty)) { ref =>
      new EventWriterStorage[F] {
        override def put(event: Event): F[Unit] = ref.update(_ :+ event)
        override def get: F[Vector[Event]] = ref.get
      }
    }
  }
}