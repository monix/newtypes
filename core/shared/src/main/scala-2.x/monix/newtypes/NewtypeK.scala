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

/** $newtypeKDescription */
abstract class NewtypeK[Src[_]] extends CoreScalaDoc { companion =>
  type Base = Any { type NewType$base }
  trait Tag extends Any
  type Type[A] <: Base with Tag

  @inline final def value[A](x: Type[A]): Src[A] =
    x.asInstanceOf[Src[A]]

  implicit final class Ops[A](val self: Type[A]) {
    @inline final def value: Src[A] = companion.value(self)
  }

  @inline
  protected final def unsafeCoerce[A](value: Src[A]): Type[A] =
    value.asInstanceOf[Type[A]]

  @inline
  protected final def derive[F[_], A](implicit ev: F[Src[A]]): F[Type[A]] =
    ev.asInstanceOf[F[Type[A]]]

  @inline
  protected final def deriveK[F[_[_]]](implicit ev: F[Src]): F[Type] =
    ev.asInstanceOf[F[Type]]
}

/** $newtypeCovariantKDescription */
abstract class NewtypeCovariantK[Src[+_]] extends NewtypeK[Src] {
  override type Type[+A] <: Base with Tag
}
