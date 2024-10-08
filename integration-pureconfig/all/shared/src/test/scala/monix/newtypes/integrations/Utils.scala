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

package monix.newtypes.integrations

import pureconfig._
import com.typesafe.config._

object Utils {
  val renderOptions =
    ConfigRenderOptions
      .defaults()
      .setOriginComments(false)
      .setComments(false)
      .setFormatted(true)
      .setJson(true)

  def serialize[A](a: A)(implicit w: ConfigWriter[A]) =
    w.to(a).render(renderOptions)

  def deserialize[A](str: String)(implicit r: ConfigReader[A]) =
    ConfigSource.string(str).load[A]
}
