---
layout: docs
title:  "Integration with Cats typeclasses (Eq, Show, Hash, Order)"
---

# Integration with Cats typeclasses (Eq, Show, Hash, Order)

Importing the [Cats](https://github.com/typelevel/cats) integration:

```scala
// For Cats version 2.x.x
libraryDependencies += "io.monix" %% "newtypes-cats-v2" % "@VERSION@"
```

Usage:

```scala mdoc:silent
import monix.newtypes._
import monix.newtypes.integrations.DerivedCatsInstances

type Email = Email.Type
object Email extends NewtypeValidated[String] with DerivedCatsInstances {
  def apply(v: String): Either[BuildFailure[Type], Type] =
    if (v.contains("@")) 
      Right(unsafeCoerce(v))
    else 
      Left(BuildFailure("missing @"))
}
```

You can now use the syntax from cats:

```scala mdoc
import cats.syntax.all._

Email.unsafe("noreply@alexn.org").show
```
