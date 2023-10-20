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
  * Scala 2 specific encoding for newtypes that wrap types with a
  * type parameter — common trait to use in [[monix.newtypes.NewtypeK]]
  * and [[monix.newtypes.NewsubtypeK]].
  */
private trait NewEncodingK[Src[_]] {
  type Base[A]
  trait Tag extends Any
  type Type[A] <: Base[A] with Tag

  @inline final def value[A](x: Type[A]): Src[A] =
    x.asInstanceOf[Src[A]]

  implicit final class Ops[A](val self: Type[A]) {
    @inline def value: Src[A] = NewEncodingK.this.value(self)
  }

  @inline
  protected final def unsafeCoerce[A](value: Src[A]): Type[A] =
    value.asInstanceOf[Type[A]]

  @inline
  protected final def derive[F[_], A](implicit ev: F[Src[A]]): F[Type[A]] =
    ev.asInstanceOf[F[Type[A]]]

  @inline
  protected final def deriveK[F[_[_]]](implicit ev: F[Src]): F[Type] =
    ev.asInstanceOf[F[Type]]

  implicit def typeName[A: TypeInfo]: TypeInfo[Type[A]] = {
    val raw = TypeInfo.forClasses(ClassTag(getClass))
    TypeInfo(
      typeName = raw.typeName.replaceFirst("[$]$", ""),
      typeLabel = raw.typeLabel.replaceFirst("[$](\\d+[$])?$", ""),
      packageName = raw.packageName,
      typeParams = List(Some(implicitly[TypeInfo[A]]))
    )
  }

  implicit final def extractor[A]: HasExtractor.Aux[Type[A], Src[A]] =
    new HasExtractor[Type[A]] {
      type Source = Src[A]
      def extract(value: Type[A]): Src[A] = value.value
    }
}

private[newtypes] trait NewtypeTraitK[Src[_]] extends NewEncodingK[Src] {
  override type Base[A] = Any { type NewType$base }
}

private[newtypes] trait NewtypeCovariantTraitK[Src[+_]] extends NewEncodingK[Src] {
  override type Base[+A] = Any { type NewType$base }
  override type Type[+A] <: Base[A] with Tag
}

private[newtypes] trait NewsubtypeTraitK[Src[_]] extends NewEncodingK[Src] {
  override type Base[A] = Src[A]
}

private[newtypes] trait NewsubtypeCovariantTraitK[Src[+_]] extends NewEncodingK[Src] {
  override type Base[+A] = Src[A]
  override type Type[+A] <: Base[A] with Tag
}
