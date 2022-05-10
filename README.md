# Monix's Newtypes

[![Build](https://github.com/monix/newtypes/workflows/build/badge.svg?branch=main)](https://github.com/monix/newtypes/actions?query=branch%3Amain+workflow%3Abuild) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.monix/newtypes-core_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.monix/newtypes-core_2.13)

Macro-free helpers for defining newtypes in Scala, cross-compiled to Scala 3.

## Usage

The packages are published on Maven Central.

```scala
libraryDependencies += "io.monix" %% "newtypes-core" % "0.2.2"
```

For the [Circe integration](https://newtypes.monix.io/docs/circe.html):

```scala
// For Circe version 0.14.x
libraryDependencies += "io.monix" %% "newtypes-circe-v0-14" % "0.2.2"
```

For the [PureConfig integration](https://newtypes.monix.io/docs/pure-config.html):

```scala
// For Circe version 0.14.x
libraryDependencies += "io.monix" %% "newtypes-pureconfig-v0-17" % "0.2.2"
```

NOTE: the [version scheme](https://www.scala-lang.org/blog/2021/02/16/preventing-version-conflicts-with-versionscheme.html) is set to `early-semver`.

### Documentation

- [ScalaDoc API](https://newtypes.monix.io/api/)
- [Website](https://newtypes.monix.io/docs/)
  - [Motivation](https://newtypes.monix.io/docs/motivation.html)
  - [Defining Newtypes](https://newtypes.monix.io/docs/core.html)
  - [Circe JSON integration](https://newtypes.monix.io/docs/circe.html)

## Acknowledgements

Encoding was shamelessly copied from the [scala-newtype](https://github.com/estatico/scala-newtype/) project by Cary Robbins et al.

## Contributing

This project welcomes contributions from anybody wishing to participate.  All code or documentation that is provided must be licensed with the same license that Newtypes is licensed with (Apache 2.0, see [LICENCE](./LICENSE.md)).

People are expected to follow the [Scala Code of Conduct](./CODE_OF_CONDUCT.md) when discussing Newtypes on GitHub, Gitter channel, or other venues.

Feel free to open an issue if you notice a bug, have an idea for a feature, or have a question about the code. Pull requests are also gladly accepted. For more information, check out the [contributor guide](./CONTRIBUTING.md).

## License

All code in this repository is licensed under the Apache License, Version 2.0.  See [LICENCE](./LICENSE.md).
