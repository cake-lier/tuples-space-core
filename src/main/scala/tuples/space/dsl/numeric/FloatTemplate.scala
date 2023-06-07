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

import tuples.space.JsonTemplate.JsonNumericTemplate.JsonFloatTemplate

object FloatTemplate extends NumericTemplate {

  class ConstantFloatTemplate(const: Float)
    extends ConstantNumericTemplate[Float](
      const,
      JsonFloatTemplate(_, None, None, None, None)
    )

  class EmptyFloatTemplate()
    extends EmptyNumericTemplate[
      Float,
      WithMinimumFloatTemplate,
      WithMaximumFloatTemplate,
      CompleteFloatTemplate
    ](
      new WithMinimumFloatTemplate(_),
      new WithMaximumFloatTemplate(_),
      () => JsonFloatTemplate(None, None, None, None, None)
    )

  class WithMinimumFloatTemplate(min: Either[Float, Float])
    extends WithMinimumNumericTemplate[Float, CompleteFloatTemplate](
      min,
      new CompleteFloatTemplate(_, _),
      min => JsonFloatTemplate(None, min.left.toOption, None, min.toOption, None)
    )

  class WithMaximumFloatTemplate(max: Either[Float, Float])
    extends WithMaximumNumericTemplate[Float, CompleteFloatTemplate](
      max,
      new CompleteFloatTemplate(_, _),
      max => JsonFloatTemplate(None, None, max.left.toOption, None, max.toOption)
    )

  class CompleteFloatTemplate(
    min: Either[Float, Float],
    max: Either[Float, Float]
  ) extends CompleteNumericTemplate[Float](
      min,
      max,
      (min, max) => JsonFloatTemplate(None, min.left.toOption, max.left.toOption, min.toOption, max.toOption)
    )
}
