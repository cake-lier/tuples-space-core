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

import java.util.Objects
import io.github.cakelier.AnyOps.*

sealed trait JsonTemplate {

  def matches(value: JsonElement): Boolean
}

object JsonTemplate {

  sealed trait JsonMultipleTemplate extends JsonTemplate {

    val templates: Seq[JsonTemplate]
  }

  object JsonMultipleTemplate {

    sealed trait JsonAnyOfTemplate extends JsonMultipleTemplate

    object JsonAnyOfTemplate {

      final private case class JsonAnyOfTemplateImpl(templates: Seq[JsonTemplate]) extends JsonAnyOfTemplate {

        override def matches(value: JsonElement): Boolean = templates.exists(_.matches(value))
      }

      def apply(templates: Seq[JsonTemplate]): JsonAnyOfTemplate = JsonAnyOfTemplateImpl(templates)
    }

    sealed trait JsonAllOfTemplate extends JsonMultipleTemplate

    object JsonAllOfTemplate {

      final private case class JsonAllOfTemplateImpl(templates: Seq[JsonTemplate]) extends JsonAllOfTemplate {

        override def matches(value: JsonElement): Boolean = templates.forall(_.matches(value))
      }

      def apply(templates: Seq[JsonTemplate]): JsonAllOfTemplate = JsonAllOfTemplateImpl(templates)
    }

    sealed trait JsonOneOfTemplate extends JsonMultipleTemplate

    object JsonOneOfTemplate {

      final private case class JsonOneOfTemplateImpl(templates: Seq[JsonTemplate]) extends JsonOneOfTemplate {

        override def matches(value: JsonElement): Boolean = templates.find(_.matches(value)) match {
          case Some(t) => templates.diff(Seq(t)).forall(ts => JsonNotTemplate(ts).matches(value))
          case _ => false
        }
      }

      def apply(templates: Seq[JsonTemplate]): JsonOneOfTemplate = JsonOneOfTemplateImpl(templates)
    }
  }

  sealed trait JsonNotTemplate extends JsonTemplate {

    val template: JsonTemplate
  }

  object JsonNotTemplate {

    final private case class JsonNotTemplateImpl(template: JsonTemplate) extends JsonNotTemplate {

      override def matches(value: JsonElement): Boolean = !template.matches(value)
    }

    def apply(template: JsonTemplate): JsonNotTemplate = JsonNotTemplateImpl(template)
  }

  sealed trait JsonTupleTemplate extends JsonTemplate {

    val itemsTemplates: Seq[JsonTemplate]

    val additionalItems: Boolean
  }

  object JsonTupleTemplate {

    final private case class JsonTupleTemplateImpl(itemsTemplates: Seq[JsonTemplate], additionalItems: Boolean)
      extends JsonTupleTemplate {

      override def matches(value: JsonElement): Boolean = value match {
        case es: JsonTuple =>
          ((es.arity === itemsTemplates.size && !additionalItems) || (es.arity > itemsTemplates.size && additionalItems))
          && es.toSeq.zip(itemsTemplates).forall((e, t) => t.matches(e))
        case _ => false
      }
    }

    def apply(itemsTemplates: Seq[JsonTemplate], additionalItems: Boolean): JsonTupleTemplate =
      JsonTupleTemplateImpl(itemsTemplates, additionalItems)
  }

  @SuppressWarnings(Array("org.wartremover.warts.Null"))
  case object JsonNullTemplate extends JsonTemplate {

    override def matches(value: JsonElement): Boolean = value === null
  }

  case object JsonAnyTemplate extends JsonTemplate {

    override def matches(value: JsonElement): Boolean = true
  }

  sealed trait JsonBooleanTemplate extends JsonTemplate {

    val const: Option[Boolean]
  }

  object JsonBooleanTemplate {

    final private case class JsonBooleanTemplateImpl(const: Option[Boolean]) extends JsonBooleanTemplate {

      override def matches(value: JsonElement): Boolean = value match {
        case b: Boolean => const.forall(_ === b)
        case _ => false
      }
    }

    def apply(const: Option[Boolean]): JsonBooleanTemplate = JsonBooleanTemplateImpl(const)
  }

  import scala.reflect.ClassTag

  sealed trait JsonNumericTemplate[+A: Numeric: ClassTag] extends JsonTemplate {

    val const: Option[A]

    val minimum: Option[A]

    val maximum: Option[A]

    val exclusiveMinimum: Option[A]

    val exclusiveMaximum: Option[A]

    import math.Ordering.Implicits.infixOrderingOps

    override def matches(value: JsonElement): Boolean = value match {
      case n: A =>
        const.forall(_ === n)
        && minimum.forall(_ <= n)
        && maximum.forall(_ >= n)
        && exclusiveMinimum.forall(_ < n)
        && exclusiveMaximum.forall(_ > n)
      case _ => false
    }
  }

  object JsonNumericTemplate {

    sealed trait JsonFloatTemplate extends JsonNumericTemplate[Float]

    object JsonFloatTemplate {

