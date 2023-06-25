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

  /** The "empty" double template, capable of matching any value of the double type.
   *
   * This is the starting template from which creating any other double template by specifying more and more constraints. Being
   * so, it is allowed to add a minimum value, either inclusive or exclusive, or a maximum value, either inclusive or exclusive,
   * to create a new template from this.
   */
  class EmptyDoubleTemplate
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

  /** The double template for which the minimum value, either inclusive or exclusive, has been specified.
   *
   * This template represents one for double values where the minimum constraint, whether it be an inclusive or exclusive one,
   * has been specified. Now, only a maximum constraint can be specified, closing the now half-open interval. This is not mandatory
   * to do. This will produce a "complete" template for which no more constraints can be specified.
   *
   * @constructor creates a new instance of this class specifying the minimum value, being a [[scala.util.Left]] if it inclusive,
   *              or a [[scala.util.Right]] if it is exclusive
   */
  class WithMinimumDoubleTemplate(min: Either[Double, Double])
    extends WithMinimumNumericTemplate[Double, CompleteDoubleTemplate](
      min,
      new CompleteDoubleTemplate(_, _),
      min => JsonDoubleTemplate(None, min.left.toOption, None, min.toOption, None)
    )

  /** The double template for which the maximum value, either inclusive or exclusive, has been specified.
   *
   * This template represents one for double values where the maximum constraint, whether it be an inclusive or exclusive one,
   * has been specified. Now, only a minimum constraint can be specified, closing the now half-open interval. This is not mandatory
   * to do. This will produce a "complete" template for which no more constraints can be specified.
   *
   * @constructor creates a new instance of this class specifying the maximum value, being a [[scala.util.Left]] if it inclusive,
   *              or a [[scala.util.Right]] if it is exclusive
   */
  class WithMaximumDoubleTemplate(max: Either[Double, Double])
    extends WithMaximumNumericTemplate[Double, CompleteDoubleTemplate](
      max,
      new CompleteDoubleTemplate(_, _),
      max => JsonDoubleTemplate(None, None, max.left.toOption, None, max.toOption)
    )

  /** The "terminal" double template, for which no more information can be specified for building a [[JsonDoubleTemplate]].
   *
   * This template represents the last stage in building a numeric template, meaning that no more constraints can be specified.
   * The range for the value has already been specified as a minimum and maximum allowed values, either inclusive or exclusive.
   *
   * @constructor creates a new instance of this class specifying the minimum and the maximum values, being [[scala.util.Left]]s
   *              if they are inclusive, or [[scala.util.Right]]s if they are exclusive
   */
  class CompleteDoubleTemplate(min: Either[Double, Double], max: Either[Double, Double])
    extends CompleteNumericTemplate[Double](
      min,
      max,
      (min, max) => JsonDoubleTemplate(None, min.left.toOption, max.left.toOption, min.toOption, max.toOption)
    )
}
