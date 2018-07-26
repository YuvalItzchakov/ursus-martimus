package com.yuvalitzchakov.asyncpc

/**
  * Holds events in accumulated state
  * @param eventsByType A grouping of events by their type field
  * @param eventsByData A grouping of events by their data field
  */
final case class EventState(eventsByType: Map[String, Int], eventsByData: Map[String, Int])
