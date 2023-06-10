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
package tuples.space.dsl

import tuples.space.JsonTemplate
import JsonTemplate.JsonStringTemplate

object StringTemplate {

  class ConstantStringTemplate(const: String) extends Template {

    override def toJsonTemplate: JsonTemplate = JsonStringTemplate(Some(Set(const)), None, None, None)
  }

  import scala.util.matching.Regex

  class EmptyStringTemplate() extends Template {

    def in(values: String*): CompleteStringTemplate = CompleteStringTemplate(Some(values.toSet), None, None, None)

    def matches(regex: Regex): CompleteStringTemplate = CompleteStringTemplate(None, None, None, Some(regex))

    def gte(minLength: Int): WithMinimumStringTemplate = WithMinimumStringTemplate(minLength)

    def lte(maxLength: Int): WithMaximumStringTemplate = WithMaximumStringTemplate(maxLength)

    override def toJsonTemplate: JsonTemplate = JsonStringTemplate(None, None, None, None)
  }

  class WithMinimumStringTemplate(minLength: Int) extends Template {

    def lte(maxLength: Int): CompleteStringTemplate = CompleteStringTemplate(None, Some(minLength), Some(maxLength), None)

    override def toJsonTemplate: JsonTemplate = JsonStringTemplate(None, Some(minLength), None, None)
  }

  class WithMaximumStringTemplate(maxLength: Int) extends Template {

    def gte(minLength: Int): CompleteStringTemplate = CompleteStringTemplate(None, Some(minLength), Some(maxLength), None)

    override def toJsonTemplate: JsonTemplate = JsonStringTemplate(None, None, Some(maxLength), None)
  }

  class CompleteStringTemplate(
    values: Option[Set[String]],
    minLength: Option[Int],
    maxLength: Option[Int],
    pattern: Option[Regex]
  ) extends Template {

    override def toJsonTemplate: JsonTemplate = JsonStringTemplate(values, minLength, maxLength, pattern)
  }
}
