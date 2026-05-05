## Usage

The packages are published on Maven Central:

```scala
libraryDependencies += "io.monix" %% "newtypes-core" % "{{ projectVersion }}"
```

### Quick sample

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

## Documentation

- [Motivation](./motivation.md)
- [Defining Newtypes](./core.md)
- [Integration with Circe (JSON encoding/decoding)](./circe.md)
- [Integration with PureConfig (HOCON configuration files)](./pure-config.md)
