/*
 * Copyright (c) 2021-2023 the Newtypes contributors.
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

/**
  * Type-class used for encoding types.
  *
  * Used for automatically deriving encoders (e.g. to JSON)
  * for newtypes.
  *
  * @see [[HasBuilder]] for deriving decoders.
  */
trait HasExtractor[Type] {
  type Source

  def extract(value: Type): Source
}

object HasExtractor {
  type Aux[T, S] = HasExtractor[T] { type Source = S }
}
