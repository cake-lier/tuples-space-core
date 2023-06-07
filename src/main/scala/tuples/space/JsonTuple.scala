/*
 * Copyright (c) 2023 Matteo Castellucci
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

package io.github.cakelier
package tuples.space

import scala.annotation.{tailrec, targetName}
import scala.util.Try

sealed trait JsonTuple {

  @targetName("cons")
  infix def #:(h: JsonElement): JsonTuple
}

@SuppressWarnings(Array("org.wartremover.warts.Overloading"))
object JsonTuple {

  case object JsonNil extends JsonTuple {

    @targetName("cons")
    override infix def #:(h: JsonElement): JsonTuple = new #:(h, JsonNil)

    override def toString: String = "()"
  }

  @targetName("JsonNonEmptyTuple")
  final case class #:(h: JsonElement, t: JsonTuple) extends JsonTuple {

    @targetName("cons")
    override infix def #:(h: JsonElement): JsonTuple = new #:(h, this)

    override def toString: String = {
      @tailrec
      def _toString(t: JsonTuple, acc: String): String = t match {
        case x #: xs => _toString(xs, acc + ", " + String.valueOf(x))
        case _ => acc
      }

      "(" + String.valueOf(h) + _toString(t, acc = "") + ")"
    }
  }

  def apply(): JsonTuple = JsonNil

  def apply(v: JsonElement): JsonTuple = v #: JsonNil

  def apply(vs: JsonElement*): JsonTuple = fromSeq(vs)

  def fromSeq(vs: Seq[JsonElement]): JsonTuple = {
    @tailrec
    def _fromSeq(t: JsonTuple, s: Seq[JsonElement]): JsonTuple = s match {
      case x +: xs => _fromSeq(x #: t, xs)
      case _ => t
    }
    _fromSeq(JsonNil, vs.reverse)
  }
}

export JsonTuple.{JsonNil, #:}
