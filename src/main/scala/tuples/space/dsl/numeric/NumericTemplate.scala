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
package tuples.space.dsl.numeric

import tuples.space.JsonTemplate
import tuples.space.JsonTemplate.JsonNumericTemplate
import tuples.space.dsl.Template

trait NumericTemplate {

  trait EmptyNumericTemplate[
    A: Numeric,
    B <: WithMinimumNumericTemplate[A, D],
    C <: WithMaximumNumericTemplate[A, D],
    D <: CompleteNumericTemplate[A]
  ](
    withMinBuilder: Either[A, A] => B,
    withMaxBuilder: Either[A, A] => C,
    converter: () => JsonNumericTemplate[A]
  ) extends Template {

    def gte(min: A): B = withMinBuilder(Left[A, A](min))

    def gt(min: A): B = withMinBuilder(Right[A, A](min))

    def lte(max: A): C = withMaxBuilder(Left[A, A](max))

    def lt(max: A): C = withMaxBuilder(Right[A, A](max))

    override def toJsonTemplate: JsonTemplate = converter()
  }

  trait WithMinimumNumericTemplate[A: Numeric, B <: CompleteNumericTemplate[A]](
    min: Either[A, A],
    completeBuilder: (Either[A, A], Either[A, A]) => B,
    converter: Either[A, A] => JsonNumericTemplate[A]
  ) extends Template {

    def lte(max: A): B = completeBuilder(min, Left[A, A](max))

    def lt(max: A): B = completeBuilder(min, Right[A, A](max))

    override def toJsonTemplate: JsonTemplate = converter(min)
  }

  trait WithMaximumNumericTemplate[A: Numeric, B <: CompleteNumericTemplate[A]](
    max: Either[A, A],
    completeBuilder: (Either[A, A], Either[A, A]) => B,
    converter: Either[A, A] => JsonNumericTemplate[A]
  ) extends Template {

    def gte(min: A): B = completeBuilder(Left[A, A](min), max)

    def gt(min: A): B = completeBuilder(Right[A, A](min), max)

    override def toJsonTemplate: JsonTemplate = converter(max)
  }

  trait CompleteNumericTemplate[A: Numeric](
    min: Either[A, A],
    max: Either[A, A],
    converter: (Either[A, A], Either[A, A]) => JsonNumericTemplate[A]
  ) extends Template {

    override def toJsonTemplate: JsonTemplate = converter(min, max)
  }
}
