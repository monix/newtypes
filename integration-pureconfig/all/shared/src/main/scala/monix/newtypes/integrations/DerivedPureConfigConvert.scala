/*
 * Copyright (c) 2021-2022 the Newtypes contributors.
 * See the project homepage at: https://newtypes.monix.io/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package monix.newtypes
package integrations

import pureconfig.error.FailureReason
import pureconfig._

/**
  * Derives a type-class instances for encoding and decoding HOCON
  * configuration files for any type implementing [[HasExtractor]] and 
  * [[HasBuilder]] type-class instances.
  * 
  * See [[https://pureconfig.github.io/docs/]].
  */
trait DerivedPureConfigConvert extends DerivedPureConfigReader with DerivedPureConfigWriter

/**
  * Derives `pureconfig.ConfigReader` type-class instances from
  * [[HasBuilder]] instances, with the purpose of deserializing
  * values from HOCON configuration files.
  * 
  * See [[https://pureconfig.github.io/]].
  */
trait DerivedPureConfigReader {
  implicit def pureConfigReader[T, S](implicit 
    builder: HasBuilder.Aux[T, S],
    reader: ConfigReader[S],
  ): ConfigReader[T] = {
    reader.emap { value =>
      builder.build(value) match {
        case value @ Right(_) => 
          value.asInstanceOf[Either[FailureReason, T]]
        case Left(msg) => 
          Left(
            new FailureReason {
              override def description: String = 
                msg.toReadableString
            }
          )
      }
    }
  }
}

/**
  * Derives `pureconfig.ConfigWriter` type-class instances from
  * [[HasExtractor]] instances, with the purpose of serializing
  * values from HOCON configuration files.
  * 
  * See [[https://pureconfig.github.io/]].
  */
trait DerivedPureConfigWriter {
  implicit def pureConfigWriter[T, S](implicit 
    extractor: HasExtractor.Aux[T, S],
    writer: ConfigWriter[S],
  ): ConfigWriter[T] = {
    (t: T) => writer.to(extractor.extract(t))
  }
}
