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

import cats.Eq
import org.scalatest.funsuite.AnyFunSuite
import monix.newtypes.TestUtils.illTyped

class NewsubtypeWrappedSuite extends AnyFunSuite {
  import NewsubtypeWrappedSuite._

  test("it compiles") {
    val n: YourName = YourName("Alex")
    assert(n.value == "Alex")
    assert(YourName.value(n) == "Alex")
  }

  test("newsubtype is 'translucent'") {
    val n: String = YourName("Alex")
    assert(n == "Alex")
  }

  test("newsubtype is type-safe") {
    illTyped("""
      val n: YourName = "Alex"
      """)
  }

  test("derive") {
    assert(Eq[YourName].eqv(YourName("Alex"), YourName("Alex")))
  }

  test("unapply") {
    val n = YourName("Alex")
    n match {
      case ref @ YourName(src) =>
        assert(ref === n)
        assert(src === n.value)
    }

    illTyped(
      """
      "Alex" match { case YourName(_) => () }
      """
    )

    illTyped(
      """
      (1: Any) match { case YourName(_) => () }
      """
    )

    illTyped(
      """
      (YourName("Alex"): Any) match { case YourName(_) => () }
      """
    )
  }
}

object NewsubtypeWrappedSuite {
  import cats.implicits._

  type YourName = YourName.Type
  object YourName extends NewsubtypeWrapped[String] {
    implicit val eq: Eq[YourName] = derive
  }
}
