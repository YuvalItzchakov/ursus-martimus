package com.yuvalitzchakov.asyncpc

/**
 * Created by Yuval.Itzchakov on 26/07/2018.
 */
final case class EventState(eventsByType: Map[String, Int], eventsByData: Map[String, Int])
