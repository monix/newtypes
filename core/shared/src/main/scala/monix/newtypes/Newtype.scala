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
  * Base class for defining newtypes that have no type parameters.
  *
  * This class does not define any "builder", or related [[NewBuilder]]
  * instance, as you're expected to provide one yourself.
  *
  * Usage sample: {{{ 
  *   type EmailAddress = EmailAddress.Type
  *
  *   object EmailAddress extends Newtype[String] { self =>
  *     def apply(value: String): Option[EmailAddress] = 
  *       if (value.contains("@")) 
  *         Some(unsafeBuild(value)) 
  *       else 
  *         None 
  * 
  *     // Recommended instance, but not required; 
  *     // use Newtype.Validated to get rid of this boilerplate ;-)
  *     implicit val builder: NewBuilder.Aux[EmailAddress, String] =
  *       new NewBuilder[EmailAddress] {
  *         type Source = String
  *         def build(v: String): Either[BuildFailure[String], EmailAddress] =
  *           apply(v) match {
  *             case None => Left(BuildFailure(EmailAddress, v, Some("missing @")))
  *             case Some(r) => Right(r)
  *           }
  *       }
  *   } 
  * }}}
  */
abstract class Newtype[Src] extends NewtypeTrait[Src]
