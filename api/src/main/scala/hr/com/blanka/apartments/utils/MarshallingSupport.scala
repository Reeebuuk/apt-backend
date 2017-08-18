package hr.com.blanka.apartments.utils

import hr.com.blanka.apartments.http.model.{ ErrorResponse, NewEnquiryResponse }
import play.api.libs.json.{ Json, OWrites, Reads }

trait WriteMarshallingSupport extends ErrorMarshallingSupport {

  import hr.com.blanka.apartments.http.model._

  implicit val priceForRangeDtoFormat: OWrites[PriceForRangeResponse] =
    Json.writes[PriceForRangeResponse]
  implicit val availableUnitsFormat: OWrites[AvailableUnitsResponse] =
    Json.writes[AvailableUnitsResponse]
  implicit val bookedDayFormat: OWrites[BookedDayResponse]   = Json.writes[BookedDayResponse]
  implicit val bookedDaysFormat: OWrites[BookedDaysResponse] = Json.writes[BookedDaysResponse]
  implicit val pricePerPeriodResponseFormat: OWrites[PricePerPeriodResponse] =
    Json.writes[PricePerPeriodResponse]
  implicit val pricePerPeriodsResponseFormat: OWrites[PricePerPeriodsResponse] =
    Json.writes[PricePerPeriodsResponse]
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
  implicit val contactRequestFormat: Reads[ContactRequest]  = Json.reads[ContactRequest]

}

trait ErrorMarshallingSupport {
  implicit val errorDtoFormat: OWrites[ErrorResponse] = Json.writes[ErrorResponse]
  implicit val newEnquiryResponseFormat: OWrites[NewEnquiryResponse] =
    Json.writes[NewEnquiryResponse]

}
