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

/**
  * Type-class.
  */
trait NewBuilder[NewT] {
  type Source

  def build(value: Source): Either[BuildFailure[Source], NewT]
}

object NewBuilder {
  type Aux[T, S] = NewBuilder[T] { type Source = S }

  def apply[T](implicit ev: NewBuilder[T]) = ev
}

final case class BuildFailure[Source](
  targetTypeName: String,
  value: Source,
)

object BuildFailure {
  def apply[Src](companion: NewEncoding[Src], value: Src): BuildFailure[Src] =
    BuildFailure(
      targetTypeName = getName(companion),
      value = value,
    )

  def apply[Src[_], A](companion: NewEncodingK[Src], value: Src[A]): BuildFailure[Src[A]] =
    BuildFailure(
      targetTypeName = getName(companion),
      value = value,
    )

  private[this] def getName(o: AnyRef): String =
    o.getClass().getSimpleName().replaceFirst("[$]$", "")
}
