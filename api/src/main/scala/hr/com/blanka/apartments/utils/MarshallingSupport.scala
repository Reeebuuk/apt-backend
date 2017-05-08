package hr.com.blanka.apartments.utils

import hr.com.blanka.apartments.command.booking.{DepositPaid, Enquiry, EnquiryReceived}
import hr.com.blanka.apartments.command.price.SavePriceRange
import hr.com.blanka.apartments.http.routes._
import hr.com.blanka.apartments.query.booking.{AvailableApartments, BookedDay, BookedDays}
import hr.com.blanka.apartments.query.price.LookupPriceForRange
import org.joda.time.LocalDate
import org.joda.time.format.ISODateTimeFormat
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

trait MarshallingSupport extends DefaultJsonProtocol {

  implicit val LocalDateFormat = new JsonFormat[LocalDate] {

    private val iso_date_time = ISODateTimeFormat.localDateParser()

    def write(x: LocalDate) = JsString(x.toString)

    def read(value: JsValue) = value match {
      case JsString(x) => LocalDate.parse(x, iso_date_time)
      case x => throw new RuntimeException(s"Unexpected type %s on parsing of LocalDateTime type".format(x.getClass.getName))
    }
  }

  implicit val LookupPriceForRangeFormat = jsonFormat4(LookupPriceForRange.apply)
  implicit val SavePriceRangeDtoFormat = jsonFormat5(SavePriceRange.apply)
  implicit val PriceForRangeDtoFormat = jsonFormat1(PriceForRangeResponse.apply)
  implicit val ErrorDtoFormat = jsonFormat1(ErrorResponse.apply)
  implicit val EnquiryFormat = jsonFormat13(Enquiry.apply)
  implicit val SaveBookingFormat = jsonFormat2(EnquiryReceived.apply)
  implicit val DepositPaidFormat = jsonFormat4(DepositPaid.apply)
  implicit val AvailableApartmentsFormat = jsonFormat1(AvailableApartments.apply)
  implicit val BookedDayFormat = jsonFormat3(BookedDay.apply)
  implicit val BookedDaysFormat = jsonFormat1(BookedDays.apply)
}
