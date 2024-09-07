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

package monix.newtypes

import scala.reflect.ClassTag

/**
  * Scala 3 specific encoding for newtypes that wrap types with a
  * type parameter — common trait to use in [[monix.newtypes.NewtypeK]]
  * and [[monix.newtypes.NewsubtypeK]].
  */
private trait NewEncodingK[Src[_]] {
  type Type[A]

  extension [A](self: Type[A]) {
    inline final def value: Src[A] = self.asInstanceOf[Src[A]]
  }

  protected inline final def unsafeCoerce[A](value: Src[A]): Type[A] =
    value.asInstanceOf[Type[A]]

  protected inline final def derive[F[_], A](using ev: F[Src[A]]): F[Type[A]] =
    ev.asInstanceOf[F[Type[A]]]

  protected inline final def deriveK[F[_[_]]](using ev: F[Src]): F[Type] =
    ev.asInstanceOf[F[Type]]

  implicit def typeName[A: TypeInfo]: TypeInfo[Type[A]] = {
    val raw = TypeInfo.forClasses(ClassTag(getClass()))
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
      def extract(value: Type[A]) = value.value
    }
}

private trait NewtypeTraitK[Src[_]] extends NewEncodingK[Src] {
  override opaque type Type[A] = Src[A]
}

private trait NewtypeCovariantTraitK[Src[+_]] extends NewEncodingK[Src] {
  override opaque type Type[+A] = Src[A]
}

private trait NewsubtypeTraitK[Src[_]] extends NewEncodingK[Src] {
  override opaque type Type[A] <: Src[A] = Src[A]
}

private trait NewsubtypeCovariantTraitK[Src[+_]] extends NewEncodingK[Src] {
  override opaque type Type[+A] <: Src[A] = Src[A]
}
