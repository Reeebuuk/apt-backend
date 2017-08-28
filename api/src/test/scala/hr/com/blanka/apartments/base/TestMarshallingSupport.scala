package hr.com.blanka.apartments.base

import hr.com.blanka.apartments.http.model._
import play.api.libs.json.{ Format, Json, OWrites, Reads }

trait TestMarshallingSupport {
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
  implicit val bookedDateResponseReads: Reads[BookedDateResponse] =
    Json.reads[BookedDateResponse]
  implicit val bookedDatesResponseReads: Reads[BookedDatesResponse] =
    Json.reads[BookedDatesResponse]
  implicit val priceForRangeResponse: Format[PriceForRangeResponse] =
    Json.format[PriceForRangeResponse]
  implicit val bookingIdFormat: Format[NewEnquiryResponse] =
    Json.format[NewEnquiryResponse]

  implicit val enquiryResponseReads: Reads[EnquiryResponse] =
    Json.reads[EnquiryResponse]
  implicit val bookingResponseReads: Reads[BookingResponse] =
    Json.reads[BookingResponse]
  implicit val allBookingsResponsesReads: Reads[AllBookingsResponse] =
    Json.reads[AllBookingsResponse]
}

object TestMarshallingSupport extends TestMarshallingSupport
