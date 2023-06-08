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

import scala.annotation.tailrec
import scala.annotation.targetName

private trait SequentialOps[-T, E, G] {

  def arity(t: T): Int

  @targetName("append")
  infix def :#(t: T, v: E): G

  @targetName("concat")
  def :##(t1: T, t2: G): G

  def drop(t: T, n: Int): G

  def elem(t: T, n: Int): Option[E]

  def filter(t: T, p: E => Boolean): G

  def map(t: T, f: E => E): G

  def flatMap(t: T, f: E => G): G

  def foldLeft[A](t: T, z: A, a: (A, E) => A): A

  def foldRight[A](t: T, z: A, a: (E, A) => A): A

  def head(t: T): Option[E]

  def tail(t: T): G

  def init(t: T): G

  def last(t: T): Option[E]

  def split(t: T, n: Int): G

  def take(t: T, n: Int): G

  def zip(t1: T, t2: G): G

  def toSeq(t: T): Seq[E]

  def foreach(t: T, f: JsonElement => Unit): Unit
}

private object SequentialOps {

  extension [T, E, G](t1: T)(using SequentialOps[T, E, G]) {

    def arity: Int = implicitly[SequentialOps[T, E, G]].arity(t1)

    @targetName("append")
    infix def :#(v: E): G = implicitly[SequentialOps[T, E, G]].:#(t1, v)

    @targetName("concat")
    def :##(t: G): G = implicitly[SequentialOps[T, E, G]].:##(t1, t)

    def drop(n: Int): G = implicitly[SequentialOps[T, E, G]].drop(t1, n)

    def elem(n: Int): Option[E] = implicitly[SequentialOps[T, E, G]].elem(t1, n)

    def filter(p: E => Boolean): G = implicitly[SequentialOps[T, E, G]].filter(t1, p)

    def map(f: E => E): G = implicitly[SequentialOps[T, E, G]].map(t1, f)

    def flatMap(f: E => G): G = implicitly[SequentialOps[T, E, G]].flatMap(t1, f)

    def foldLeft[A](z: A)(a: (A, E) => A): A = implicitly[SequentialOps[T, E, G]].foldLeft(t1, z, a)

    def foldRight[A](z: A)(a: (E, A) => A): A = implicitly[SequentialOps[T, E, G]].foldRight(t1, z, a)

    def head: Option[E] = implicitly[SequentialOps[T, E, G]].head(t1)

    def tail: G = implicitly[SequentialOps[T, E, G]].tail(t1)

    def init: G = implicitly[SequentialOps[T, E, G]].init(t1)

    def last: Option[E] = implicitly[SequentialOps[T, E, G]].last(t1)

    def split(n: Int): G = implicitly[SequentialOps[T, E, G]].split(t1, n)

    def take(n: Int): G = implicitly[SequentialOps[T, E, G]].take(t1, n)

    def zip(t: G): G = implicitly[SequentialOps[T, E, G]].zip(t1, t)

    def toSeq: Seq[E] = implicitly[SequentialOps[T, E, G]].toSeq(t1)

    def foreach(f: JsonElement => Unit): Unit = implicitly[SequentialOps[T, E, G]].foreach(t1, f)
  }

