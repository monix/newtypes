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

/** A validated [[Newsubtype]].
  *
  * This class is for defining newsubtypes with a builder that validates values.
  *
  * Example: {{{ 
  *   type EmailAddress = EmailAddress.Type
  *
  *   object EmailAddress extends NewtypeValidated[String, Exception] { 
  *     def apply(v: String): Either[Exception, EmailAddress] = 
  *       if (v.contains("@")) 
  *         Right(unsafeCoerce(v)) 
  *       else 
  *         Left(new IllegalArgumentException("Not a valid email")) 
  *   } 
  * }}}
  */
abstract class NewsubtypeValidated[Src, E] extends Newsubtype[Src] {
  def apply(value: Src): Either[E, Type]

  final def unsafe(value: Src): Type =
    unsafeCoerce(value)

  final def unapply[A](a: A)(implicit ev: A =:= Type): Some[Src] =
    Some(ev(a).value)
}
