/*
 * Copyright (c) 2015 Typelevel
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cats
package kernel
package compat

private[kernel] class HashCompat {
  // Adapted from scala.util.hashing.MurmurHash#productHash.
  private[kernel] def product1HashWithPrefix(_1Hash: Int, prefix: String): Int = {
    import scala.util.hashing.MurmurHash3._
    var h = productSeed
    h = mix(h, _1Hash)
    finalizeHash(h, 1)
  }

  // Adapted from scala.util.hashing.MurmurHash#productHash.
  private[cats] def product2HashWithPrefix(_1Hash: Int, _2Hash: Int, prefix: String): Int = {
    import scala.util.hashing.MurmurHash3._
    var h = productSeed
    h = mix(h, _1Hash)
    h = mix(h, _2Hash)
    finalizeHash(h, 2)
  }

  private[cats] def updateUnorderedHashC(c: Int, h: Int): Int = if (h != 0) c * h else c

  // adapted from [[scala.util.hashing.MurmurHash3]],
  // but modified standard `Any#hashCode` to `ev.hash`.
  def listHash[A](x: List[A])(implicit A: Hash[A]): Int = {
    import scala.util.hashing.MurmurHash3._
    var n = 0
    var h = seqSeed
    var elems = x
    while (!elems.isEmpty) {
      val head = elems.head
      val tail = elems.tail
      h = mix(h, A.hash(head))
      n += 1
      elems = tail
    }
    finalizeHash(h, n)
  }

  // adapted from scala.util.hashing.MurmurHash3
  def orderedHash[A](xs: TraversableOnce[A])(implicit A: Hash[A]): Int = {
    import scala.util.hashing.MurmurHash3._
    var n = 0
    var h = seqSeed
    xs.foreach { x =>
      h = mix(h, A.hash(x))
      n += 1
    }
    finalizeHash(h, n)
  }
}
