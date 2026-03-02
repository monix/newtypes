/*
 * Copyright (c) 2021-2024 Alexandru Nedelcu.
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

import io.circe.{KeyEncoder, KeyDecoder}

/** Derives a type-class instances for encoding and decoding JSON object keys
  * for any type implementing [[HasExtractor]] and [[HasBuilder]] type-class
  * instances.
  *
  * See
  * [[https://circe.github.io/circe/codecs/custom-codecs.html#custom-key-types]].
  */
trait DerivedCirceKeyCodec
    extends DerivedCirceKeyDecoder
    with DerivedCirceKeyEncoder

/** Derives a `io.circe.KeyDecoder` type-class instance for decoding a JSON
  * object key to any type with a [[HasBuilder]] instance.
  *
  * See
  * [[https://circe.github.io/circe/codecs/custom-codecs.html#custom-key-types]].
  */
trait DerivedCirceKeyDecoder {
  implicit def jsonKeyDecoder[T, S](implicit
      builder: HasBuilder.Aux[T, S],
      dec: KeyDecoder[S]
  ): KeyDecoder[T] = {
    jsonKeyDecode(_)
  }

  protected def jsonKeyDecode[T, S](s: String)(implicit
      builder: HasBuilder.Aux[T, S],
      dec: KeyDecoder[S]
  ): Option[T] = {
    dec.apply(s).flatMap {
      builder.build(_).toOption
    }
  }
}

/** Derives a `io.circe.KeyEncoder` type-class instance for encoding any type
  * with a [[HasExtractor]] instance to a JSON object key.
  *
  * See
  * [[https://circe.github.io/circe/codecs/custom-codecs.html#custom-key-types]].
  */
trait DerivedCirceKeyEncoder {
  implicit def jsonKeyEncoder[T, S](implicit
      extractor: HasExtractor.Aux[T, S],
      enc: KeyEncoder[S]
  ): KeyEncoder[T] = {
    jsonKeyEncode(_)
  }

  protected def jsonKeyEncode[T, S](a: T)(implicit
      extractor: HasExtractor.Aux[T, S],
      enc: KeyEncoder[S]
  ): String = {
    enc.apply(extractor.extract(a))
  }
}
