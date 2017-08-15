package hr.com.blanka.apartments

import hr.com.blanka.apartments.http.model._
import play.api.libs.json.{ Format, Json, OWrites, Reads }

trait MarshallingSupport {
  implicit val enquiryRequestWrites: OWrites[EnquiryRequest] =
    Json.writes[EnquiryRequest]
  implicit val enquiryReceivedRequestWrites: OWrites[EnquiryReceivedRequest] =
    Json.writes[EnquiryReceivedRequest]
  implicit val depositPaidRequestWrites: OWrites[DepositPaidRequest] =
    Json.writes[DepositPaidRequest]
  implicit val savePriceRangeRequestWrites: OWrites[SavePriceRangeRequest] =
    Json.writes[SavePriceRangeRequest]
  implicit val lookupPriceForRangeRequestWrites: OWrites[LookupPriceForRangeRequest] =
    Json.writes[LookupPriceForRangeRequest]

  implicit val availableApartmentsReads: Reads[AvailableUnitsResponse] =
    Json.reads[AvailableUnitsResponse]
  implicit val bookedDayResponseReads: Reads[BookedDayResponse] =
    Json.reads[BookedDayResponse]
  implicit val bookedDaysResponseReads: Reads[BookedDaysResponse] =
    Json.reads[BookedDaysResponse]
  implicit val priceForRangeResponse1: Format[PriceForRangeResponse] =
    Json.format[PriceForRangeResponse]
  implicit val BookingIdFormat: Format[NewEnquiryResponse] =
    Json.format[NewEnquiryResponse]
}

object MarshallingSupport extends MarshallingSupport
