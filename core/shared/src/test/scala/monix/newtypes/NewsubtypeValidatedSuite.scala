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

import org.scalatest.funsuite.AnyFunSuite
import monix.newtypes.TestUtils.illTyped

class NewsubtypeValidatedSuite extends AnyFunSuite {
  import NewsubtypeValidatedSuite._

  test("validation") {
    EmailAddress("noreply@alexn.org") match {
      case Right(ref @ EmailAddress(str)) =>
        assert(ref === EmailAddress.unsafe("noreply@alexn.org"))
        assert(str === "noreply@alexn.org")
      case other =>
        fail(s"Unexpected match: $other")
    }

    EmailAddress("not a valid email") match {
      case Left(BuildFailure(_, _)) => succeed
      case other => fail(s"Unexpected value: $other")
    }
  }

  test("unapply") {
    val ea1 = EmailAddress.unsafe("noreply@alexn.org")
    ea1 match {
      case ref @ EmailAddress(v) =>
        assert(ref === ea1)
        assert(v === ea1)
    }

    illTyped(
      """
      val ea2: Any = "noreply@alexn.org"
      ea2 match {
        case EmailAddress(v) => ()
        case _ => ()
      }
      """
    )

    illTyped(
      """
      val ea4: Any = Int.MaxValue
      ea4 match {
        case EmailAddress(_) => ()
        case _ => ()
      }
      """
    )
  }
}

object NewsubtypeValidatedSuite {
  type EmailAddress = EmailAddress.Type

  object EmailAddress extends NewsubtypeValidated[String] {
    def apply(v: String): Either[BuildFailure[Type], Type] =
      if (v.contains("@"))
        Right(unsafeCoerce(v))
      else
        Left(BuildFailure[EmailAddress]("missing @"))
  }
}
