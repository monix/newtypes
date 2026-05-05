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

trait DerivedCatsInstances extends LowPriorityDerivedCatsInstances {
  implicit def catsOrder[T, S](implicit extractor: HasExtractor.Aux[T, S], orderS: Order[S]): Order[T] =
    new Order[T] {
      override def compare(x: T, y: T): Int = orderS.compare(extractor.extract(x), extractor.extract(y))
    }

  implicit def catsShow[T, S](implicit extractor: HasExtractor.Aux[T, S], showS: Show[S]): Show[T] =
    new Show[T] {
      override def show(t: T): String = showS.show(extractor.extract(t))
    }
}

private[integrations] trait LowPriorityDerivedCatsInstances {
  implicit def catsEq[T, S](implicit extractor: HasExtractor.Aux[T, S], eqS: Eq[S]): Eq[T] =
    new Eq[T] {
      override def eqv(x: T, y: T): Boolean = eqS.eqv(extractor.extract(x), extractor.extract(y))
    }

  implicit def catsHash[T, S](implicit extractor: HasExtractor.Aux[T, S], hashS: Hash[S]): Hash[T] =
    new Hash[T] {
      override def eqv(x: T, y: T): Boolean = hashS.eqv(extractor.extract(x), extractor.extract(y))
      override def hash(x: T): Int          = hashS.hash(extractor.extract(x))
    }
}
