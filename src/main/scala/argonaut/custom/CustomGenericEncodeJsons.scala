package argonaut
package custom

import shapeless._, labelled._

object CustomGenericEncodeJsons {
  implicit def customHConsEncodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]],
    pc: JsonProductCodec
  ): EncodeJson[FieldType[K, H] :: T] =
    EncodeJson { case h :: t =>
      pc.encodeField(key.value.name, headEncode.value.encode(h), tailEncode.value.encode(t))
    }
}

trait CustomGenericEncodeJsons {
  implicit def hnilEncodeJson[L <: HNil]: EncodeJson[L] =
    GenericEncodeJsons.hnilJsObjectEncodeJson

  implicit def hconsEncodeJson[K <: Symbol, H, T <: HList](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]],
    pc: JsonProductCodec
  ): EncodeJson[FieldType[K, H] :: T] =
    CustomGenericEncodeJsons.customHConsEncodeJson(key, headEncode, tailEncode, pc)

  implicit val cnilEncodeJson: EncodeJson[CNil] =
    GenericEncodeJsons.cnilEncodeJsonFails

  implicit def cconsEncodeJson[K <: Symbol, H, T <: Coproduct](implicit
    key: Witness.Aux[K],
    headEncode: Lazy[EncodeJson[H]],
    tailEncode: Lazy[EncodeJson[T]]
  ): EncodeJson[FieldType[K, H] :+: T] =
    GenericEncodeJsons.cconsJsObjectEncodeJson(key, headEncode, tailEncode)

  implicit def instanceEncodeJson[F, G](implicit
    gen: LabelledGeneric.Aux[F, G],
    encode: Lazy[EncodeJson[G]]
  ): EncodeJson[F] =
    GenericEncodeJsons.defaultInstanceEncodeJson(gen, encode)
}
