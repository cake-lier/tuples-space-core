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

import tuples.space.*

object IntTemplate extends IntegralTemplate {

  class EmptyIntTemplate()
    extends EmptyIntegralTemplate[
      Int,
      WithMinimumIntTemplate,
      WithMaximumIntTemplate,
      WithMinWithMaxIntTemplate,
      WithMinWithMultipleIntTemplate,
      WithMaxWithMultipleIntTemplate,
      WithMultipleIntTemplate,
      CompleteIntTemplate
    ](
      new WithMinimumIntTemplate(_),
      new WithMaximumIntTemplate(_),
      new WithMultipleIntTemplate(_),
      () => JsonIntTemplate(None, None, None, None, None, None)
    )

  class WithMultipleIntTemplate(multipleOf: Int)
    extends WithMultipleIntegralTemplate[
      Int,
      WithMinWithMultipleIntTemplate,
      WithMaxWithMultipleIntTemplate,
      CompleteIntTemplate
    ](
      multipleOf,
      new WithMinWithMultipleIntTemplate(_, _),
      new WithMaxWithMultipleIntTemplate(_, _),
      JsonIntTemplate(None, _, None, None, None, None)
    )

  class WithMinimumIntTemplate(min: Either[Int, Int])
    extends WithMinimumIntegralTemplate[
      Int,
      WithMinWithMaxIntTemplate,
      WithMinWithMultipleIntTemplate,
      CompleteIntTemplate
    ](
      min,
      new WithMinWithMaxIntTemplate(_, _),
      new WithMinWithMultipleIntTemplate(_, _),
      min => JsonIntTemplate(None, None, min.left.toOption, None, min.toOption, None)
    )

  class WithMaximumIntTemplate(max: Either[Int, Int])
    extends WithMaximumIntegralTemplate[
      Int,
      WithMinWithMaxIntTemplate,
      WithMaxWithMultipleIntTemplate,
      CompleteIntTemplate
    ](
      max,
      new WithMinWithMaxIntTemplate(_, _),
      new WithMaxWithMultipleIntTemplate(_, _),
      max => JsonIntTemplate(None, None, None, max.left.toOption, None, max.toOption)
    )

  class WithMinWithMaxIntTemplate(min: Either[Int, Int], max: Either[Int, Int])
    extends WithMinWithMaxIntegralTemplate[
      Int,
      CompleteIntTemplate
    ](
      min,
      max,
      new CompleteIntTemplate(_, _, _),
      (min, max) => JsonIntTemplate(None, None, min.left.toOption, max.left.toOption, min.toOption, max.toOption)
    )

  class WithMinWithMultipleIntTemplate(min: Either[Int, Int], multipleOf: Int)
    extends WithMinWithMultipleIntegralTemplate[
      Int,
      CompleteIntTemplate
    ](
      min,
      multipleOf,
      new CompleteIntTemplate(_, _, _),
      (min, multipleOf) => JsonIntTemplate(None, multipleOf, min.left.toOption, None, min.toOption, None)
    )

  class WithMaxWithMultipleIntTemplate(max: Either[Int, Int], multipleOf: Int)
    extends WithMaxWithMultipleIntegralTemplate[
      Int,
      CompleteIntTemplate
    ](
      max,
      multipleOf,
      new CompleteIntTemplate(_, _, _),
      (max, multipleOf) => JsonIntTemplate(None, multipleOf, None, max.left.toOption, None, max.toOption)
    )

  class CompleteIntTemplate(min: Either[Int, Int], max: Either[Int, Int], multipleOf: Int)
    extends CompleteIntegralTemplate[Int](
      min,
      max,
      multipleOf,
      (min, max, multipleOf) =>
        JsonIntTemplate(None, multipleOf, min.left.toOption, max.left.toOption, min.toOption, max.toOption)
    )
}
