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

import scala.annotation.targetName

import tuples.space.dsl.*
import tuples.space.dsl.BooleanTemplate.*
import tuples.space.dsl.StringTemplate.*
import tuples.space.dsl.numeric.DoubleTemplate.*
import tuples.space.dsl.numeric.FloatTemplate.*
import tuples.space.dsl.numeric.IntTemplate.*
import tuples.space.dsl.numeric.LongTemplate.*

object DSL {

  private type ConstantElement = Int | Long | Float | Double | Boolean | String

  private def convertToTemplate(v: Template | ConstantElement): Template = v match {
    case e: ConstantElement =>
      e match {
        case i: Int => ConstantIntTemplate(i)
        case l: Long => ConstantLongTemplate(l)
        case f: Float => ConstantFloatTemplate(f)
        case d: Double => ConstantDoubleTemplate(d)
        case b: Boolean => ConstantBooleanTemplate(b)
        case s: String => ConstantStringTemplate(s)
      }
    case t: Template => t
  }

  def compile(template: AllOfTemplate | AnyOfTemplate | OneOfTemplate | NotTemplate | TupleTemplate): JsonTemplate =
    template.toJsonTemplate

  def complete(templates: (Template | ConstantElement)*): TupleTemplate =
    TupleTemplate(templates.map(convertToTemplate), additionalItems = false)

  def partial(templates: (Template | ConstantElement)*): TupleTemplate =
    TupleTemplate(templates.map(convertToTemplate), additionalItems = true)

  @targetName("any")
  def `*` : AnyTemplate.type = AnyTemplate

  def nil: NullTemplate.type = NullTemplate

  def float: EmptyFloatTemplate = EmptyFloatTemplate()

  def double: EmptyDoubleTemplate = EmptyDoubleTemplate()

  def bool: EmptyBooleanTemplate = EmptyBooleanTemplate()

  def string: EmptyStringTemplate = EmptyStringTemplate()

  def int: EmptyIntTemplate = EmptyIntTemplate()

  def long: EmptyLongTemplate = EmptyLongTemplate()

  def all(templates: (Template | ConstantElement)*): AllOfTemplate = AllOfTemplate(templates.map(convertToTemplate))

  def any(templates: (Template | ConstantElement)*): AnyOfTemplate = AnyOfTemplate(templates.map(convertToTemplate))

  def one(templates: (Template | ConstantElement)*): OneOfTemplate = OneOfTemplate(templates.map(convertToTemplate))

  def not(template: Template | ConstantElement): NotTemplate = NotTemplate(convertToTemplate(template))
}

export DSL.*
