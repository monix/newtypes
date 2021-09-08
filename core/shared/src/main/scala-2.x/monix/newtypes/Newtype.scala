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

/** $newtypeBaseDescription */
abstract class Newtype[Src] extends CoreScalaDoc { companion =>
  type Base = Any { type NewType$base }
  trait Tag extends Any
  type Type <: Base with Tag

  @inline final def value(x: Type): Src =
    x.asInstanceOf[Src]

  implicit final class Ops(val self: Type) {
    @inline def value: Src = companion.value(self)
  }

  @inline protected final def unsafeCoerce(value: Src): Type =
    value.asInstanceOf[Type]

  @inline protected final def derive[F[_]](implicit ev: F[Src]): F[Type] =
    ev.asInstanceOf[F[Type]]

  protected def typeName: String =
    getClass().getSimpleName().replaceFirst("[$]$", "")

  implicit final val extractor: NewExtractor.Aux[Type, Src] =
    new NewExtractor[Type] {
      type Source = Src
      def extract(value: Type) = value.value
    }
}
