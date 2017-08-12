package hr.com.blanka.apartments.utils

import hr.com.blanka.apartments.http.model.ErrorResponse
import hr.com.blanka.apartments.http.routes.command.BookingId
import play.api.libs.json.{ Json, OWrites, Reads }

trait WriteMarshallingSupport extends ErrorMarshallingSupport {

  import hr.com.blanka.apartments.http.model._

  implicit val PriceForRangeDtoFormat: OWrites[PriceForRangeResponse] =
    Json.writes[PriceForRangeResponse]
  implicit val AvailableApartmentsFormat: OWrites[AvailableApartmentsResponse] =
    Json.writes[AvailableApartmentsResponse]
  implicit val BookedDayFormat: OWrites[BookedDayResponse]   = Json.writes[BookedDayResponse]
  implicit val BookedDaysFormat: OWrites[BookedDaysResponse] = Json.writes[BookedDaysResponse]

}

trait ReadMarshallingSupport extends ErrorMarshallingSupport {

  import hr.com.blanka.apartments.http.model._

  implicit val lookupPriceForRangeFormat: Reads[LookupPriceForRangeRequest] =
    Json.reads[LookupPriceForRangeRequest]
  implicit val savePriceRangeDtoFormat: Reads[SavePriceRangeRequest] =
    Json.reads[SavePriceRangeRequest]
  implicit val enquiryFormat: Reads[EnquiryRequest] = Json.reads[EnquiryRequest]
  implicit val enquiryReceivedRequestFormat: Reads[EnquiryReceivedRequest] =
    Json.reads[EnquiryReceivedRequest]
  implicit val depositPaidFormat: Reads[DepositPaidRequest] = Json.reads[DepositPaidRequest]

}

trait ErrorMarshallingSupport {
  implicit val errorDtoFormat: OWrites[ErrorResponse] = Json.writes[ErrorResponse]
  implicit val bookingIdFormat: OWrites[BookingId]    = Json.writes[BookingId]

}
