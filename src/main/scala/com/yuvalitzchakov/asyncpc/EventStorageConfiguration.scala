package com.yuvalitzchakov.asyncpc

/**
  * Created by Yuval.Itzchakov on 24/07/2018.
  */
final case class EventStorageConfiguration(
    maxQueuedReaderElements: Int,
    maxQueuedWriterElements: Int)
