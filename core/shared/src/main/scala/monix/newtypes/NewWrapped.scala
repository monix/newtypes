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

/** Simple variant of [[Newtype]] that provides an `apply` builder.
  *
  * Such newtypes are meant for simple wrappers that don't do any validation.
  *
  * Usage: {{{
  *   type FullName = FullName.Type
  *
  *   object FullName extends NewtypeWrapped[String]
  *
  *   // Initializing
  *   val name: FullName = FullName("Alexandru Nedelcu")
  *   // Extracting the value when a string is needed:
  *   val nameStr: String = name.value
  *   assert(nameStr === "Alexandru Nedelcu")
  *
  *   // We can pattern-match too:
  *   name match {
  *     case FullName(nameStr) =>
  *       assert(nameStr === "Alexandru Nedelcu")
  *   }
  * }}}
  */
abstract class NewtypeWrapped[Src] extends Newtype[Src] with NewWrapped[Src]

/** Simple variant of [[Newsubtype]] that provides an `apply` builder.
  *
  * Such newsubtypes are meant for simple wrappers that don't do any validation.
  *
  * Usage: {{{
  *   type FullName = FullName.Type
  *
  *   object FullName extends NewsubtypeWrapped[String]
  *
  *   // Initializing
  *   val name: FullName = FullName("Alexandru Nedelcu")
  *   // No need to extract the value when a string is needed:
  *   assert(name === "Alexandru Nedelcu")
  *
  *   // We can pattern-match too:
  *   name match {
  *     case FullName(nameStr) =>
  *       assert(nameStr === "Alexandru Nedelcu")
  *   }
  * }}}
  */
abstract class NewsubtypeWrapped[Src] extends Newsubtype[Src] with NewWrapped[Src]

/**
  * Common implementation between [[NewtypeWrapped]] and [[NewsubtypeWrapped]].
  */
private[newtypes] trait NewWrapped[Src] { self: NewEncoding[Src] =>
  final def apply(x: Src): Type = unsafeCoerce(x)

  final def unapply[A](a: A)(implicit ev: A =:= Type): Some[Src] =
    Some(value(ev(a)))

  implicit final val builder: HasBuilder.Aux[Type, Src] =
    new HasBuilder[Type] {
      type Source = Src
      def build(value: Src) = Right(apply(value))
    }
}
