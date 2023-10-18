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

import io.circe.{ Encoder, Decoder, HCursor }
import io.circe.DecodingFailure
import io.circe.Json

/**
  * Derives a type-class instances for encoding and decoding JSON 
  * for any type implementing [[HasExtractor]] and [[HasBuilder]] 
  * type-class instances.
  * 
  * See [[https://circe.github.io/circe/codecs/custom-codecs.html]].
  * 
  * WARN: we are not deriving `io.circe.Codec`, as it's an anti-pattern
  * due to making code less generic. Also, due to Scala's subtyping it
  * can lead to conflicts.
  */
trait DerivedCirceCodec extends DerivedCirceEncoder with DerivedCirceDecoder 

/**
  * Derives a `io.circe.Decoder` type-class instance for decoding
  * a JSON value to any type with a [[HasBuilder]] instance.
  * 
  * See [[https://circe.github.io/circe/codecs/custom-codecs.html]].
  */
trait DerivedCirceDecoder {
  implicit def jsonDecoder[T, S](implicit 
    builder: HasBuilder.Aux[T, S],
    dec: Decoder[S],
  ): Decoder[T] = {
    jsonDecode(_)
  }

  protected def jsonDecode[T, S](c: HCursor)(implicit
    builder: HasBuilder.Aux[T, S],
    dec: Decoder[S],
  ): Decoder.Result[T] =
    dec.apply(c).flatMap { value =>
      builder.build(value) match {
        case value @ Right(_) => 
          value.asInstanceOf[Either[DecodingFailure, T]]
        case Left(failure) => 
          val msg = failure.message.fold("")(m => s" — $m")
          Left(DecodingFailure(
            s"Invalid ${failure.typeInfo.typeLabel}$msg", 
            c.history
          ))
      }
    }
}

/**
  * Derives a `io.circe.Encoder` type-class instance for encoding 
  * any type with a [[HasExtractor]] instance to JSON, via Circe.
  * 
  * See [[https://circe.github.io/circe/codecs/custom-codecs.html]].
  */
trait DerivedCirceEncoder {
  implicit def jsonEncoder[T, S](implicit 
    extractor: HasExtractor.Aux[T, S],
    enc: Encoder[S],
  ): Encoder[T] = {
    jsonEncode(_)
  }

  protected def jsonEncode[T, S](a: T)(implicit 
    extractor: HasExtractor.Aux[T, S],
    enc: Encoder[S],
  ): Json = {
    enc.apply(extractor.extract(a))
  }
}
