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

package monix.newtypes.integrations

import pureconfig._
import scala.annotation.nowarn
import com.typesafe.config.ConfigFactory

final case class Envelope[A](value: A)

@nowarn
object Envelope {
  implicit def reader[A: ConfigReader]: ConfigReader[Envelope[A]] = 
    ConfigReader.fromCursor[Envelope[A]] { cur =>
      for {
        objCur <- cur.asObjectCursor
        valueCur <- objCur.atKey("value")
        value <- ConfigReader[A].from(valueCur)
      } yield Envelope(value)
    } 
    
  implicit def writer[A: ConfigWriter]: ConfigWriter[Envelope[A]] =
    (value: Envelope[A]) => 
      ConfigFactory.empty()
        .withValue("value", ConfigWriter[A].to(value.value))
        .root()
}
