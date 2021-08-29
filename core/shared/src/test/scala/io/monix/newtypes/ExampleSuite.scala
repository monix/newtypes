/*
 * Copyright (c) 2021 the Newtypes contributors.
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

package io.monix.newtypes

import cats.implicits._
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.scalacheck.Checkers

class ExampleSuite extends AnyFunSuite with Checkers {

  test("sample test") {
    val sum = Example.sumAll(List(1, 2, 3, 4))
    assert(sum == 1 + 2 + 3 + 4)
  }

  // Property-based testing via ScalaCheck
  test("sum up any list") {
    check { (l: List[Int]) =>
      Example.sumAll(l) == l.sum
    }
  }
}
