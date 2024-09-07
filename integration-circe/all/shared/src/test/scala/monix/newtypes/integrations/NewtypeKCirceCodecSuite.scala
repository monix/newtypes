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
import io.circe.{ Encoder, Decoder }
import io.circe.parser.parse
import io.circe.syntax._

class NewtypeKCirceEncoderSuite extends AnyFunSuite {
  import NewtypeKCirceCodecSuite._

  test("NewtypeK with DerivedCirceEncoder") {
    val list = List("first", "second", "third")
    val value = NelInv0(list.head, list.tail)
    assert(list.asJson.noSpaces == value.asJson.noSpaces)
    val clsName = Encoder[NelInv0.Type[String]].getClass().getName
    assert(clsName.startsWith("monix.newtypes."))
  }

  test("NewtypeK with DerivedCirceCodec") {
    val list = List("first", "second", "third")
    val value = NelInv(list.head, list.tail)
    val parsed = parse(value.asJson.noSpaces).flatMap(_.as[NelInv.Type[String]])
    assert(parsed == Right(value))
    assert(Encoder[NelInv.Type[String]].getClass().getName.startsWith("monix.newtypes."))
    assert(Decoder[NelInv.Type[String]].getClass().getName.startsWith("monix.newtypes."))
  }

  test("NewtypeCovariantK") {
    val list = List("first", "second", "third")
    val value = NelCov(list.head, list.tail)
    assert(list.asJson.noSpaces == value.asJson.noSpaces)
    val clsName = Encoder[NelCov.Type[String]].getClass().getName
    assert(clsName.startsWith("monix.newtypes."))
  }

  test("NewsubtypeK") {
    val list = List("first", "second", "third")
    val value = NelSubInv(list.head, list.tail)

    val parsed = parse(value.asJson.noSpaces).flatMap(_.as[NelSubInv.Type[String]])
    assert(parsed == Right(value))
    val clsName1 = Encoder[NelSubInv.Type[String]].getClass().getName
    assert(clsName1.startsWith("io.circe."))
    val clsName2 = Decoder[NelSubInv.Type[String]].getClass().getName
    assert(clsName2.startsWith("monix.newtypes."))
  }

  test("NewsubtypeCovariantK") {
    val list = List("first", "second", "third")
    val value = NelSubCov(list.head, list.tail)
    assert(list.asJson.noSpaces == value.asJson.noSpaces)
    val clsName = Encoder[NelSubCov.Type[String]].getClass().getName
    assert(clsName.startsWith("io.circe."))
  }
}

object NewtypeKCirceCodecSuite {
  object NelInv0 extends NewtypeK[List] with DerivedCirceEncoder {
    def apply[A](first: A, rest: List[A]): Type[A] =
      unsafeCoerce(first :: rest)
  }

  object NelInv extends NewtypeK[List] with DerivedCirceCodec {
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

  object NelCov extends NewtypeCovariantK[List] with DerivedCirceCodec {
    def apply[A](first: A, rest: List[A]): Type[A] =
      unsafeCoerce(first :: rest)
  }

  object NelSubInv extends NewsubtypeK[List] with DerivedCirceCodec {
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

  object NelSubCov extends NewsubtypeCovariantK[List] {
    def apply[A](first: A, rest: List[A]): Type[A] =
      unsafeCoerce(first :: rest)
  }
}
