package hr.com.blanka.apartments.utils

import hr.com.blanka.apartments.http.model.{ ErrorResponse, NewEnquiryResponse }
import play.api.libs.json.{ Json, OWrites, Reads }

trait WriteMarshallingSupport extends ErrorMarshallingSupport {

  import hr.com.blanka.apartments.http.model._

  implicit val priceForRangeDtoWrites: OWrites[PriceForRangeResponse] =
    Json.writes[PriceForRangeResponse]
  implicit val availableUnitsWrites: OWrites[AvailableUnitsResponse] =
    Json.writes[AvailableUnitsResponse]
  implicit val bookedDateWrites: OWrites[BookedDateResponse]   = Json.writes[BookedDateResponse]
  implicit val bookedDatesWrites: OWrites[BookedDatesResponse] = Json.writes[BookedDatesResponse]
  implicit val pricePerPeriodResponseWrites: OWrites[PricePerPeriodResponse] =
    Json.writes[PricePerPeriodResponse]
  implicit val pricePerPeriodsResponseWrites: OWrites[PricePerPeriodsResponse] =
    Json.writes[PricePerPeriodsResponse]
  implicit val enquiryResponseWrites: OWrites[EnquiryResponse] =
    Json.writes[EnquiryResponse]
  implicit val bookingResponseWrites: OWrites[BookingResponse] =
    Json.writes[BookingResponse]
  implicit val unapprovedEnquiryResponseWrites: OWrites[UnapprovedEnquiryResponse] =
    Json.writes[UnapprovedEnquiryResponse]
  implicit val approvedEnquiryResponseWrites: OWrites[ApprovedEnquiryResponse] =
    Json.writes[ApprovedEnquiryResponse]
  implicit val allBookingsResponsesWrites: OWrites[AllBookingsResponse] =
    Json.writes[AllBookingsResponse]
  implicit val allUnapprovedEnquiriesWrites: OWrites[AllUnapprovedEnquiriesResponse] =
    Json.writes[AllUnapprovedEnquiriesResponse]
  implicit val allApprovedEnquiriesWrites: OWrites[AllApprovedEnquiriesResponse] =
    Json.writes[AllApprovedEnquiriesResponse]
}

trait ReadMarshallingSupport extends ErrorMarshallingSupport {

  import hr.com.blanka.apartments.http.model._

  implicit val lookupPriceForRangeReads: Reads[LookupPriceForRangeRequest] =
    Json.reads[LookupPriceForRangeRequest]
  implicit val savePriceRangeDtoReads: Reads[SavePriceRangeRequest] =
    Json.reads[SavePriceRangeRequest]
  implicit val enquiryReads: Reads[EnquiryRequest]         = Json.reads[EnquiryRequest]
  implicit val depositPaidReads: Reads[DepositPaidRequest] = Json.reads[DepositPaidRequest]
  implicit val contactRequestReads: Reads[ContactRequest]  = Json.reads[ContactRequest]

}

trait ErrorMarshallingSupport {
  implicit val errorDtoWrites: OWrites[ErrorResponse] = Json.writes[ErrorResponse]
  implicit val newEnquiryResponseWrites: OWrites[NewEnquiryResponse] =
    Json.writes[NewEnquiryResponse]

}
