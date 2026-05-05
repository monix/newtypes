/*
 * Copyright (c) 2021-2026 Alexandru Nedelcu.
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

import cats.{Eq, Hash, Order, Show}
import cats.syntax.all._
import munit.FunSuite
import monix.newtypes.NewtypeWrapped
import monix.newtypes.integrations.DerivedCatsInstancesSuite._

class DerivedCatsInstancesSuite extends FunSuite {
  private val testValues: List[Internal] = List("", "value", "another value")
  private val testPairs: List[(Internal, Internal)] = for {
    x <- testValues
    y <- testValues
  } yield (x, y)

  test("an instance of Eq exists") {
    implicitly[Eq[SomeNewtype]]
  }
  test("the Eq instances produce the same result") {
    testPairs.foreach { case (x, y) =>
      assertEquals(SomeNewtype(x).eqv(SomeNewtype(y)), x.eqv(y))
    }
  }

  test("an instance of Hash exists") {
    implicitly[Hash[SomeNewtype]]
  }
  test("the Hash instances produce the same result") {
    testValues.foreach { x =>
      assertEquals(SomeNewtype(x).hash, x.hash)
    }
  }

  test("an instance of Show exists") {
    implicitly[Show[SomeNewtype]]
  }
  test("the Show instances produce the same result") {
    testValues.foreach { x =>
      assertEquals(SomeNewtype(x).show, x.show)
    }
  }

  test("an instance of Order exists") {
    implicitly[Order[SomeNewtype]]
  }
  test("the Order instances produce the same result") {
    testPairs.foreach { case (x, y) =>
      assertEquals(
        implicitly[Order[SomeNewtype]].compare(SomeNewtype(x), SomeNewtype(y)),
        x.compare(y)
      )
    }
  }
}

object DerivedCatsInstancesSuite {
  final type Internal = String

  final type SomeNewtype = SomeNewtype.Type
  object SomeNewtype extends NewtypeWrapped[Internal] with DerivedCatsInstances
}
