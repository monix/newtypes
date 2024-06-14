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
package integrations

import org.scalatest.funsuite.AnyFunSuite

class NewtypeKCirceEncoderSuite extends AnyFunSuite {
  import NewtypeKCirceCodecSuite._

  test("NewtypeK with DerivedPureConfigConvert") {
    val list = List("first", "second", "third")
    val value = NelInv(list.head, list.tail)
    val parsed = Utils
      .deserialize[Envelope[NelInv.Type[String]]](Utils.serialize(Envelope(value)))
      .map(_.value)
    assert(parsed == Right(value))
  }

  test("NewtypeCovariantK") {
    val list = List("first", "second", "third")
    val value = NelSubInv(list.head, list.tail)
    val parsed = Utils
      .deserialize[Envelope[NelSubInv.Type[String]]](Utils.serialize(Envelope(value)))
      .map(_.value)
    assert(parsed == Right(value))
  }
}

object NewtypeKCirceCodecSuite {
  object NelInv extends NewtypeK[List] with DerivedPureConfigConvert {
    def apply[A](first: A, rest: List[A]): Type[A] =
      unsafeCoerce(first :: rest)

    implicit final def builder[A: TypeInfo]: HasBuilder.Aux[Type[A], List[A]] =
      new HasBuilder[Type[A]] {
        type Source = List[A]

        override def build(value: List[A]): Either[BuildFailure[Type[A]], Type[A]] =
          value match {
            case head :: next => 
              Right(apply(head, next))
            case Nil => 
              Left(BuildFailure("Empty list"))
          }
      }
  }

  object NelSubInv extends NewsubtypeK[List] with DerivedPureConfigConvert {
    def apply[A](first: A, rest: List[A]): Type[A] =
      unsafeCoerce(first :: rest)

    implicit final def builder[A: TypeInfo]: HasBuilder.Aux[Type[A], List[A]] =
      new HasBuilder[Type[A]] {
        type Source = List[A]

        override def build(value: List[A]): Either[BuildFailure[Type[A]], Type[A]] =
          value match {
            case head :: next => 
              Right(apply(head, next))
            case Nil => 
              Left(BuildFailure("Empty list"))
          }
      }
  }
}
