package hr.com.blanka.apartments

import java.time.LocalDate

import hr.com.blanka.apartments.http.model._

object RequestResponseGenerators {

  val USER_ID = "userId"
  val UNIT_ID = 1

  def generateEnquiryReceivedRequest(
      userId: String = USER_ID,
      enquiryRequest: EnquiryRequest = generateEnquiryRequest()
  ): EnquiryReceivedRequest =
    EnquiryReceivedRequest(userId = userId, enquiry = enquiryRequest)

  def generateEnquiryRequest(unitId: Int = UNIT_ID,
                             fromDate: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
                             toDate: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(12),
                             name: String = "John",
                             surname: String = "Cockroach",
                             phoneNumber: String = "+35395443443",
                             email: String = "john.cockroach@gmail.com",
                             address: String = "7 Street",
                             city: String = "London",
                             country: String = "UK",
                             animals: String = "One elephant",
                             noOfPeople: String = "2+2",
                             note: String = "We like to barbecue indoors"): EnquiryRequest =
    EnquiryRequest(
      unitId = unitId,
      dateFrom = fromDate,
      dateTo = toDate,
      name = name,
      surname = surname,
      phoneNumber = phoneNumber,
      email = email,
      address = address,
      city = city,
      country = country,
      animals = animals,
      noOfPeople = noOfPeople,
      note = note
    )

  def generateDepositPaidRequest(
      bookingId: Long,
      userId: String = USER_ID,
      depositAmount: BigDecimal = BigDecimal(100),
      currency: String = "EUR"
  ): DepositPaidRequest =
    DepositPaidRequest(userId = userId,
                       bookingId = bookingId,
                       depositAmount = depositAmount,
                       currency = currency)

  def generateSavePriceRangeRequest(
      userId: String = USER_ID,
      unitId: Int = UNIT_ID,
      from: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
      to: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(12),
      price: BigDecimal = BigDecimal(50)
  ): SavePriceRangeRequest =
    SavePriceRangeRequest(
      userId = userId,
      unitId = unitId,
      from = from,
      to = to,
      price = price
    )

  def generateLookupPriceForRangeRequest(
      userId: String = USER_ID,
      unitId: Int = UNIT_ID,
      from: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
      to: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(12)
  ): LookupPriceForRangeRequest =
    LookupPriceForRangeRequest(
      userId = userId,
      unitId = unitId,
      from = from,
      to = to
    )

  def generateBookedDaysResponse(dateFrom: LocalDate, dateTo: LocalDate): BookedDatesResponse = {
    import hr.com.blanka.apartments.utils.HelperMethods._

    val days = iterateThroughDays(dateFrom, dateTo)
    val bookedDays = days.foldLeft(List[BookedDateResponse]())(
      (acc, date) =>
        acc match {
          case Nil => List(BookedDateResponse(date, firstDay = true, lastDay = false))
          case x if x.size < days.size - 1 =>
            acc :+ BookedDateResponse(date, firstDay = false, lastDay = false)
          case _ => acc :+ BookedDateResponse(date, firstDay = false, lastDay = true)
      }
    )

    BookedDatesResponse(bookedDays)
  }
}
