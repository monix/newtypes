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

/** A validated [[Newtype]].
  *
  * This class is for defining newtypes with a builder that validates values.
  *
  * Example: {{{
  *   type EmailAddress = EmailAddress.Type
  *
  *   object EmailAddress extends NewtypeValidated[String] {
  *     def apply(v: String): Either[BuildFailure[String], EmailAddress] =
  *       if (v.contains("@"))
  *         Right(unsafeCoerce(v))
  *       else
  *         Left(BuildFailure(TypeInfo.of[EmailAddress], v, Some("missing @")))
  *   }
  * }}}
  */
abstract class NewtypeValidated[Src] extends Newtype[Src] with NewValidated[Src]

/** A validated [[Newsubtype]].
  *
  * This class is for defining newsubtypes with a builder that validates values.
  *
  * Example: {{{
  *   type EmailAddress = EmailAddress.Type
  *
  *   object EmailAddress extends NewsubtypeValidated[String] {
  *     def apply(v: String): Either[BuildFailure[String], EmailAddress] =
  *       if (v.contains("@"))
  *         Right(unsafeCoerce(v))
  *       else
  *         Left(BuildFailure(TypeInfo.of[EmailAddress], v, Some("missing @")))
  *   }
  * }}}
  */
abstract class NewsubtypeValidated[Src] extends Newsubtype[Src] with NewValidated[Src]

/**
  * Common implementation between [[NewtypeValidated]] and [[NewsubtypeValidated]].
  */
private[newtypes] trait NewValidated[Src] { self: NewEncoding[Src] =>
  def apply(value: Src): Either[BuildFailure[Src], Type]

  final def unsafe(value: Src): Type =
    unsafeCoerce(value)

  final def unapply[A](a: A)(implicit ev: A =:= Type): Some[Src] =
    Some(ev(a).value)

  implicit final val builder: HasBuilder.Aux[Type, Src] =
    new HasBuilder[Type] {
      type Source = Src
      def build(value: Src): Either[BuildFailure[Src], Type] = apply(value)
    }
}

