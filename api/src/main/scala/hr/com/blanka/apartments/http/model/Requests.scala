package hr.com.blanka.apartments.http.model

import hr.com.blanka.apartments.command.booking.{ DepositPaid, SaveEnquiryInitiated }
import hr.com.blanka.apartments.command.price.SavePriceRange
import java.time.LocalDate

import hr.com.blanka.apartments.ValueClasses.{ BookingId, UnitId, UserId }
import hr.com.blanka.apartments.command.contact.SaveContact
import hr.com.blanka.apartments.common.Enquiry
import hr.com.blanka.apartments.query.price.LookupPriceForRange

case class EnquiryRequest(unitId: Int,
                          dateFrom: LocalDate,
                          dateTo: LocalDate,
                          name: String,
                          surname: String,
                          phoneNumber: String,
                          email: String,
                          address: String,
                          city: String,
                          country: String,
                          animals: String,
                          noOfPeople: String,
                          note: String)

case class EnquiryReceivedRequest(userId: String, enquiry: EnquiryRequest) {
  def toCommand: SaveEnquiryInitiated =
    SaveEnquiryInitiated(
      UserId(userId),
      Enquiry(
        UnitId(enquiry.unitId),
        enquiry.dateFrom,
        enquiry.dateTo,
        enquiry.name,
        enquiry.surname,
        enquiry.phoneNumber,
        enquiry.email,
        enquiry.address,
        enquiry.city,
        enquiry.country,
        enquiry.animals,
        enquiry.noOfPeople,
        enquiry.note
      )
    )
}
case class DepositPaidRequest(userId: String,
                              bookingId: Long,
                              depositAmount: BigDecimal,
                              currency: String) {
  def toCommand: DepositPaid =
    DepositPaid(UserId(userId), BookingId(bookingId), depositAmount, currency)
}

case class SavePriceRangeRequest(userId: String,
                                 unitId: Int,
                                 from: LocalDate,
                                 to: LocalDate,
                                 price: BigDecimal) {
  def toCommand: SavePriceRange =
    SavePriceRange(UserId(userId), UnitId(unitId), from, to, price)
}

case class LookupPriceForRangeRequest(userId: String, unitId: Int, from: LocalDate, to: LocalDate) {
  def toQuery: LookupPriceForRange =
    LookupPriceForRange(UserId(userId), UnitId(unitId), from, to)
}

case class ContactRequest(name: String, email: String, text: String) {
  def toCommand: SaveContact =
    SaveContact(name, email, text)
}
