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

import tuples.space.JsonTemplate.JsonNumericTemplate.JsonDoubleTemplate

object DoubleTemplate extends NumericTemplate {

  class EmptyDoubleTemplate()
    extends EmptyNumericTemplate[
      Double,
      WithMinimumDoubleTemplate,
      WithMaximumDoubleTemplate,
      CompleteDoubleTemplate
    ](
      new WithMinimumDoubleTemplate(_),
      new WithMaximumDoubleTemplate(_),
      () => JsonDoubleTemplate(None, None, None, None, None)
    )

  class WithMinimumDoubleTemplate(min: Either[Double, Double])
    extends WithMinimumNumericTemplate[Double, CompleteDoubleTemplate](
      min,
      new CompleteDoubleTemplate(_, _),
      min => JsonDoubleTemplate(None, min.left.toOption, None, min.toOption, None)
    )

  class WithMaximumDoubleTemplate(max: Either[Double, Double])
    extends WithMaximumNumericTemplate[Double, CompleteDoubleTemplate](
      max,
      new CompleteDoubleTemplate(_, _),
      max => JsonDoubleTemplate(None, None, max.left.toOption, None, max.toOption)
    )

  class CompleteDoubleTemplate(min: Either[Double, Double], max: Either[Double, Double])
    extends CompleteNumericTemplate[Double](
      min,
      max,
      (min, max) => JsonDoubleTemplate(None, min.left.toOption, max.left.toOption, min.toOption, max.toOption)
    )
}
