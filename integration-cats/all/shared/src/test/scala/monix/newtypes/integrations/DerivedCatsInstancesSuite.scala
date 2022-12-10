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

import cats.{Eq, Hash, Order, Show}
import cats.syntax.all._
import monix.newtypes.NewtypeWrapped
import monix.newtypes.integrations.DerivedCatsInstancesSuite._
import org.scalacheck.Prop._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class DerivedCatsInstancesSuite extends AnyFunSuite with ScalaCheckPropertyChecks {

  test("an instance of Eq exists") {
    implicitly[Eq[SomeNewtype]]
  }
  test("the Eq instances produce the same result") {
    forAll { (x: Internal, y: Internal) =>
      x.eqv(y) == SomeNewtype(x).eqv(SomeNewtype(y))
    }
  }

  test("an instance of Hash exists") {
    implicitly[Hash[SomeNewtype]]
  }
  test("the Hash instances produce the same result") {
    forAll { (x: Internal) =>
      x.hash == SomeNewtype(x).hash
    }
  }

  test("an instance of Show exists") {
    implicitly[Show[SomeNewtype]]
  }
  test("the Show instances produce the same result") {
    forAll { (x: Internal) =>
      x.show == SomeNewtype(x).show
    }
  }

  test("an instance of Order exists") {
    assert(SomeNewtype.catsOrder[SomeNewtype, Internal].isInstanceOf[Order[_]])
  }
  test("the Order instances produce the same result") {
    forAll { (x: Internal, y: Internal) =>
      x.compare(y) == SomeNewtype.catsOrder[SomeNewtype, Internal].compare(SomeNewtype(x), SomeNewtype(y))
    }
  }
}

object DerivedCatsInstancesSuite {
  final type Internal = String

  final type SomeNewtype = SomeNewtype.Type
  object SomeNewtype extends NewtypeWrapped[Internal] with DerivedCatsInstances
}
