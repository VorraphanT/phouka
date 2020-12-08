package com.selfdualbrain.data_structures

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
  * Super-fast "map-like" collection that internally uses ArrayBuffer.
  * Conceptually this is a replacement for mutable.Map[Int,E].
  * This version of map enforces that keys always form a full interval <0,n>.
  * Keys cannot be removed.
  *
  * @param initialSize initial size
  * @tparam E type of map values
  */
class FastIntMapWithAutoinit[E](initialSize: Int)(valuesInitializer: Int => E) extends mutable.Map[Int,E] {
  private val storage = new ArrayBuffer[E](initialSize)

  override def subtractOne(key: Int): this.type = {
    throw new RuntimeException("removing keys is not supported in this version of fast-int-map")
  }

  override def addOne(elem: (Int, E)): this.type = {
    val (key,value) = elem
    assert(key >= 0)
    if (key <= lastIndexInUse)
      storage(key) = value
    else {
      for (i <- lastIndexInUse + 1 until key)
        storage.addOne(valuesInitializer(i))
      storage.addOne(value)
    }
    return this
  }

  override def get(key: Int): Option[E] =
    if (key > lastIndexInUse)
      None
    else
      Some(storage(key))

  override def iterator: Iterator[(Int, E)] = storage.iterator.zipWithIndex collect {case (e,n) => (n,e)}

  override def valuesIterator: Iterator[E] = storage.iterator

  private def lastIndexInUse: Int = storage.size - 1

  override def values: Iterable[E] = storage

  override def size: Int = storage.size

  override def isEmpty: Boolean = size == 0

  def lastKey: Option[Int] = if (isEmpty) None else Some(lastIndexInUse)

  def underlyingArrayBuffer: ArrayBuffer[E] = storage
}

class FastMapOnIntInterval[E](initialSize: Int) extends FastIntMapWithAutoinit[E](initialSize)((i: Int) => throw new RuntimeException(s"only adding first key missing is supported; attempted key $i"))

