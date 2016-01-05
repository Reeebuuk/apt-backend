package hr.com.blanka.apartments.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import hr.com.blanka.apartments.http.routes.{SavePriceForRangeDto, CalculatePriceForRangeDto, ErrorResponse, PriceForRangeResponse}
import spray.json.{DefaultJsonProtocol, JsString, JsValue, JsonFormat}

trait MarshallingSupport extends DefaultJsonProtocol {
  implicit val CalculatePriceForRangeDtoFormat = jsonFormat3(CalculatePriceForRangeDto.apply)
  implicit val SavePriceForRangeDtoFormat = jsonFormat4(SavePriceForRangeDto.apply)
  implicit val PriceForRangeDtoFormat = jsonFormat2(PriceForRangeResponse.apply)
  implicit val ErrorDtoFormat = jsonFormat2(ErrorResponse.apply)

  implicit val LocalDateTimeFormat = new JsonFormat[LocalDateTime] {

    private val iso_date_time = DateTimeFormatter.ISO_DATE_TIME

    def write(x: LocalDateTime) = JsString(iso_date_time.format(x))

    def read(value: JsValue) = value match {
      case JsString(x) => LocalDateTime.parse(x, iso_date_time)
      case x => throw new RuntimeException(s"Unexpected type %s on parsing of LocalDateTime type".format(x.getClass.getName))
    }
  }
}
