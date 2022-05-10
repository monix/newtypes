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

/**
  * For building newtypes over types that 
  * have an invariant type parameter (higher-kinded types).
  *
  * Example: {{{ 
  *   // Only needed for type-class derivation 
  *   import cats._ 
  *   import cats.implicits._
  *
  *   type Nel[A] = Nel.Type[A]
  *
  *   object Nel extends NewtypeK[List] { 
  *     def apply[A](head: A, tail: A*): Nel[A] = 
  *       unsafeCoerce(head :: tail.toList)
  *
  *     def unapply[F[_], A](list: F[A])(
  *       implicit ev: F[A] =:= Nel[A]
  *     ): Some[(A, List[A])] = { 
  *       val l = value(list)
  *       Some((l.head, l.tail)) 
  *     }
  *
  *     implicit def eq[A: Eq]: Eq[Nel[A]] = 
  *       derive
  *     implicit val traverse: Traverse[Nel] = 
  *       deriveK
  *     implicit val monad: Monad[Nel] = 
  *       deriveK
  *   } 
  * }}}
  *
  * NOTE: the type-parameter is invariant. See [[NewtypeCovariantK]]
  * for working with covariant type parameters.
  */
abstract class NewtypeK[Src[_]] extends NewtypeTraitK[Src]

/**
  * For building newtypes over types that have a covariant type parameter 
  * (higher-kinded types).
  *
  * Example: {{{ 
  *   // Only needed for type-class derivation 
  *   import cats._ 
  *   import cats.implicits._
  *
  *   type NonEmptyList[A] = NonEmptyList.Type[A]
  *
  *   object NonEmptyList extends NewtypeCovariantK[List] { 
  *     def apply[A](head: A, tail: A*): NonEmptyList[A] = 
  *       unsafeCoerce(head :: tail.toList)
  *
  *     def unapply[F[_], A](list: F[A])(
  *       implicit ev: F[A] =:= NonEmptyList[A]
  *     ): Some[(A, List[A])] = { 
  *       val l = value(list)
  *       Some((l.head, l.tail)) 
  *     }
  *
  *     implicit def eq[A: Eq]: Eq[NonEmptyList[A]] = 
  *       derive
  *     implicit val traverse: Traverse[NonEmptyList] = 
  *       deriveK
  *     implicit val monad: Monad[NonEmptyList] = 
  *       deriveK
  *   } 
  * }}}
  *
  * NOTE: the type-parameter is covariant. 
  * 
  * @see [[NewtypeK]] for working with invariance.
  */
abstract class NewtypeCovariantK[Src[+_]] extends NewtypeCovariantTraitK[Src]
