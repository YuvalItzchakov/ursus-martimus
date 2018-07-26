package com.yuvalitzchakov.asyncpc

import cats.Functor
import cats.data.ReaderT
import cats.effect.concurrent.Ref
import cats.effect.{IO, Sync}

/**
  * Created by Yuval.Itzchakov on 25/07/2018.
  */
trait EventWriterStorage[F[_]] {
  def put(event: Event): F[Unit]
  def get: F[Vector[Event]]
}

object EventWriterStorage {
  def create[F[_]: Sync](implicit F: Functor[F]): F[EventWriterStorage[F]] = {
    F.map(Ref.of[F, Vector[Event]](Vector.empty)) { ref =>
      new EventWriterStorage[F] {
        override def put(event: Event): F[Unit] = ref.update(_ :+ event)
        override def get: F[Vector[Event]] = ref.get
      }
    }
  }

  implicit def readerEventWriterStorage: EventWriterStorage[ReaderT[IO, Ref[IO, Vector[Event]], ?]] =
    new EventWriterStorage[ReaderT[IO, Ref[IO, Vector[Event]], ?]] {
      override def put(event: Event): ReaderT[IO, Ref[IO, Vector[Event]], Unit] =
        ReaderT[IO, Ref[IO, Vector[Event]], Unit] { ref =>
          ref.update(_ :+ event)
        }

      override def get: ReaderT[IO, Ref[IO, Vector[Event]], Vector[Event]] = ReaderT { ref =>
        ref.get
      }
    }
}
