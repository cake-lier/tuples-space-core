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

import tuples.space.JsonTemplate.JsonNumericTemplate.JsonIntegralTemplate.JsonLongTemplate

object LongTemplate extends IntegralTemplate {

  class EmptyLongTemplate()
    extends EmptyIntegralTemplate[
      Long,
      WithMinimumLongTemplate,
      WithMaximumLongTemplate,
      WithMinWithMaxLongTemplate,
      WithMinWithMultipleLongTemplate,
      WithMaxWithMultipleLongTemplate,
      WithMultipleLongTemplate,
      CompleteLongTemplate
    ](
      new WithMinimumLongTemplate(_),
      new WithMaximumLongTemplate(_),
      new WithMultipleLongTemplate(_),
      () => JsonLongTemplate(None, None, None, None, None, None)
    )

  class WithMultipleLongTemplate(multipleOf: Long)
    extends WithMultipleIntegralTemplate[
      Long,
      WithMinWithMultipleLongTemplate,
      WithMaxWithMultipleLongTemplate,
      CompleteLongTemplate
    ](
      multipleOf,
      new WithMinWithMultipleLongTemplate(_, _),
      new WithMaxWithMultipleLongTemplate(_, _),
      JsonLongTemplate(None, _, None, None, None, None)
    )

  class WithMinimumLongTemplate(min: Either[Long, Long])
    extends WithMinimumIntegralTemplate[
      Long,
      WithMinWithMaxLongTemplate,
      WithMinWithMultipleLongTemplate,
      CompleteLongTemplate
    ](
      min,
      new WithMinWithMaxLongTemplate(_, _),
      new WithMinWithMultipleLongTemplate(_, _),
      min => JsonLongTemplate(None, None, min.left.toOption, None, min.toOption, None)
    )

  class WithMaximumLongTemplate(max: Either[Long, Long])
    extends WithMaximumIntegralTemplate[
      Long,
      WithMinWithMaxLongTemplate,
      WithMaxWithMultipleLongTemplate,
      CompleteLongTemplate
    ](
      max,
      new WithMinWithMaxLongTemplate(_, _),
      new WithMaxWithMultipleLongTemplate(_, _),
      max => JsonLongTemplate(None, None, None, max.left.toOption, None, max.toOption)
    )

  class WithMinWithMaxLongTemplate(min: Either[Long, Long], max: Either[Long, Long])
    extends WithMinWithMaxIntegralTemplate[
      Long,
      CompleteLongTemplate
    ](
      min,
      max,
      new CompleteLongTemplate(_, _, _),
      (min, max) => JsonLongTemplate(None, None, min.left.toOption, max.left.toOption, min.toOption, max.toOption)
    )

  class WithMinWithMultipleLongTemplate(min: Either[Long, Long], multipleOf: Long)
    extends WithMinWithMultipleIntegralTemplate[
      Long,
      CompleteLongTemplate
    ](
      min,
      multipleOf,
      new CompleteLongTemplate(_, _, _),
      (min, multipleOf) => JsonLongTemplate(None, multipleOf, min.left.toOption, None, min.toOption, None)
    )

  class WithMaxWithMultipleLongTemplate(max: Either[Long, Long], multipleOf: Long)
    extends WithMaxWithMultipleIntegralTemplate[
      Long,
      CompleteLongTemplate
    ](
      max,
      multipleOf,
      new CompleteLongTemplate(_, _, _),
      (max, multipleOf) => JsonLongTemplate(None, multipleOf, None, max.left.toOption, None, max.toOption)
    )

  class CompleteLongTemplate(min: Either[Long, Long], max: Either[Long, Long], multipleOf: Long)
    extends CompleteIntegralTemplate[Long](
      min,
      max,
      multipleOf,
      (min, max, multipleOf) =>
        JsonLongTemplate(None, multipleOf, min.left.toOption, max.left.toOption, min.toOption, max.toOption)
    )
}
