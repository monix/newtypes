---
layout: docs
title:  Defining Newtypes
---

# Defining Newtypes

Importing the dependency:

```scala
libraryDependencies += "io.monix" %% "newtypes-core" % "<version>"
```

**Table of contents:**

- [Newtype](#newtype)
  - [NewtypeWrapped](#newtypewrapped)
  - [NewtypeValidated](#newtypevalidated)
  - [Deriving type-class instances](#deriving-type-class-instances)
- [NewtypeK and NewtypeCovariantK](#newtypek-and-newtypecovariantk)
- [Newsubtype](#newtype)

## Newtype

[Newtype]({{ site.api_baseurl }}/io/monix/newtypes/Newtype.html) exposes the base encoding for newtypes over types with no type parameters. It provides no pre-defined builders, such that you need to provide `apply` or `unapply` by yourself:

```scala
import monix.newtypes._

type EmailAddress = EmailAddress.Type

object EmailAddress extends Newtype[String] {
  def apply(value: String): Option[EmailAddress] =
    if (value.contains("@"))
      Some(unsafeBuild(value))
    else
      None

  def unapply[A](a: A)(implicit ev: A =:= Type): Some[String] =
    Some(value(ev(a)))
}
```

It's more convenient to work with `NewtypeWrapped` or `NewtypeValidated`, as shown below.

### NewtypeWrapped

We can use [NewtypeWrapped]({{ site.api_baseurl }}/io/monix/newtypes/NewtypeWrapped.html) for creating newtypes, as simple wrappers (no validation) over types with no type parameters:

```scala mdoc:reset:silent
import monix.newtypes._

type FirstName = FirstName.Type

object FirstName extends NewtypeWrapped[String]

// Usage:
val fname = FirstName("Alex")

// To coerce into its source type again:
fname.value
//=> val res: String = "Alex"
```

Note, this is a type-safe alias, aka an "[opaque type](https://docs.scala-lang.org/scala3/reference/other-new-features/opaques.html)", so our `FirstName` is not seen as a `String` or vice-versa:

```scala mdoc:fail:silent
// ERROR — should fail at compile-time
val fname1: FirstName = "Alex"
```

```scala mdoc:fail:silent
// ERROR — should fail at compile-time too
val fname2: String = FirstName("Alex")
```

Pattern matching is also possible:

```scala mdoc:silent
fname match {
  case FirstName(str) => 
    s"Name: $str"
}
```

Note that due to type-erasure we are restricting the pattern matching that's possible. This doesn't work:

```scala mdoc:fail:silent
// ERROR — should fail at compile-time
(fname: Any) match {
  case FirstName(_) => "Matches!"
  case _ => "Nope!"  
}
```

This doesn't work either:

```scala mdoc:fail:silent
// ERROR — should fail at compile-time
"Alex" match {
  case FirstName(_) => "Matches!"
  case _ => "Nope!"  
}
```

And trying to do a regular `isInstanceOf` checks should trigger at least a Scala warning, due to the type being erased, hopefully you're working with `-Xfatal-warnings`:

```scala mdoc:fail:silent
// ERROR — should fail at compile-time
fname match {
  case ref: FirstName => "Matches!"
}
```

### NewtypeValidated

Use [NewtypeValidated]({{ site.api_baseurl }}/io/monix/newtypes/NewtypeValidated.html) for creating newtypes that have extra validation:

```scala mdoc:reset:silent
import monix.newtypes._

type EmailAddress = EmailAddress.Type

object EmailAddress extends NewtypeValidated[String, Exception] {
  def apply(v: String): Either[Exception, EmailAddress] =
    if (v.contains("@")) 
      Right(unsafeBuild(v))
    else 
      Left(new IllegalArgumentException("Not a valid email"))
}
```

We only allow strings with a certain format to be considered valid email addresses:

```scala mdoc
EmailAddress("noreply@alexn.org") match {
  case Right(address) =>
    s"Validated: ${address.value}"
  case Left(e) =>
    s"Error: $e"
}
```

There are cases in which the validation needs to be bypassed, which can be done via the "unsafe" builder:

```scala mdoc:silent
val address = EmailAddress.unsafe("noreply@alexn.org")
```

And, we can pattern match it to extract its value:

```scala mdoc
address match {
  case EmailAddress(str) => s"Matched: $str"
}
```

Note the same caveats apply for pattern matching:

```scala mdoc:fail:silent
// ERROR — should fail at compile-time
(address: Any) match {
  case EmailAddress(_) => ()
  case _ => ()
}
```

```scala mdoc:fail:silent
// ERROR — should fail at compile-time
"noreply@alexn.org" match {
  case EmailAddress(_) => ()
  case _ => ()
}
```

```scala mdoc:fail:silent
// ERROR — triggers at least a warning at compile-time
address match {
  case _: EmailAddress => ()
  case _ => ()
}
```

### Deriving type-class instances


We can derive type class instances, with a `derive` helper available in `Newtype`:

```scala mdoc:reset:silent
import cats._
import cats.implicits._
import monix.newtypes._

type FirstName = FirstName.Type

object FirstName extends NewtypeWrapped[String] {
  implicit val eq: Eq[FirstName] = derive
  implicit val show: Show[FirstName] = derive
}

// ...
val fname = FirstName("Alex")

assert(fname.show == "Alex")
assert(Eq[FirstName].eqv(fname, FirstName("Alex"))) 
```

## NewtypeK and NewtypeCovariantK


[NewtypeK]({{ site.api_baseurl }}/io/monix/newtypes/NewtypeK.html) is for defining newtypes over types with an *invariant type parameter*.

[NewtypeCovariantK](({{ site.api_baseurl }}/io/monix/newtypes/NewtypeCovariantK.html)) inherits from it and is for defining newtypes over types with a *covariant type parameter*.

```scala mdoc:reset:silent
import cats._
import cats.implicits._
import monix.newtypes._

type NonEmptyList[A] = NonEmptyList.Type[A]

object NonEmptyList extends NewtypeCovariantK[List] {
  // Builder forces at least one element
  def apply[A](head: A, tail: A*): NonEmptyList[A] =
    unsafeBuild(head :: tail.toList)

  // Exposes (head, tail)
  def unapply[F[_], A](list: F[A])(
    implicit ev: F[A] =:= NonEmptyList[A]
  ): Some[(A, List[A])] = {
    val l = value(list)
    Some((l.head, l.tail))
  }

  // Utilities specific for NonEmptyList
  implicit final class NelOps[A](val self: NonEmptyList[A]) {
    def head: A = self.value.head
    def tail: List[A] = self.value.tail
  }

  implicit def eq[A: Eq]: Eq[NonEmptyList[A]] =
    derive

  // Deriving type-class instance working on F[_], notice use of deriveK
  implicit val traverse: Traverse[NonEmptyList] =
    deriveK

  // Deriving type-class instance working on F[_], notice use of deriveK
  implicit val monad: Monad[NonEmptyList] =
    deriveK
}
```

And usage:

```scala mdoc
val colors = NonEmptyList("Red", "Green", "Blue")

colors.head
colors.tail

// Pattern matching works
colors match {
  case NonEmptyList(head, tail) => ()
}

// Covariance works
val any: NonEmptyList[Any] = colors

// It can be traversed
NonEmptyList(Option("Red"), Option("Green"), Option("Blue"))
  .sequence
```

With `NewtypeK` and `NewtypeCovariantK` you have to provide the `apply`, `unapply`, and other utilities by youself. Which makes sense, as these are more complex types to deal with.

## Newsubtype

[Newsubtype]({{ site.api_baseurl }}/io/monix/newtypes/Newsubtype.html) exposes the base encoding for newsubtypes over types with no type parameters. It functions exactly the same as `Newtype`, except as a subtype of the underlying type instead of as an entirely new type.

It provides the same utility classes as [Newtype](#newtype), including `NewsubtypeWrapped`, `NewsubtypeValidated`, `NewsubtypeK`, and `NewsubtypeCovariantK`.

There are two core benefits of `Newsubtype` and its variants:
1. `Newsubtype`s of primitives are unboxed in scala 2 (in scala 3 both should be unboxed as expected).
2. There is reduced boilerplate in dealing with the underlying type.

That said, unless you know you need `Newsubtype`, you're likely better off living with the extra boilerplate in a production system, as `Newsubtype` can lead to accidental unwrapping.

`Newsubtype`s don't need to declare forwarding methods or reimplement any methods on their underlying types:

```scala mdoc:reset:silent
import monix.newtypes._

type Level = Level.Type
object Level extends NewsubtypeWrapped[Int]

val myLevel: Level = Level(5)
```

Thus we can do things like call `+` from `Int` on our new subtype, however this unwraps our result to `Int`:

```scala mdoc
val anotherLevel: Int = myLevel + 1
```

The likely desired result type doesn't work:

```scala mdoc:fail:silent
val newLevel: Level = level + 1
```

We would need to re-wrap our results, which could be prohibitively expensive depending on the validation logic on the `Newsubtype`:

```scala mdoc
val newLevel: Level = Level(level + 1)
```

`Newsubtype` can unwrap in more subtle and potentially dangerous ways. As a simple and contrived example, instances of either of `Map[Level, Int]` or `List[Level]` have `apply` methods that can take our subtype `Level` but would return dramatically different results. If we were using the `Map` apply and someone else changed the data type to `List`, our code would continue to compile but silently produce invalid results. If our `Level` were a `Newtype` instead, code using the `List` `apply` method but expecting the `Map` `apply` would now fail at compile time.
