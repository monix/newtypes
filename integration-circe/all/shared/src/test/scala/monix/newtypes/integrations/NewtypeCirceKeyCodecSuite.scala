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
import org.scalatest.EitherValues
import org.scalatest.funsuite.AnyFunSuite

class NewtypeCirceKeyCodecSuite extends AnyFunSuite with EitherValues {
  import NewtypeCirceKeyCodecSuite._

  test("NewtypeWrapped has JSON key codec") {
    val originalMap = Map(12347 -> "")
    val expectedMap = Map(Text("12347") -> "")

    // Check KeyEncoder.
    val expectedJson = originalMap.asJson
    val receivedJson = expectedMap.asJson
    assert(receivedJson == expectedJson)

    // Check KeyDecoder.
    val receivedMap = expectedJson.as[Map[Text, String]]
    assert(receivedMap == Right(expectedMap))
  }

  test("NewtypeValidated has JSON key codec") {
    val originalMap = Map(0x7f -> "")
    val expectedMap = PosByte(0x7f).map(k => Map(k -> "")).value

    // Check KeyEncoder.
    val expectedJson = originalMap.asJson
    val receivedJson = expectedMap.asJson
    assert(receivedJson == expectedJson)

    // Check KeyDecoder.
    val receivedMap = expectedJson.as[Map[PosByte, String]]
    assert(receivedMap == Right(expectedMap))
  }

  // NOTE: Circe does not allow custom error messages in KeyDecoder.
  test("NewtypeValidated JSON key decoder does validation") {
    val received = Map(0 -> "").asJson.as[Map[PosByte, String]]
    assert(received.isLeft)
  }
}

object NewtypeCirceKeyCodecSuite {
  type Text = Text.Type
  object Text extends NewtypeWrapped[String] with DerivedCirceKeyCodec

  type PosByte = PosByte.Type
  object PosByte extends NewtypeValidated[Byte] with DerivedCirceKeyCodec {
    override def apply(value: Byte): Either[BuildFailure[Type], Type] =
      Either.cond(
        value > 0,
        unsafe(value),
        BuildFailure() // message is not used by KeyDecoder anyway
      )
  }
}
