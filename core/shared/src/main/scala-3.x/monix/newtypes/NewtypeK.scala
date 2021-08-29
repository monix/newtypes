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
abstract class NewtypeK[Src[_]] {
  opaque type Type[A] = Src[A]

  // TODO: add inline after when this PR ships in Scala compiler
  // https://github.com/lampepfl/dotty/pull/12815

  extension [A](self: Type[A]) {
    def value: Src[A] = self
  }

  protected def unsafeCoerce[A](value: Src[A]): Type[A] =
    value

  protected final def derive[F[_], A](using ev: F[Src[A]]): F[Type[A]] =
    ev

  protected final def deriveK[F[_[_]]](using ev: F[Src]): F[Type] =
    ev
}

/** $newtypeCovariantKDescription */
abstract class NewtypeCovariantK[Src[+_]] extends NewtypeK[Src] {
  override opaque type Type[+A] = Src[A]
}