  given [T <: JsonTuple]: SequentialOps[T, JsonElement, JsonTuple] with {

    override def arity(t: T): Int = {
      @tailrec
      def _arity(r: JsonTuple, acc: Int): Int = r match {
        case _ #: xs => _arity(xs, acc + 1)
        case _ => acc
      }

      _arity(t, acc = 0)
    }

    @targetName("append")
    override infix def :#(t: T, v: JsonElement): JsonTuple = {
      @tailrec
      def _append(r: JsonTuple, acc: JsonTuple => JsonTuple): JsonTuple = r match {
        case x #: xs => _append(xs, e => acc(x #: e))
        case _ => acc(v #: JsonNil)
      }

      _append(t, identity[JsonTuple])
    }

    private def reifiedConcat(t1: JsonTuple, t2: JsonTuple): JsonTuple = {
      @tailrec
      def _concat(r: JsonTuple, acc: JsonTuple => JsonTuple): JsonTuple = r match {
        case x #: xs => _concat(xs, e => acc(x #: e))
        case _ => acc(t2)
      }

      _concat(t1, identity[JsonTuple])
    }

    @targetName("concat")
    override infix def :##(t1: T, t2: JsonTuple): JsonTuple = reifiedConcat(t1, t2)

    override def drop(t: T, n: Int): JsonTuple = {
      @tailrec
      def _drop(r: JsonTuple, c: Int): JsonTuple = c match {
        case 0 => r
        case _ =>
          r match {
            case _ #: xs => _drop(xs, c - 1)
            case _ => JsonNil
          }
      }

      if (n >= 0) _drop(t, n) else t
    }

    override def elem(t: T, n: Int): Option[JsonElement] = {
      @tailrec
      def _elem(r: JsonTuple, c: Int): Option[JsonElement] = r match {
        case x #: xs =>
          c match {
            case 0 => Some(x)
            case _ => _elem(xs, c - 1)
          }
        case _ => None
      }

      if (n >= 0) _elem(t, n) else None
    }

    override def filter(t: T, p: JsonElement => Boolean): JsonTuple = {
      @tailrec
      def _filter(r: JsonTuple, acc: JsonTuple => JsonTuple): JsonTuple = r match {
        case x #: xs if p(x) => _filter(xs, e => acc(x #: e))
        case _ #: xs => _filter(xs, acc)
        case _ => acc(JsonNil)
      }

      _filter(t, identity[JsonTuple])
    }

    override def map(t: T, f: JsonElement => JsonElement): JsonTuple = {
      @tailrec
      def _map(r: JsonTuple, acc: JsonTuple => JsonTuple): JsonTuple = r match {
        case x #: xs => _map(xs, e => acc(f(x) #: e))
        case _ => acc(JsonNil)
      }

      _map(t, identity[JsonTuple])
    }

    override def flatMap(t: T, f: JsonElement => JsonTuple): JsonTuple = {
      @tailrec
      def _flatMap(r: JsonTuple, acc: JsonTuple => JsonTuple): JsonTuple = r match {
        case x #: xs => _flatMap(xs, e => acc(reifiedConcat(f(x), e)))
        case _ => acc(JsonNil)
      }

      _flatMap(t, identity[JsonTuple])
    }

    override def foldLeft[A](t: T, z: A, a: (A, JsonElement) => A): A = {
      @tailrec
      def _foldLeft(r: JsonTuple, acc: A): A = r match {
        case x #: xs => _foldLeft(xs, a(acc, x))
        case _ => acc
      }

      _foldLeft(t, z)
    }

    override def foldRight[A](t: T, z: A, a: (JsonElement, A) => A): A = {
      @tailrec
      def _foldRight(r: JsonTuple, acc: A => A): A = r match {
        case x #: xs => _foldRight(xs, e => acc(a(x, e)))
        case _ => acc(z)
      }

      _foldRight(t, identity[A])
    }

    override def head(t: T): Option[JsonElement] = t match {
      case x #: _ => Some(x)
      case _ => None
    }

    override def tail(t: T): JsonTuple = t match {
      case _ #: xs => xs
      case _ => JsonNil
    }

    override def init(t: T): JsonTuple = {
      @tailrec
      def _init(r: JsonTuple, acc: JsonTuple => JsonTuple): JsonTuple = r match {
        case _ #: JsonNil | JsonNil => acc(JsonNil)
        case x #: xs => _init(xs, e => acc(x #: e))
      }

      _init(t, identity[JsonTuple])
    }

    override def last(t: T): Option[JsonElement] = {
      @tailrec
      def _last(r: JsonTuple): Option[JsonElement] = r match {
        case x #: JsonNil => Some(x)
        case _ #: xs => _last(xs)
        case _ => None
      }

      _last(t)
    }

    override def split(t: T, n: Int): JsonTuple = if (n >= 0) JsonTuple(take(t, n), drop(t, n)) else t

    override def take(t: T, n: Int): JsonTuple = {
      @tailrec
      def _take(r: JsonTuple, v: Int, acc: JsonTuple => JsonTuple): JsonTuple = v match {
        case 0 => acc(JsonNil)
        case _ =>
          r match {
            case x #: xs => _take(xs, v - 1, e => acc(x #: e))
            case _ => acc(JsonNil)
          }
      }

      if (n >= 0) _take(t, n, identity[JsonTuple]) else t
    }

    override def zip(t1: T, t2: JsonTuple): JsonTuple = {
      @tailrec
      def _zip(r1: JsonTuple, r2: JsonTuple, acc: JsonTuple => JsonTuple): JsonTuple = (r1, r2) match {
        case (x1 #: xs1, x2 #: xs2) => _zip(xs1, xs2, e => acc(JsonTuple(x1, x2) #: e))
        case (_, JsonNil) | (JsonNil, _) => acc(JsonNil)
      }

      _zip(t1, t2, identity[JsonTuple])
    }

    override def toSeq(t: T): Seq[JsonElement] = {
      @tailrec
      def _toSeq(r: JsonTuple, s: Seq[JsonElement]): Seq[JsonElement] = r match {
        case x #: xs => _toSeq(xs, s :+ x)
        case _ => s
      }

      _toSeq(t, Seq.empty[JsonElement])
    }

    override def foreach(t: T, f: JsonElement => Unit): Unit = {
      @tailrec
      def _foreach(r: JsonTuple): Unit = r match {
        case x #: xs =>
          f(x)
          _foreach(xs)
        case _ => ()
      }

      _foreach(t)
    }
  }
}

export SequentialOps.*
