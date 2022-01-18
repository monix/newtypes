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
package integrations

import io.circe.{ Encoder, Decoder }
import io.circe.syntax._
import io.circe.parser
import org.scalatest.funsuite.AnyFunSuite

class NewsubtypeCirceCodecSuite extends AnyFunSuite {
  import NewsubtypeCirceCodecSuite._

  test("NewsubtypeWrapped[String] with DerivedCirceEncoder") {
    val value = FirstName0("Alex")
    val json = value.asJson.noSpaces
    assert(json == value.value.asJson.noSpaces)
    assert(Encoder[FirstName0].getClass().getName.startsWith("monix.newtypes."))
  }

  test("NewsubtypeWrapped[String] with DerivedCirceDecoder") {
    val value = FirstName1("Alex")
    val json = value.value.asJson.noSpaces
    val received = parser.parse(json).flatMap(_.as[FirstName1])
    assert(received == Right(value))
    assert(Decoder[FirstName1].getClass().getName.startsWith("monix.newtypes."))
  }

  test("NewsubtypeWrapped[String] with DerivedCirceCodec") {
    val value = FirstName("Alex")
    val json = value.asJson.noSpaces
    val received = parser.parse(json).flatMap(_.as[FirstName])
    assert(received == Right(value))
    assert(Encoder[FirstName].getClass().getName.startsWith("monix.newtypes."))
    assert(Decoder[FirstName].getClass().getName.startsWith("monix.newtypes."))
  }

  test("NewsubtypeValidated has JSON codec") {
    EmailAddress("noreply@alexn.org") match {
      case Left(failure) =>
        fail(failure.toString())
      case Right(value) =>
        val json = value.asJson.noSpaces
        val received = parser.parse(json).flatMap(_.as[EmailAddress])
        assert(received == Right(value))
    }
  }

  test("NewsubtypeValidated decoder does validation") {
    val value = parser.parse("\"Not an email\"").flatMap(_.as[EmailAddress])
    assert(value.isLeft)
    assert(value.left.map(_.getMessage()) == Left("Invalid EmailAddress — Not an email address"))
  }
}

object NewsubtypeCirceCodecSuite {
  type FirstName0 = FirstName.Type
  object FirstName0 extends NewsubtypeWrapped[String] with DerivedCirceEncoder

  type FirstName1 = FirstName.Type
  object FirstName1 extends NewsubtypeWrapped[String] with DerivedCirceDecoder

  type FirstName = FirstName.Type
  object FirstName extends NewsubtypeWrapped[String] with DerivedCirceCodec

  type EmailAddress = EmailAddress.Type
  object EmailAddress extends NewsubtypeValidated[String] with DerivedCirceCodec {
    def apply(value: String): Either[BuildFailure[Type], Type] =
      if (value.contains("@")) Right(unsafe(value))
      else Left(BuildFailure("Not an email address"))
  }
}
