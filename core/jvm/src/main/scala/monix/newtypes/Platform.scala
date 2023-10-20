/*
 * Copyright (c) 2021-2023 the Newtypes contributors.
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

import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.{util => ju}
import scala.util.control.NonFatal

private[newtypes] object Platform {
  def getPackageName(cls: Class[_]): String =
    if (getPackageName != null) {
      getPackageName
        .invokeWithArguments(ju.Arrays.asList(cls))
        .asInstanceOf[String]
    } else {
      cls.getName.replaceAll("^(.*?)\\.[^.]+$", "$1")
    }

  def getTypeParamsCount(cls: Class[_]): Int =
    cls.getTypeParameters.length

  private[this] val getPackageName =
    try {
      val publicLookup = MethodHandles.publicLookup()
      val mt = MethodType.methodType(classOf[String])
      publicLookup.findVirtual(classOf[Class[_]], "getPackageName", mt)
    } catch {
      case _: NoSuchMethodException | _: SecurityException | 
        _: NullPointerException | _: IllegalAccessException | NonFatal(_) =>
        null
    }
}
