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

/** $newsubtypeBaseDescription */
abstract class Newsubtype[Src] { companion =>
  opaque type Type <: Src = Src

  // TODO: add inline after when this PR ships in Scala compiler
  // https://github.com/lampepfl/dotty/pull/12815

  extension (self: Type) {
    final def value: Src = self
  }

  protected final def unsafeCoerce(value: Src): Type =
    value

  protected final def derive[F[_]](implicit ev: F[Src]): F[Type] =
    ev
}
