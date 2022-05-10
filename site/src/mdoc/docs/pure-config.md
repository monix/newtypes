---
layout: docs
title:  "Integration with PureConfig (HOCON configuration files)"
---

# Integration with PureConfig (HOCON configuration files)

Importing the [PureConfig](https://github.com/pureconfig/pureconfig) integration:

```scala
// For Circe version 0.14.x
libraryDependencies += "io.monix" %% "newtypes-pureconfig-v0.17" % "@VERSION@"
```

Usage:

```scala mdoc:silent
import monix.newtypes._
import monix.newtypes.integrations.DerivedPureConfigConvert
import pureconfig._
import pureconfig.generic.semiauto._

type EmailAddress = EmailAddress.Type
object EmailAddress extends NewtypeValidated[String] with DerivedPureConfigConvert {
  def apply(v: String): Either[BuildFailure[Type], Type] =
    if (v.contains("@")) 
      Right(unsafeCoerce(v))
    else 
      Left(BuildFailure("missing @"))
}

// Sample document
final case class Envelope[A](
  value: A
)

object Envelope {
  implicit def reader[A: ConfigReader]: ConfigReader[Envelope[A]] = 
    deriveReader
  implicit def writer[A: ConfigWriter]: ConfigWriter[Envelope[A]] =
    deriveWriter
}
```

You can now serialize and deserialize to/from HOCON configuration:

```scala mdoc
import pureconfig._
import com.typesafe.config._

val renderOptions =
  ConfigRenderOptions
    .defaults()
    .setOriginComments(false)
    .setComments(false)
    .setFormatted(true)
    .setJson(true)

val serialized = 
  ConfigWriter[Envelope[EmailAddress]]
    .to(Envelope(EmailAddress.unsafe("noreply@alexn.org")))
    .render(renderOptions)

ConfigSource
  .string(serialized)
  .load[Envelope[EmailAddress]]
```
