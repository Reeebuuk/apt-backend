package hr.com.blanka.apartments.utils

import hr.com.blanka.apartments.http.model.ErrorResponse
import org.joda.time.LocalDate
import spray.json.{ DefaultJsonProtocol, JsString, JsValue, JsonFormat, RootJsonFormat }

trait WriteMarshallingSupport extends DefaultJsonProtocol with DateFormatter {

  import hr.com.blanka.apartments.http.model._

  implicit val PriceForRangeDtoFormat: RootJsonFormat[PriceForRangeResponse] = jsonFormat1(PriceForRangeResponse.apply)
  implicit val AvailableApartmentsFormat: RootJsonFormat[AvailableApartmentsResponse] = jsonFormat1(
    AvailableApartmentsResponse.apply
  )
  implicit val BookedDayFormat: RootJsonFormat[BookedDayResponse] = jsonFormat3(BookedDayResponse.apply)
  implicit val BookedDaysFormat: RootJsonFormat[BookedDaysResponse] = jsonFormat1(BookedDaysResponse.apply)
}

trait ReadMarshallingSupport extends DefaultJsonProtocol with DateFormatter {

  import hr.com.blanka.apartments.http.model._

  implicit val LookupPriceForRangeFormat: RootJsonFormat[LookupPriceForRangeRequest] = jsonFormat4(
    LookupPriceForRangeRequest.apply
  )
  implicit val SavePriceRangeDtoFormat: RootJsonFormat[SavePriceRangeRequest] = jsonFormat5(
    SavePriceRangeRequest.apply
  )
  implicit val EnquiryFormat: RootJsonFormat[EnquiryRequest] = jsonFormat13(EnquiryRequest.apply)
  implicit val SaveBookingFormat: RootJsonFormat[EnquiryReceivedRequest] = jsonFormat2(EnquiryReceivedRequest.apply)
  implicit val DepositPaidFormat: RootJsonFormat[DepositPaidRequest] = jsonFormat4(DepositPaidRequest.apply)
}

trait DateFormatter { self: DefaultJsonProtocol =>

  import org.joda.time.format.ISODateTimeFormat

  implicit val LocalDateFormat = new JsonFormat[LocalDate] {

    private val iso_date_time = ISODateTimeFormat.localDateParser()

    def write(x: LocalDate) = JsString(x.toString)

    def read(value: JsValue): LocalDate = value match {
      case JsString(x) => LocalDate.parse(x, iso_date_time)
      case x =>
        throw new RuntimeException(s"Unexpected type %s on parsing of LocalDateTime type".format(x.getClass.getName))
    }
  }

  implicit val ErrorDtoFormat: RootJsonFormat[ErrorResponse] = jsonFormat1(ErrorResponse.apply)

}