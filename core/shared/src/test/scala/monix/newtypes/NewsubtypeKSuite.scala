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

import cats.implicits._
import cats.{Eq, Monad, Traverse}
import org.scalatest.funsuite.AnyFunSuite
import monix.newtypes.TestUtils.illTyped

class NewsubtypeKSuite extends AnyFunSuite {
  import NewsubtypeKSuite._

  test("it compiles") {
    val n: Nel[String] = Nel("Alex", "John")
    assert(n.value == List("Alex", "John"))
  }

  test("newsubtype is 'translucent'") {
    val n: List[Any] = Nel("Alex", "John")
    assert(n == List("Alex", "John"))
  }

  test("newsubtype is type-safe") {
    illTyped("""
      val n: Nel[String] = List("Alex", "John")
      """)
  }

  test("derive") {
    assert(
      Eq.eqv(
        Nel("Alex", "John"),
        Nel("Alex", "John")
      ))
  }

  test("deriveK") {
    val list = Nel(Option("Alex"), Option("John")).sequence
    assert(list == Some(Nel("Alex", "John")))
  }

  test("custom unapply") {
    val nel = Nel(1, 2, 3, 4)
    nel match {
      case Nel(head, tail) =>
        assert(head == 1)
        assert(tail == List(2, 3, 4))
    }

    illTyped("""
    (Nel(1, 2, 3, 4): Any) match {
      case Nel(_, _) => ()
    }
    """)
  }

  test("invariance") {
    illTyped("""
    val nel: Nel[Any] = Nel[Int](1, 2, 3, 4)
    """)
  }
}

object NewsubtypeKSuite {
  type Nel[A] = Nel.Type[A]

  object Nel extends NewsubtypeK[List] {
    def apply[A](head: A, tail: A*): Nel[A] =
      unsafeCoerce(head :: tail.toList)

    def unapply[F[_], A](list: F[A])(implicit ev: F[A] =:= Nel[A]): Some[(A, List[A])] = {
      val l = value(list)
      Some((l.head, l.tail))
    }

    implicit def eq[A: Eq]: Eq[Nel[A]] =
      derive

    implicit val traverse: Traverse[Nel] =
      deriveK

    implicit val monad: Monad[Nel] =
      deriveK
  }
}
