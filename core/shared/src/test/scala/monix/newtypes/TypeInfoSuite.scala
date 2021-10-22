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

package monix.newtypes

import org.scalatest.funsuite.AnyFunSuite

class TypeInfoSuite extends AnyFunSuite {
  def x = 1
  class Foo { def foo = x }

  test("String") {
    assert(TypeInfo.of[String] == TypeInfo[String](
      typeName = "String",
      typeLabel = "String",
      packageName = "java.lang",
      typeParams = Nil
    ))
  }

  test("primitives") {
    assert(TypeInfo.of[Int] == TypeInfo[Int](
      typeName = "Int",
      typeLabel = "Int",
      packageName = "scala",
      typeParams = Nil
    ))
    assert(TypeInfo.of[Int].asHumanReadable == "Int")

    assert(TypeInfo.of[Short] == TypeInfo[Short](
      typeName = "Short",
      typeLabel = "Short",
      packageName = "scala",
      typeParams = Nil
    ))
    assert(TypeInfo.of[Short].asHumanReadable == "Short")

    assert(TypeInfo.of[Byte] == TypeInfo[Byte](
      typeName = "Byte",
      typeLabel = "Byte",
      packageName = "scala",
      typeParams = Nil
    ))
    assert(TypeInfo.of[Byte].asHumanReadable == "Byte")

    assert(TypeInfo.of[Char] == TypeInfo[Char](
      typeName = "Char",
      typeLabel = "Char",
      packageName = "scala",
      typeParams = Nil
    ))
    assert(TypeInfo.of[Char].asHumanReadable == "Char")

    assert(TypeInfo.of[Float] == TypeInfo[Float](
      typeName = "Float",
      typeLabel = "Float",
      packageName = "scala",
      typeParams = Nil
    ))
    assert(TypeInfo.of[Float].asHumanReadable == "Float")

    assert(TypeInfo.of[Double] == TypeInfo[Double](
      typeName = "Double",
      typeLabel = "Double",
      packageName = "scala",
      typeParams = Nil
    ))
    assert(TypeInfo.of[Double].asHumanReadable == "Double")

    assert(TypeInfo.of[Long] == TypeInfo[Long](
      typeName = "Long",
      typeLabel = "Long",
      packageName = "scala",
      typeParams = Nil
    ))
    assert(TypeInfo.of[Long].asHumanReadable == "Long")
  }

  test("anonymous class") {
    val cl = new Runnable { def foo() = (); def run() = () }
    assert(TypeInfo.of(cl) == TypeInfo[Runnable](
      typeName = "Runnable",
      typeLabel = "Runnable",
      packageName = "java.lang",
      typeParams = Nil
    ))
    assert(TypeInfo.of(cl).asHumanReadable == "Runnable")
  }

  test("inner class") {
    assert(TypeInfo.of[Foo] == TypeInfo(
      typeName = "TypeInfoSuite$Foo",
      typeLabel = "Foo",
      packageName = "monix.newtypes",
      typeParams = Nil
    ))
    assert(TypeInfo.of[Foo].asHumanReadable == "Foo")
  }

  test("inner static class") {
    assert(TypeInfo.of[TypeInfoSuite.FooStatic] == TypeInfo(
      typeName = "TypeInfoSuite$FooStatic",
      typeLabel = "FooStatic",
      packageName = "monix.newtypes",
      typeParams = Nil
    ))
    assert(TypeInfo.of[TypeInfoSuite.FooStatic].asHumanReadable == "FooStatic")
  }

  test("List[String]") {
    assert(TypeInfo.of[List[String]] == TypeInfo(
      typeName = "List",
      typeLabel = "List",
      packageName = "scala.collection.immutable",
      typeParams = List(Some(TypeInfo.of[String]))
    ))
    assert(TypeInfo.of[List[String]].asHumanReadable == "List[String]")
  }

  test("Either[String, Int]") {
    assert(TypeInfo.of[Either[String, Int]] == TypeInfo(
      typeName = "Either",
      typeLabel = "Either",
      packageName = "scala.util",
      typeParams = List(Some(TypeInfo.of[String]), Some(TypeInfo.of[Int]))
    ))
    assert(TypeInfo.of[Either[String, Int]].asHumanReadable == "Either[String, Int]")
  }

  test("Tuple3[String, Int, Float]") {
    assert(TypeInfo.of[Tuple3[String, Int, Float]] == TypeInfo(
      typeName = "Tuple3",
      typeLabel = "Tuple3",
      packageName = "scala",
      typeParams = List(Some(TypeInfo.of[String]), Some(TypeInfo.of[Int]), Some(TypeInfo.of[Float]))
    ))
    assert(TypeInfo.of[Tuple3[String, Int, Float]].asHumanReadable == "Tuple3[String, Int, Float]")
  }

  test("Tuple4[String, Int, Float, Byte]") {
    assert(TypeInfo.of[Tuple4[String, Int, Float, Byte]] == TypeInfo(
      typeName = "Tuple4",
      typeLabel = "Tuple4",
      packageName = "scala",
      typeParams = List(
        Some(TypeInfo.of[String]),
        Some(TypeInfo.of[Int]),
        Some(TypeInfo.of[Float]),
        Some(TypeInfo.of[Byte])
      )
    ))
    assert(TypeInfo.of[Tuple4[String, Int, Float, Byte]].asHumanReadable ==
      "Tuple4[String, Int, Float, Byte]")
  }

  test("Tuple5[String, Int, Float, Byte, Double]") {
    assert(TypeInfo.of[Tuple5[String, Int, Float, Byte, Double]] == TypeInfo(
      typeName = "Tuple5",
      typeLabel = "Tuple5",
      packageName = "scala",
      typeParams = List(
        Some(TypeInfo.of[String]),
        Some(TypeInfo.of[Int]),
        Some(TypeInfo.of[Float]),
        Some(TypeInfo.of[Byte]),
        Some(TypeInfo.of[Double]),
      )
    ))
    assert(TypeInfo.of[Tuple5[String, Int, Float, Byte, Double]].asHumanReadable ==
      "Tuple5[String, Int, Float, Byte, Double]")
  }

  test("Tuple6[String, Int, Float, Byte, Double, Long]") {
    assert(TypeInfo.of[Tuple6[String, Int, Float, Byte, Double, Long]] == TypeInfo(
      typeName = "Tuple6",
      typeLabel = "Tuple6",
      packageName = "scala",
      typeParams = List(
        Some(TypeInfo.of[String]),
        Some(TypeInfo.of[Int]),
        Some(TypeInfo.of[Float]),
        Some(TypeInfo.of[Byte]),
        Some(TypeInfo.of[Double]),
        Some(TypeInfo.of[Long]),
      )
    ))
    assert(TypeInfo.of[Tuple6[String, Int, Float, Byte, Double, Long]].asHumanReadable ==
      "Tuple6[String, Int, Float, Byte, Double, Long]")
  }
}

object TypeInfoSuite {
  class FooStatic {}
}
