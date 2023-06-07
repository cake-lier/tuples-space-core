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
import tuples.space.JsonTemplate.JsonNumericTemplate.JsonIntegralTemplate
import tuples.space.dsl.Template

trait IntegralTemplate extends NumericTemplate {

  import scala.annotation.targetName

  trait ConstantIntegralTemplate[A: Integral](const: A, converter: Option[A] => JsonIntegralTemplate[A]) extends Template {

    override def toJsonTemplate: JsonTemplate = converter(Some(const))
  }

  trait EmptyIntegralTemplate[
    A: Integral,
    B <: WithMinimumIntegralTemplate[A, D, E, H],
    C <: WithMaximumIntegralTemplate[A, D, F, H],
    D <: WithMinWithMaxIntegralTemplate[A, H],
    E <: WithMinWithMultipleIntegralTemplate[A, H],
    F <: WithMaxWithMultipleIntegralTemplate[A, H],
    G <: WithMultipleIntegralTemplate[A, E, F, H],
    H <: CompleteIntegralTemplate[A]
  ](
    withMinBuilder: Either[A, A] => B,
    withMaxBuilder: Either[A, A] => C,
    withMultipleBuilder: A => G,
    converter: () => JsonIntegralTemplate[A]
  ) extends Template {

    def gte(min: A): B = withMinBuilder(Left[A, A](min))

    def gt(min: A): B = withMinBuilder(Right[A, A](min))

    def lte(max: A): C = withMaxBuilder(Left[A, A](max))

    def lt(max: A): C = withMaxBuilder(Right[A, A](max))

    def div(multipleOf: A): G = withMultipleBuilder(multipleOf)

    override def toJsonTemplate: JsonTemplate = converter()
  }

  trait WithMultipleIntegralTemplate[
    A: Integral,
    B <: WithMinWithMultipleIntegralTemplate[A, D],
    C <: WithMaxWithMultipleIntegralTemplate[A, D],
    D <: CompleteIntegralTemplate[A]
  ](
    multipleOf: A,
    withMinWithMultipleBuilder: (Either[A, A], A) => B,
    withMaxWithMultipleBuilder: (Either[A, A], A) => C,
    converter: Option[A] => JsonIntegralTemplate[A]
  ) extends Template {

    def gte(min: A): B = withMinWithMultipleBuilder(Left[A, A](min), multipleOf)

    def gt(min: A): B = withMinWithMultipleBuilder(Right[A, A](min), multipleOf)

    def lte(max: A): C = withMaxWithMultipleBuilder(Left[A, A](max), multipleOf)

    def lt(max: A): C = withMaxWithMultipleBuilder(Right[A, A](max), multipleOf)

    override def toJsonTemplate: JsonTemplate = converter(Some(multipleOf))
  }

  trait WithMinimumIntegralTemplate[
    A: Integral,
    B <: WithMinWithMaxIntegralTemplate[A, D],
    C <: WithMinWithMultipleIntegralTemplate[A, D],
    D <: CompleteIntegralTemplate[A]
  ](
    min: Either[A, A],
    withMinWithMaxBuilder: (Either[A, A], Either[A, A]) => B,
    withMinWithMultipleBuilder: (Either[A, A], A) => C,
    converter: Either[A, A] => JsonIntegralTemplate[A]
  ) extends Template {

    def lte(max: A): B = withMinWithMaxBuilder(min, Left[A, A](max))

    def lt(max: A): B = withMinWithMaxBuilder(min, Right[A, A](max))

    def div(multipleOf: A): C = withMinWithMultipleBuilder(min, multipleOf)

    override def toJsonTemplate: JsonTemplate = converter(min)
  }

  trait WithMaximumIntegralTemplate[
    A: Numeric,
    B <: WithMinWithMaxIntegralTemplate[A, D],
    C <: WithMaxWithMultipleIntegralTemplate[A, D],
    D <: CompleteIntegralTemplate[A]
  ](
    max: Either[A, A],
    withMinWithMaxBuilder: (Either[A, A], Either[A, A]) => B,
    withMaxWithMultipleBuilder: (Either[A, A], A) => C,
    converter: Either[A, A] => JsonIntegralTemplate[A]
  ) extends Template {

    def gte(min: A): B = withMinWithMaxBuilder(Left[A, A](min), max)

    def gt(min: A): B = withMinWithMaxBuilder(Right[A, A](min), max)

    def div(multipleOf: A): C = withMaxWithMultipleBuilder(max, multipleOf)

    override def toJsonTemplate: JsonTemplate = converter(max)
  }

  trait WithMinWithMaxIntegralTemplate[A: Numeric, B <: CompleteIntegralTemplate[A]](
    min: Either[A, A],
    max: Either[A, A],
    completeBuilder: (Either[A, A], Either[A, A], A) => B,
    converter: (Either[A, A], Either[A, A]) => JsonNumericTemplate[A]
  ) extends Template {

    def div(multipleOf: A): B = completeBuilder(min, max, multipleOf)

    override def toJsonTemplate: JsonTemplate = converter(min, max)
  }

  trait WithMinWithMultipleIntegralTemplate[A: Integral, B <: CompleteIntegralTemplate[A]](
    min: Either[A, A],
    multipleOf: A,
    completeBuilder: (Either[A, A], Either[A, A], A) => B,
    converter: (Either[A, A], Option[A]) => JsonIntegralTemplate[A]
  ) extends Template {

    def lte(max: A): B = completeBuilder(min, Left[A, A](max), multipleOf)

    def lt(max: A): B = completeBuilder(min, Right[A, A](max), multipleOf)

    override def toJsonTemplate: JsonTemplate = converter(min, Some(multipleOf))
  }

  trait WithMaxWithMultipleIntegralTemplate[A: Integral, B <: CompleteIntegralTemplate[A]](
    max: Either[A, A],
    multipleOf: A,
    completeBuilder: (Either[A, A], Either[A, A], A) => B,
    converter: (Either[A, A], Option[A]) => JsonIntegralTemplate[A]
  ) extends Template {

    def gte(min: A): B = completeBuilder(Left[A, A](min), max, multipleOf)

    def gt(min: A): B = completeBuilder(Right[A, A](min), max, multipleOf)

    override def toJsonTemplate: JsonTemplate = converter(max, Some(multipleOf))
  }

  trait CompleteIntegralTemplate[A: Integral](
    min: Either[A, A],
    max: Either[A, A],
    multipleOf: A,
    converter: (Either[A, A], Either[A, A], Option[A]) => JsonIntegralTemplate[A]
  ) extends Template {

    override def toJsonTemplate: JsonTemplate = converter(min, max, Some(multipleOf))
  }
}
