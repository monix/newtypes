/*
 * Copyright (c) 2021 the Newtypes contributors.
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

trait HasBuilder[Type] {
  type Source

  def build(value: Source): Either[BuildFailure[Source], Type]
}

object HasBuilder {
  type Aux[T, S] = HasBuilder[T] { type Source = S }

  def apply[T](implicit ev: HasBuilder[T]) = ev
}

final case class BuildFailure[Source](
  typeInfo: TypeInfo[_],
  value: Source,
  message: Option[String],
)
