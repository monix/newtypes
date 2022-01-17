---
layout: docs
title:  "Integration with Circe (JSON encoding/decoding)"
---

# Integration with Circe (JSON encoding/decoding)

Importing the dependency for Circe version `0.14.x`:

```scala
libraryDependencies += "io.monix" %% "newtypes-circe-v0.14" % "<version>"
```

Usage:

```scala mdoc:silent
import monix.newtypes._
import monix.newtypes.integrations.DerivedCirceCodec

type Email = Email.Type
object Email extends NewtypeValidated[String] with DerivedCirceCodec {
  def apply(v: String): Either[BuildFailure[String], Email] =
    if (v.contains("@")) 
      Right(unsafeCoerce(v))
    else 
      Left(BuildFailure(TypeInfo.of[Email], v, Some("missing @")))
}
```

You can now serialize and deserialize to/from JSON:

```scala mdoc
import io.circe.syntax._
import io.circe.parser.decode

decode[Email](
  Email.unsafe("noreply@alexn.org")
    .asJson
    .noSpaces
)

decode[Email](
  Email.unsafe("la, la, lalalala")
    .asJson
    .noSpaces
)
```
