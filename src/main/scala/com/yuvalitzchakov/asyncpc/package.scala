package com.yuvalitzchakov

package object asyncpc {

  /**
    * Description of a stream which when starts to emit produces json values of the specified contract.
    * (see the Event type or https://github.com/YuvalItzchakov/ursus-martimus for contract schema)
    * @tparam F The effect the stream runs in.
    */
  type SourceValue[F[_]] = fs2.Stream[F, String]
}
