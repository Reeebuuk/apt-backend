package hr.com.blanka.apartments.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import hr.com.blanka.apartments.command.booking.{DepositPaid, Enquiry, EnquiryReceived}
import hr.com.blanka.apartments.command.price.SavePriceRange
import hr.com.blanka.apartments.http.routes._
import hr.com.blanka.apartments.query.price.LookupPriceForRange
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

trait MarshallingSupport extends DefaultJsonProtocol {
  implicit val LookupPriceForRangeFormat = jsonFormat4(LookupPriceForRange.apply)
  implicit val SavePriceRangeDtoFormat = jsonFormat5(SavePriceRange.apply)
  implicit val PriceForRangeDtoFormat = jsonFormat1(PriceForRangeResponse.apply)
  implicit val ErrorDtoFormat = jsonFormat1(ErrorResponse.apply)
  implicit val EnquiryFormat = jsonFormat13(Enquiry.apply)
  implicit val SaveBookingFormat = jsonFormat2(EnquiryReceived.apply)
  implicit val DepositPaidFormat = jsonFormat4(DepositPaid.apply)

  implicit val LocalDateTimeFormat = new JsonFormat[LocalDateTime] {

    private val iso_date_time = DateTimeFormatter.ISO_DATE_TIME

    def write(x: LocalDateTime) = JsString(iso_date_time.format(x))

    def read(value: JsValue) = value match {
      case JsString(x) => LocalDateTime.parse(x, iso_date_time)
      case x => throw new RuntimeException(s"Unexpected type %s on parsing of LocalDateTime type".format(x.getClass.getName))
    }
  }
}
