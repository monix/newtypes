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

class NewsubtypePureConfigCodecSuite extends AnyFunSuite {
  import NewsubtypeCirceCodecSuite._

  test("NewsubtypeWrapped has JSON codec") {
    val value = FirstName("Alex")
    val serialized = Utils.serialize(Envelope(value))
    val deserialized = Utils.deserialize[Envelope[FirstName]](serialized)
    assert(deserialized.map(_.value) == Right(value))
  }

  test("NewsubtypeValidated has JSON codec") {
    val value = EmailAddress.unsafe("noreply@alexn.org")
    val serialized = Utils.serialize(Envelope(value))
    val deserialized = Utils.deserialize[Envelope[EmailAddress]](serialized)
    assert(deserialized.map(_.value) == Right(value))
  }

  test("NewsubtypeValidated decoder does validation") {
    val value = EmailAddress.unsafe("Not an email")
    val serialized = Utils.serialize(Envelope(value))
    val deserialized = Utils.deserialize[Envelope[EmailAddress]](serialized)
    assert(deserialized.isLeft)
    assert(deserialized.left.map(_.head.description) == Left("Invalid EmailAddress — Not an email address"))
  }
}

object NewsubtypeCirceCodecSuite {
  type FirstName = FirstName.Type
  object FirstName extends NewsubtypeWrapped[String] with DerivedPureConfigConvert

  type EmailAddress = EmailAddress.Type
  object EmailAddress extends NewsubtypeValidated[String] with DerivedPureConfigConvert {
    def apply(value: String): Either[BuildFailure[Type], Type] = 
      if (value.contains("@")) Right(unsafe(value))
      else Left(BuildFailure("Not an email address"))
  }
}
