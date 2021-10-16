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
  def of[T](implicit ev: TypeInfo[T]) = ev
  
  def of[T](@unused value: T)(implicit ev: TypeInfo[T]) = ev

  implicit def forClassesK1[T[_], A](implicit 
    A: TypeInfo[A],
    T: ClassTag[T[_]],
  ): TypeInfo[T[A]] = {
    val t = TypeInfo.forClasses[T[_]]
    t.copy(typeParams = List(Some(A)))
  }

  implicit def forClassesK2[T[_, _], A1, A2](implicit 
    A1: TypeInfo[A1],
    A2: TypeInfo[A2],
    T: ClassTag[T[_, _]],
  ): TypeInfo[T[A1, A2]] = {
    val t = TypeInfo.forClasses[T[_, _]]
    t.copy(typeParams = List(Some(A1), Some(A2)))
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
        TypeInfo(
          typeName = c.getName().replaceAll("^" + Pattern.quote(c.getPackageName()) + "\\.", ""),
          typeLabel = c.getSimpleName(),
          packageName = c.getPackageName(),
          typeParams = List.fill(c.getTypeParameters().length)(None)
        )
    }
  }
}
