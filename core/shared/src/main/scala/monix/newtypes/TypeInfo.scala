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

import scala.annotation.unused
import scala.reflect.ClassTag
import java.util.regex.Pattern

final case class TypeInfo[T](
  typeName: String,
  typeLabel: String,
  packageName: String,
  typeParams: List[Option[TypeInfo[_]]],
) {
  def asHumanReadable: String =
    typeParams match {
      case Nil => typeLabel
      case _ =>
        s"$typeLabel[${typeParams.map(_.fold("_")(_.asHumanReadable)).mkString(", ")}]"
    }
}

object TypeInfo extends TypeInfoLevel0 {
  def of[T](implicit ev: TypeInfo[T]): TypeInfo[T] = ev

  def of[T](@unused value: T)(implicit ev: TypeInfo[T]): TypeInfo[T] = ev

  implicit def forClassesK1[T[_], A](implicit
    a: TypeInfo[A],
    tag: ClassTag[T[A]],
  ): TypeInfo[T[A]] = {
    val t = TypeInfo.forClasses[T[A]]
    t.copy(typeParams = List(Some(a)))
  }

  implicit def forClassesK2[T[_, _], A1, A2](implicit
    a1: TypeInfo[A1],
    a2: TypeInfo[A2],
    tag: ClassTag[T[A1, A2]],
  ): TypeInfo[T[A1, A2]] = {
    val t = TypeInfo.forClasses[T[A1, A2]](tag)
    t.copy(typeParams = List(Some(a1), Some(a2)))
  }

  implicit def forClassesK3[T[_, _, _], A1, A2, A3](implicit
    a1: TypeInfo[A1],
    a2: TypeInfo[A2],
    a3: TypeInfo[A3],
    tag: ClassTag[T[A1, A2, A3]],
  ): TypeInfo[T[A1, A2, A3]] = {
    val t = TypeInfo.forClasses[T[A1, A2, A3]](tag)
    t.copy(typeParams = List(Some(a1), Some(a2), Some(a3)))
  }

  implicit def forClassesK4[T[_, _, _, _], A1, A2, A3, A4](implicit
    a1: TypeInfo[A1],
    a2: TypeInfo[A2],
    a3: TypeInfo[A3],
    a4: TypeInfo[A4],
    tag: ClassTag[T[A1, A2, A3, A4]],
  ): TypeInfo[T[A1, A2, A3, A4]] = {
    val t = TypeInfo.forClasses[T[A1, A2, A3, A4]](tag)
    t.copy(typeParams = List(Some(a1), Some(a2), Some(a3), Some(a4)))
  }

  implicit def forClassesK5[T[_, _, _, _, _], A1, A2, A3, A4, A5](implicit
    a1: TypeInfo[A1],
    a2: TypeInfo[A2],
    a3: TypeInfo[A3],
    a4: TypeInfo[A4],
    a5: TypeInfo[A5],
    tag: ClassTag[T[A1, A2, A3, A4, A5]],
  ): TypeInfo[T[A1, A2, A3, A4, A5]] = {
    val t = TypeInfo.forClasses[T[A1, A2, A3, A4, A5]](tag)
    t.copy(typeParams = List(Some(a1), Some(a2), Some(a3), Some(a4), Some(a5)))
  }

  implicit def forClassesK6[T[_, _, _, _, _, _], A1, A2, A3, A4, A5, A6](implicit
    a1: TypeInfo[A1],
    a2: TypeInfo[A2],
    a3: TypeInfo[A3],
    a4: TypeInfo[A4],
    a5: TypeInfo[A5],
    a6: TypeInfo[A6],
    tag: ClassTag[T[A1, A2, A3, A4, A5, A6]],
  ): TypeInfo[T[A1, A2, A3, A4, A5, A6]] = {
    val t = TypeInfo.forClasses[T[A1, A2, A3, A4, A5, A6]](tag)
    t.copy(typeParams = List(Some(a1), Some(a2), Some(a3), Some(a4), Some(a5), Some(a6)))
  }
}

private[newtypes] sealed trait TypeInfoLevel0 {
  implicit def forClasses[T](implicit tag: ClassTag[T]): TypeInfo[T] = {
    def primitive(name: String) =
      TypeInfo[T](typeName = name, typeLabel = name, packageName = "scala", typeParams = Nil)

    val c = tag.runtimeClass
    c.getSimpleName match {
      case "int" => primitive("Int")
      case "long" => primitive("Long")
      case "short" => primitive("Short")
      case "byte" => primitive("Byte")
      case "float" => primitive("Float")
      case "double" => primitive("Double")
      case "boolean" => primitive("Boolean")
      case "char" => primitive("Char")
      case _ =>
        val pkg = Platform.getPackageName(c)
        TypeInfo(
          typeName = c.getName.replaceAll("^" + Pattern.quote(pkg) + "\\.", ""),
          typeLabel = c.getSimpleName,
          packageName = pkg,
          typeParams = List.fill(Platform.getTypeParamsCount(c))(None)
        )
    }
  }
}
