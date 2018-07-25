package com.yuvalitzchakov

/**
  * Created by Yuval.Itzchakov on 23/07/2018.
  */
package object asyncpc {
  type SourceResult[F[_]] = fs2.Stream[F, String]
}
