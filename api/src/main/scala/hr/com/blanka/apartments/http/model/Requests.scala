package hr.com.blanka.apartments.http.model

import hr.com.blanka.apartments.command.booking.{ DepositPaid, SaveEnquiryInitiated, Source }
import hr.com.blanka.apartments.command.price.SavePriceRange
import java.time.{ LocalDate, LocalDateTime }

import hr.com.blanka.apartments.common.ValueClasses.{ EnquiryId, UnitId, UserId }
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
                          country: String,
                          animals: String,
                          noOfPeople: String,
                          note: String) {
  def toCommand(userId: UserId): SaveEnquiryInitiated =
    SaveEnquiryInitiated(
      userId,
      Enquiry(
        UnitId(unitId),
        dateFrom,
        dateTo,
        name,
        surname,
        phoneNumber,
        email,
        country,
        animals,
        noOfPeople,
        note
      ),
      Source.Website
    )
}

case class DepositPaidRequest(enquiryId: Long, amount: BigDecimal, currency: String) {
  def toCommand(userId: UserId): DepositPaid =
    DepositPaid(userId, EnquiryId(enquiryId), amount, currency)
}

case class SavePriceRangeRequest(unitId: Int, from: LocalDate, to: LocalDate, price: BigDecimal) {
  def toCommand(userId: UserId): SavePriceRange =
    SavePriceRange(userId, UnitId(unitId), from, to, price)
}

case class LookupPriceForRangeRequest(unitId: Int, from: LocalDate, to: LocalDate) {
  def toQuery(userId: UserId): LookupPriceForRange =
    LookupPriceForRange(enquiryId = None,
                        userId = userId,
                        unitId = UnitId(unitId),
                        from = from,
                        to = to,
                        validOn = LocalDateTime.now())
}

case class ContactRequest(name: String, email: String, text: String) {
  def toCommand: SaveContact =
    SaveContact(name, email, text)
}
