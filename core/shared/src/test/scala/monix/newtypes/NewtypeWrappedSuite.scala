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

import cats.Eq
import org.scalatest.funsuite.AnyFunSuite
import monix.newtypes.TestUtils.illTyped

class NewtypeWrappedSuite extends AnyFunSuite {
  import NewtypeWrappedSuite._

  test("it compiles") {
    val n: MyName = MyName("Alex")
    assert(n.value == "Alex")
    assert(MyName.value(n) == "Alex")
  }

  test("newtype is not 'translucent'") {
    illTyped("""
      val n: String = MyName("Alex")
      """)
  }

  test("newtype is type-safe") {
    illTyped("""
      val n: MyName = "Alex"
      """)
  }

  test("derive") {
    assert(Eq[MyName].eqv(MyName("Alex"), MyName("Alex")))
  }

  test("unapply") {
    val n = MyName("Alex")
    n match {
      case ref @ MyName(src) =>
        assert(ref === n)
        assert(src === n.value)
    }

    illTyped(
      """
      "Alex" match { case MyName(_) => () }
      """
    )

    illTyped(
      """
      (1: Any) match { case MyName(_) => () }
      """
    )

    illTyped(
      """
      (MyName("Alex"): Any) match { case MyName(_) => () }
      """
    )
  }
}

object NewtypeWrappedSuite {
  import cats.implicits._

  type MyName = MyName.Type
  object MyName extends NewtypeWrapped[String] {
    implicit val eq: Eq[MyName] = derive
  }
}
