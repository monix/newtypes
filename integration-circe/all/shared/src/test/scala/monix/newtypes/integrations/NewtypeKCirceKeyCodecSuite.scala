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

import cats.Id
import io.circe.syntax._
import org.scalatest.funsuite.AnyFunSuite

class NewtypeKCirceKeyCodecSuite extends AnyFunSuite {
  import NewtypeKCirceKeyCodecSuite._

  test("NewtypeK has JSON key codec") {
    val expectedI = Map(NewId(12358) -> "")
    val expectedS = Map(NewId("12358") -> "")

    val jsonI = expectedI.asJson
    val jsonS = expectedS.asJson

    val receivedI2I = jsonI.as[Map[NewId[Int], String]]
    assert(receivedI2I == Right(expectedI))

    val receivedI2S = jsonI.as[Map[NewId[String], String]]
    assert(receivedI2S == Right(expectedS))

    val receivedS2I = jsonS.as[Map[NewId[Int], String]]
    assert(receivedS2I == Right(expectedI))

    val obtainedS2S = jsonS.as[Map[NewId[String], String]]
    assert(obtainedS2S == Right(expectedS))
  }

  // NOTE: Circe does not allow custom error messages in KeyDecoder.
  test("NewtypeK JSON key codec does validation") {
    val map = Map(NewId("ABCDEFG") -> "")
    val json = map.asJson
    val received = json.as[Map[NewId[Int], String]]
    assert(received.isLeft)
  }
}

object NewtypeKCirceKeyCodecSuite {
  type NewId[A] = NewId.Type[A]

  object NewId extends NewtypeK[Id] with DerivedCirceKeyCodec {
    def apply[A](a: A): NewId[A] = unsafeCoerce(a)

    implicit def builder[A]: HasBuilder.Aux[Type[A], A] =
      new HasBuilder[Type[A]] {
        type Source = A
        def build(value: A) = Right(apply(value))
      }
  }
}
