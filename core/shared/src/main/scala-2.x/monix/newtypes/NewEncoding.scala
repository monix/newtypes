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

import scala.reflect.ClassTag

/**
  * Scala 2 specific encoding for new-types — common trait to use
  * in [[monix.newtypes.Newtype]] and [[monix.newtypes.Newsubtype]].
  */
private trait NewEncoding[Src] {
  type Base
  trait Tag extends Any
  type Type <: Base with Tag

  @inline final def value(x: Type): Src =
    x.asInstanceOf[Src]

  implicit final class Ops(val self: Type) {
    @inline def value: Src = NewEncoding.this.value(self)
  }

  @inline protected final def extract(value: Type): Src =
    value.asInstanceOf[Src]

  @inline protected final def unsafeCoerce(value: Src): Type =
    value.asInstanceOf[Type]

  @inline protected final def derive[F[_]](implicit ev: F[Src]): F[Type] =
    ev.asInstanceOf[F[Type]]

  implicit val typeInfo: TypeInfo[Type] = {
    val raw = TypeInfo.forClasses(ClassTag(getClass))
    TypeInfo(
      typeName = raw.typeName.replaceFirst("[$]$", ""),
      typeLabel = raw.typeLabel.replaceFirst("[$](\\d+[$])?$", ""),
      packageName = raw.packageName,
      typeParams = Nil
    )
  }

  implicit final val extractor: HasExtractor.Aux[Type, Src] =
    new HasExtractor[Type] {
      type Source = Src
      def extract(value: Type): Src = value.value
    }
}

private[newtypes] trait NewsubtypeTrait[Src] extends NewEncoding[Src] {
  override type Base = Src
}

private[newtypes] trait NewtypeTrait[Src] extends NewEncoding[Src] {
  override type Base = Any { type NewType$base }
}
