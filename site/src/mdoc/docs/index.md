---
layout: docs
title:  "Documentation"
position: 105
---

The packages are published on Maven Central:

```scala
libraryDependencies += "io.monix" %% "newtypes-core" % "<version>"
```

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.monix/newtypes-core_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.monix/newtypes-core_2.13)

## Quick sample

```scala mdoc:silent
import monix.newtypes._

// Just for deriving type class instances
import cats._
import cats.implicits._

type Firstname = Firstname.Type
object Firstname extends NewtypeWrapped[String] {
  implicit val eq: Eq[Firstname] = derive
}

// Usage:
val name = Firstname("Alex")

// Coercing back into String:
name.value
```

See the documentation menu for the available topics.
