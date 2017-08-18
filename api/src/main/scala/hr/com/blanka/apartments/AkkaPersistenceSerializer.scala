package hr.com.blanka.apartments

import java.nio.charset.StandardCharsets

import akka.serialization.SerializerWithStringManifest
import hr.com.blanka.apartments.ValueClasses.{ BookingId, UnitId, UserId }
import hr.com.blanka.apartments.command.booking.{
  Enquiry,
  EnquiryBooked,
  EnquirySaved,
  NewBookingIdAssigned
}
import hr.com.blanka.apartments.command.contact.ContactSaved
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.common.DayMonth
import hr.com.blanka.apartments.query.PersistenceOffsetSaved

class AkkaPersistenceSerializer extends SerializerWithStringManifest {

  val EnquirySaved           = "EnquirySaved"
  val EnquiryBooked          = "EnquiryBooked"
  val NewBookingIdAssigned   = "NewBookingIdAssigned"
  val DailyPriceSaved        = "DailyPriceSaved"
  val ContactSaved           = "ContactSaved"
  val PersistenceOffsetSaved = "PersistenceOffsetSaved"
  val UTF_8: String          = StandardCharsets.UTF_8.name()

  import play.api.libs.json._

  case class ValueClassJson[I, T](f1: I => T)(f2: T => Option[I])(implicit reads: Reads[I],
                                                                  writes: Writes[I])
      extends Reads[T]
      with Writes[T] {
    def reads(js: JsValue): JsResult[T] = js.validate[I] map f1
    def writes(id: T): JsValue          = Json.toJson(f2(id))
  }

  implicit lazy val userIdFormat: ValueClassJson[String, UserId] =
    ValueClassJson(UserId)(UserId.unapply)
  implicit lazy val bookingIdFormat: ValueClassJson[Long, BookingId] =
    ValueClassJson(BookingId)(BookingId.unapply)
  implicit lazy val unitIdFormat: ValueClassJson[Int, UnitId] =
    ValueClassJson(UnitId)(UnitId.unapply)

  implicit lazy val enquiryFormat: OFormat[Enquiry]             = Json.format[Enquiry]
  implicit lazy val enquirySavedFormat: OFormat[EnquirySaved]   = Json.format[EnquirySaved]
  implicit lazy val enquiryBookedFormat: OFormat[EnquiryBooked] = Json.format[EnquiryBooked]
  implicit lazy val newBookingIdAssignedFormat: OFormat[NewBookingIdAssigned] =
    Json.format[NewBookingIdAssigned]
  implicit lazy val dayMonthFormat: OFormat[DayMonth]               = Json.format[DayMonth]
  implicit lazy val dailyPriceSavedFormat: OFormat[DailyPriceSaved] = Json.format[DailyPriceSaved]
  implicit lazy val contactSavedFormat: OFormat[ContactSaved]       = Json.format[ContactSaved]
  implicit lazy val persistenceOffsetSavedFormat: OFormat[PersistenceOffsetSaved] =
    Json.format[PersistenceOffsetSaved]

  def identifier = 3443221

  def manifest(obj: AnyRef): String =
    obj match {
      case _: EnquirySaved           => EnquirySaved
      case _: EnquiryBooked          => EnquiryBooked
      case _: NewBookingIdAssigned   => NewBookingIdAssigned
      case _: DailyPriceSaved        => DailyPriceSaved
      case _: ContactSaved           => ContactSaved
      case _: PersistenceOffsetSaved => PersistenceOffsetSaved
    }

  def toBinary(obj: AnyRef): Array[Byte] =
    obj match {
      case x: EnquirySaved           => Json.toJson(x).toString().getBytes(UTF_8)
      case x: EnquiryBooked          => Json.toJson(x).toString().getBytes(UTF_8)
      case x: NewBookingIdAssigned   => Json.toJson(x).toString().getBytes(UTF_8)
      case x: DailyPriceSaved        => Json.toJson(x).toString().getBytes(UTF_8)
      case x: ContactSaved           => Json.toJson(x).toString().getBytes(UTF_8)
      case x: PersistenceOffsetSaved => Json.toJson(x).toString().getBytes(UTF_8)
    }

  def fromBinary(bytes: Array[Byte], manifest: String): AnyRef =
    manifest match {
      case EnquirySaved =>
        Json.parse(new String(bytes, UTF_8)).as[EnquirySaved]
      case EnquiryBooked =>
        Json.parse(new String(bytes, UTF_8)).as[EnquiryBooked]
      case NewBookingIdAssigned =>
        Json.parse(new String(bytes, UTF_8)).as[NewBookingIdAssigned]
      case DailyPriceSaved =>
        Json.parse(new String(bytes, UTF_8)).as[DailyPriceSaved]
      case ContactSaved =>
        Json.parse(new String(bytes, UTF_8)).as[ContactSaved]
      case PersistenceOffsetSaved =>
        Json.parse(new String(bytes, UTF_8)).as[PersistenceOffsetSaved]
    }
}
