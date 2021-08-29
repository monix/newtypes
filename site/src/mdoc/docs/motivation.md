---
layout: docs
title:  "Motivation"
---

# Motivation for Newtypes

In other statically typed languages, such as Haskell, a `newtype` declaration creates a new type out of an existing one, like a type safe alias.

In Scala, this would be a perfectly acceptable newtype declaration, even if not ideal:

```scala
final case class Surname(value: String)
```

NOTE: the constructor has to take a single parameter. If it takes more than one parameter, technically it isn't a newtype.

The purpose is static type safety:

```scala
// It's much safe to deal with this:
def register(
  fname: FirstName,
  lname: LastName,
  ea: EmailAddress,
): IO[Account] = ???

// ... than to deal with this ...
def register(
  firstName: String,
  lastName: String,
  emailAddress: String,
): IO[Account] = ???
```

Note the invocation:

```scala
register(
  "Alex",
  "Nedelcu",
  "noreply@alexn.org",
)
```

It's easy to mix ordering, or to break the signature when we insert a new parameter between the existing ones. With lack of type safety, we have to rely on names:

```scala
register(
  firstName = "Alex",
  lastName = "Nedelcu",
  emailAddress = "noreply@alexn.org",
)
```

But now we're down to using discipline, as the compiler can't protect us.

A second usage of newtypes is for working with [type classes](https://typelevel.org/cats/) and defining alternative instances to those already defined, or for defining instances for types that we don't control.

For example, the `Ordering` type class in Scala has default instances for primitives, but the order is ascending:

```scala mdoc:silent
import scala.math.Ordering
import scala.collection.immutable.SortedSet

implicitly[Ordering[Int]].compare(1, 2)
//=> -1

SortedSet(1, 10, 9, 2, 5, 3)
//=> TreeSet(1, 2, 3, 5, 9, 10)
```

If we want a different ordering, it's a very bad practice to redefine the available instance. The best practice is to define a newtype:

```scala mdoc:silent
case class ReversedInt(value: Int) {
  override def toString = value.toString
}

object ReversedInt {
  implicit val ord: Ordering[ReversedInt] =
    (x, y) => -1 * implicitly[Ordering[Int]].compare(x.value, y.value)
}

SortedSet(List(1, 10, 9, 2, 5, 3).map(ReversedInt(_)):_*)
//=> TreeSet(10, 9, 5, 3, 2, 1)
```

## The library's purpose

Working with case classes, like the above, is completely fine. However, they have a runtime cost, generating extra boxing and unboxing. Also, you may want to add extra validation, or to derive type class instances.

Scala 3 has introduced [opaque types](https://docs.scala-lang.org/scala3/reference/other-new-features/opaques.html) for this purpose. These are type-safe aliases that have no added runtime cost. However, it would be nice to have some helpers in a cross-compiled fashion that also provides an encoding compatible with Scala 2.

We already had [scala-newtype](https://github.com/estatico/scala-newtype), a pretty awesome project. Monix's Newtypes is inspired by it. The purpose of this project is to be stable and easy to port. That means no macros, and no magic based on implicits.