      final private case class JsonFloatTemplateImpl(
        const: Option[Float],
        minimum: Option[Float],
        maximum: Option[Float],
        exclusiveMinimum: Option[Float],
        exclusiveMaximum: Option[Float]
      ) extends JsonFloatTemplate

      def apply(
        const: Option[Float],
        minimum: Option[Float],
        maximum: Option[Float],
        exclusiveMinimum: Option[Float],
        exclusiveMaximum: Option[Float]
      ): JsonFloatTemplate = JsonFloatTemplateImpl(const, minimum, maximum, exclusiveMinimum, exclusiveMaximum)
    }

    sealed trait JsonDoubleTemplate extends JsonNumericTemplate[Double]

    object JsonDoubleTemplate {

      final private case class JsonDoubleTemplateImpl(
        const: Option[Double],
        minimum: Option[Double],
        maximum: Option[Double],
        exclusiveMinimum: Option[Double],
        exclusiveMaximum: Option[Double]
      ) extends JsonDoubleTemplate

      def apply(
        const: Option[Double],
        minimum: Option[Double],
        maximum: Option[Double],
        exclusiveMinimum: Option[Double],
        exclusiveMaximum: Option[Double]
      ): JsonDoubleTemplate = JsonDoubleTemplateImpl(const, minimum, maximum, exclusiveMinimum, exclusiveMaximum)
    }

    sealed trait JsonIntegralTemplate[+A: Integral: ClassTag] extends JsonNumericTemplate[A] {

      val multipleOf: Option[A]

      import math.Integral.Implicits.infixIntegralOps

      override def matches(value: JsonElement): Boolean = super.matches(value) && (value match {
        case i: A => multipleOf.forall(i % _ === 0)
        case _ => false
      })
    }

    object JsonIntegralTemplate {

      sealed trait JsonIntTemplate extends JsonIntegralTemplate[Int]

      object JsonIntTemplate {

        final private case class JsonIntTemplateImpl(
          const: Option[Int],
          multipleOf: Option[Int],
          minimum: Option[Int],
          maximum: Option[Int],
          exclusiveMinimum: Option[Int],
          exclusiveMaximum: Option[Int]
        ) extends JsonIntTemplate

        def apply(
          const: Option[Int],
          multipleOf: Option[Int],
          minimum: Option[Int],
          maximum: Option[Int],
          exclusiveMinimum: Option[Int],
          exclusiveMaximum: Option[Int]
        ): JsonIntTemplate = JsonIntTemplateImpl(
          const,
          multipleOf,
          minimum,
          maximum,
          exclusiveMinimum,
          exclusiveMaximum
        )
      }

      sealed trait JsonLongTemplate extends JsonIntegralTemplate[Long]

      object JsonLongTemplate {

        final private case class JsonLongTemplateImpl(
          const: Option[Long],
          multipleOf: Option[Long],
          minimum: Option[Long],
          maximum: Option[Long],
          exclusiveMinimum: Option[Long],
          exclusiveMaximum: Option[Long]
        ) extends JsonLongTemplate

        def apply(
          const: Option[Long],
          multipleOf: Option[Long],
          minimum: Option[Long],
          maximum: Option[Long],
          exclusiveMinimum: Option[Long],
          exclusiveMaximum: Option[Long]
        ): JsonLongTemplate = JsonLongTemplateImpl(const, multipleOf, minimum, maximum, exclusiveMinimum, exclusiveMaximum)
      }
    }

    export JsonIntegralTemplate.*
  }

  import scala.util.matching.Regex

  sealed trait JsonStringTemplate extends JsonTemplate {

    val values: Option[Set[String]]

    val minLength: Option[Int]

    val maxLength: Option[Int]

    val pattern: Option[Regex]
  }

  object JsonStringTemplate {

    final private case class JsonStringTemplateImpl(
      values: Option[Set[String]],
      minLength: Option[Int],
      maxLength: Option[Int],
      pattern: Option[Regex]
    ) extends JsonStringTemplate {

      override def matches(value: JsonElement): Boolean = value match {
        case s: String =>
          values.forall(_.apply(s))
          && minLength.forall(_ <= s.length)
          && maxLength.forall(_ >= s.length)
          && pattern.forall(_.matches(s))
        case _ => false
      }

      @SuppressWarnings(Array("org.wartremover.warts.IsInstanceOf"))
      override def canEqual(that: Any): Boolean = that.isInstanceOf[JsonStringTemplateImpl]

      override def equals(obj: Any): Boolean = obj match {
        case t: JsonStringTemplateImpl =>
          t.canEqual(this)
          && values === t.values
          && minLength === t.minLength
          && maxLength === t.maxLength
          && pattern.map(_.pattern.pattern) === t.pattern.map(_.pattern.pattern)
        case _ => false
      }

      override def hashCode(): Int = Objects.hash(values, minLength, maxLength, pattern.map(_.pattern.pattern))
    }

    def apply(
      values: Option[Set[String]],
      minLength: Option[Int],
      maxLength: Option[Int],
      pattern: Option[Regex]
    ): JsonStringTemplate = JsonStringTemplateImpl(values, minLength, maxLength, pattern)
  }

  export JsonMultipleTemplate.*
  export JsonNumericTemplate.*
}

export JsonTemplate.*
