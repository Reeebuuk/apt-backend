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
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.common.DayMonth
import play.api.libs.json.{ Json, OFormat }

class MyOwnSerializer2 extends SerializerWithStringManifest {

  val EnquirySaved         = "EnquirySaved"
  val EnquiryBooked        = "EnquiryBooked"
  val NewBookingIdAssigned = "NewBookingIdAssigned"
  val DailyPriceSaved      = "DailyPriceSaved"
  val UTF_8: String        = StandardCharsets.UTF_8.name()

  implicit lazy val userIdFormat: OFormat[UserId]               = Json.format[UserId]
  implicit lazy val bookingIdFormat: OFormat[BookingId]         = Json.format[BookingId]
  implicit lazy val unitIdFormat: OFormat[UnitId]               = Json.format[UnitId]
  implicit lazy val enquiryFormat: OFormat[Enquiry]             = Json.format[Enquiry]
  implicit lazy val enquirySavedFormat: OFormat[EnquirySaved]   = Json.format[EnquirySaved]
  implicit lazy val enquiryBookedFormat: OFormat[EnquiryBooked] = Json.format[EnquiryBooked]
  implicit lazy val newBookingIdAssignedFormat: OFormat[NewBookingIdAssigned] =
    Json.format[NewBookingIdAssigned]
  implicit lazy val dayMonthFormat: OFormat[DayMonth]               = Json.format[DayMonth]
  implicit lazy val dailyPriceSavedFormat: OFormat[DailyPriceSaved] = Json.format[DailyPriceSaved]

  def identifier = 3443221

  def manifest(obj: AnyRef): String =
    obj match {
      case _: EnquirySaved         => EnquirySaved
      case _: EnquiryBooked        => EnquiryBooked
      case _: NewBookingIdAssigned => NewBookingIdAssigned
      case _: DailyPriceSaved      => DailyPriceSaved
    }

  def toBinary(obj: AnyRef): Array[Byte] =
    obj match {
      case x: EnquirySaved         => Json.toJson(x).toString().getBytes(UTF_8)
      case x: EnquiryBooked        => Json.toJson(x).toString().getBytes(UTF_8)
      case x: NewBookingIdAssigned => Json.toJson(x).toString().getBytes(UTF_8)
      case x: DailyPriceSaved      => Json.toJson(x).toString().getBytes(UTF_8)
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

    }
}
