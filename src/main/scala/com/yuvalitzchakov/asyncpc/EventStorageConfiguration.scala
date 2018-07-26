package com.yuvalitzchakov.asyncpc

final case class EventStorageConfiguration(
    maxQueuedReaderElements: Int,
    maxQueuedWriterElements: Int)
