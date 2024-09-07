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

import io.circe.syntax._
import io.circe.parser
import org.scalatest.funsuite.AnyFunSuite

class NewtypeCirceCodecSuite extends AnyFunSuite {
  import NewtypeCirceCodecSuite._

  test("NewtypeWrapped has JSON codec") {
    val value = FirstName("Alex")
    val json = value.asJson.noSpaces
    val received = parser.parse(json).flatMap(_.as[FirstName])
    assert(received == Right(value))
  }

  test("NewtypeValidated has JSON codec") {
    EmailAddress("noreply@alexn.org") match {
      case Left(failure) =>
        fail(failure.toString())
      case Right(value) =>
        val json = value.asJson.noSpaces
        val received = parser.parse(json).flatMap(_.as[EmailAddress])
        assert(received == Right(value))
    }
  }

  test("NewtypeValidated decoder does validation") {
    val value = parser.parse("\"Not an email\"").flatMap(_.as[EmailAddress])
    assert(value.isLeft)
    val msg = value.left.map(_.getMessage()).swap.getOrElse("")
    assert(msg.contains("Invalid EmailAddress — Not an email address"))
  }
}

object NewtypeCirceCodecSuite {
  type FirstName = FirstName.Type
  object FirstName extends NewtypeWrapped[String] with DerivedCirceCodec

  type EmailAddress = EmailAddress.Type
  object EmailAddress extends NewtypeValidated[String] with DerivedCirceCodec {
    def apply(value: String): Either[BuildFailure[Type], Type] =
      if (value.contains("@")) Right(unsafe(value))
      else Left(BuildFailure("Not an email address"))
  }
}
