# tuples-space-core

[![Build status](https://github.com/cake-lier/tuples-space-core/actions/workflows/release.yml/badge.svg)](https://github.com/cake-lier/tuples-space-core/actions/workflows/release.yml)
[![semantic-release: conventional-commits](https://img.shields.io/badge/semantic--release-conventional_commits-e10098?logo=semantic-release)](https://github.com/semantic-release/semantic-release)
[![Latest release](https://img.shields.io/github/v/release/cake-lier/tuples-space-core)](https://github.com/cake-lier/tuples-space-core/releases/latest/)
[![Scaladoc](https://img.shields.io/github/v/release/cake-lier/tuples-space-core?label=scaladoc)](https://cake-lier.github.io/tuples-space-core/io/github/cakelier/tuples/space)
[![Issues](https://img.shields.io/github/issues/cake-lier/tuples-space-core)](https://github.com/cake-lier/tuples-space-core/issues)
[![Pull requests](https://img.shields.io/github/issues-pr/cake-lier/tuples-space-core)](https://github.com/cake-lier/tuples-space-core/pulls)
[![Codecov](https://codecov.io/gh/cake-lier/tuples-space-core/branch/main/graph/badge.svg?token=UX36N6CU78)](https://codecov.io/gh/cake-lier/tuples-space-core)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=cake-lier_tuples-space-core&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=cake-lier_tuples-space-core)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=cake-lier_tuples-space-core&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=cake-lier_tuples-space-core)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=cake-lier_tuples-space-core&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=cake-lier_tuples-space-core)

## How to use

Add the following line to your `build.sbt` file:

```scala
libraryDependencies ++= Seq("io.github.cake-lier" % "tuples-space-core" % "1.0.2")
```

This library is currently available only for scala 3.

## What is this?

This library is the core of a bigger project which allows to create tuple spaces in an easy and reliable way. A tuple
space is a mean to exchange pieces of information between parties while at the same time coordinating the actions of the parties that
need those pieces of information. For example, an entity could suspend its job while waiting for the information to be available,
not differently from how a future-based computation works. When it is available, it can carry on from where it left off. The idea of
a tuple space, which very aptly is a "coordination medium", is to bring this onto distributed systems, which are by definition
more challenging to model, coordinate and in general make work. If you are familiar with some message-oriented middleware, such
as RabbitMQ, this is not different, with the added twist that not only we can send and receive messages, but also wait
for them, decide whether remove them from the pool of messages, probe for their presence or absence etc. A tuple space is just a
big pool of messages, waiting to be read from someone or probed or whatever. Differently from RabbitMQ, we just don't subscribe to
topics, because every receive operation is intended to receive one and only one message.

This repo contains only the core part of this project: the tools for creating tuples and templates. The actual operations are
discussed in the repo which hosts the client for interacting with the tuple space. Another repo exists which gives an implementation
to the tuple space server, which the clients can interact with.

## Okay, but what are tuples and templates, then?

Well, theoretically, anything you want. Really. They are bits of information, so anything that can constitute a piece of information is a
valid tuple. And anything that can "match" that information, discriminating between different bits is a valid template. This
implementation exists to give a more modern interpretation of a tuple: a JSON tuple. In this idea, a tuple is any valid JSON array,
except that it cannot contain, directly nor nested, JSON objects. This is because it would have allowed structures not
ideally simple and "indexed", such as an array, the original idea which the notion of a tuple revolves around. Nevertheless, tuples
can be nested, as JSON array can, and can also contain integers, long integers, booleans, floats, doubles, nulls and strings. The
whole library is to keep the implementation as close to the JSON specification as possible, being the standard interchange format
in use on the web today, which was deemed apt for some middleware where exchange is at its core.

Being tuples some JSON documents formatted in a particular way, templates are also JSON templates. The most successful, other than
the only standard, approach to check for conformity of a JSON document, meaning to check if a given JSON document matches or not
against a given template, is the "JSON Schema" one. JSON Schema allows to define schemas for JSON documents to adhere, which is
another perfect standard to follow while creating a template. This does not mean that templates are converted to JSON Schemas, nor
that the JSON Schema is followed in its entirety. But whatever it is that a JSON template does, it works exactly as
described in JSON Schema.

## Well, so can you show me some examples for creating tuples and templates?

A rightful question. The syntax for creating a JSON tuple is heavily inspired by the scala "Shapeless" library, so you can write

```scala
import io.github.cakelier.tuples.space.*

val tuple = "string" #: 1 #: 2L #: 3.5 #: 4.6f #: true #: null #: ("nested" #: false #: JsonNil) #: JsonNil
```

for specifying a JSON tuple, separating each element with a `#:` and terminating a tuple with a `JsonNil`. Another more scala-like
syntax is available

```scala
import io.github.cakelier.tuples.space.*

val tuple = JsonTuple("string", 1, 2L, 3.5, 4.6f, true, null, JsonTuple("nested", false))
```

The two syntaxes can be used together, respecting their rules as stated above. There are ways to create a tuple also from a `Seq`,
but there exists a scaladoc for a reason.

For the templates, it is available a very simple DSL to create them. Everything starts from two methods, which allow creating a
template that matches tuples with the number of elements specified or creating a template that matches tuples with
elements greater or equal to the number of elements specified. These are respectively `complete` and `partial`.

```scala
import io.github.cakelier.tuples.space.*

val template = complete(bool, int, nil)

val template = partial(bool, int nil)
```

Other templates that can be specified are the numerical ones, for matching int, long, float and double values. For all of them
can be specified a minimum and a maximum allowed value, either inclusive or exclusive, and for int and long also a value for which
they should be a multiple of. These can be specified with methods `gt` (greater than), `gte` (greater than or equal), `lt`
(less than), `lte` (less than or equal), `div` (divisible by).

```scala
import io.github.cakelier.tuples.space.*

val template = complete(
  int gt 1 lt 20 div 2,
  long lte -3L div 11 gte -100L,
  float lt 3.0e5f gt -3.0e4f,
  double gt 23e-11 lt 1e10
)
```

Other templates are available for matching boolean values, `bool`, null values, `nil` or even any value, `*`. For string values,
it can be specified if the string should be in a set of values with the `in` method, it should match a given regular expression
with `matches` or if it should have a minimum or a maximum length with `gte` and `lte`.

```scala
import io.github.cakelier.tuples.space.*

val template = partial(
  bool
  nil,
  *,
  string in ("hello", "world"),
  string matches "[0-9]+".r,
  string gte 4 lte 9
)
```

At last, for an element of a tuple template, a "meta-template" can be specified. This special template is a template that builds
upon one or more other templates. The meta-templates available are `anyOf`, which matches if and only if any of the given
templates matches; `allOf`, which matches if and only if all the given templates match; `oneOf`, which matches if and only if
exactly one of the given templates matches and `not`, which matches if and only if the given template does not match.

```scala
import io.github.cakelier.tuples.space.*

val template1 = complete(anyOf(int, string), nil) // matches JsonTuple(1, null); does not match JsonTuple(true, null)
val template2 = complete(allOf(int, long), nil) // matches JsonTuple(1, null); does not match JsonTuple(3.5, null)
val template3 = complete(oneOf(int, float), nil) // matches JsonTuple(3.5f, null); does not match JsonTuple(1, null)
val template4 = complete(not(int), nil) // matches JsonTuple(true, null); does not match JsonTuple(1, null)
```

It is also possible to specify a tuple as a template or as part of it. In this case, the exact values given are matched to the
tuples.

```scala
import io.github.cakelier.tuples.space.*

val template = partial("test", int lt 3, true, bool)
```

## Can I use it?

Of course, the MIT license is applied to this whole project.
