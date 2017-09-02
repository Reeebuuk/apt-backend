package hr.com.blanka.apartments.util

import java.time.{ LocalDate, ZoneOffset }

import hr.com.blanka.apartments.http.model._

object RequestResponseGenerators extends Constants {

  def generateEnquiryReceivedRequest(
      userId: String = USER_ID,
      enquiryRequest: EnquiryRequest = generateEnquiryRequest()
  ): EnquiryReceivedRequest =
    EnquiryReceivedRequest(userId = userId, enquiry = enquiryRequest)

  def generateEnquiryRequest(unitId: Int = UNIT_ID,
                             fromDate: LocalDate = DATE_FROM,
                             toDate: LocalDate = DATE_TO,
                             name: String = NAME,
                             surname: String = SURNAME,
                             phoneNumber: String = PHONE_NUMBER,
                             email: String = EMAIL,
                             country: String = COUNTRY,
                             animals: String = ANIMALS,
                             noOfPeople: String = NO_OF_PEOPLE,
                             note: String = NOTE): EnquiryRequest =
    EnquiryRequest(
      unitId = unitId,
      dateFrom = fromDate,
      dateTo = toDate,
      name = name,
      surname = surname,
      phoneNumber = phoneNumber,
      email = email,
      country = country,
      animals = animals,
      noOfPeople = noOfPeople,
      note = note
    )

  def generateDepositPaidRequest(
      enquiryId: Long,
      userId: String = USER_ID,
      amount: BigDecimal = DEPOSIT_AMOUNT,
      currency: String = CURRENCY
  ): DepositPaidRequest =
    DepositPaidRequest(userId = userId, enquiryId = enquiryId, amount = amount, currency = currency)

  def generateSavePriceRangeRequest(
      userId: String = USER_ID,
      unitId: Int = UNIT_ID,
      from: LocalDate = DATE_FROM,
      to: LocalDate = DATE_TO,
      price: BigDecimal = DAY_PRICE
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
      from: LocalDate = DATE_FROM,
      to: LocalDate = DATE_TO
  ): LookupPriceForRangeRequest =
    LookupPriceForRangeRequest(
      userId = userId,
      unitId = unitId,
      from = from,
      to = to
    )

  def generateBookedDaysResponse(dateFrom: LocalDate, dateTo: LocalDate): BookedDatesResponse = {
    import hr.com.blanka.apartments.utils.DateHelperMethods._

    val days = iterateThroughDaysIncludingLast(dateFrom, dateTo)
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

  def generateBookingResponse(
      enquiryId: Long,
      enquiryDttm: LocalDate = TIME_SAVED,
      approvalDttm: LocalDate = TIME_SAVED,
      unitId: Int = UNIT_ID,
      fromDate: LocalDate = DATE_FROM,
      toDate: LocalDate = DATE_TO,
      name: String = NAME,
      surname: String = SURNAME,
      phoneNumber: String = PHONE_NUMBER,
      email: String = EMAIL,
      country: String = COUNTRY,
      animals: String = ANIMALS,
      noOfPeople: String = NO_OF_PEOPLE,
      note: String = NOTE,
      totalPrice: BigDecimal = BigDecimal(7),
      depositAmount: BigDecimal = DEPOSIT_AMOUNT,
      depositCurrency: String = CURRENCY,
      depositWhen: LocalDate = DEPOSIT_PAID
  ): BookingResponse =
    BookingResponse(
      enquiryId = enquiryId,
      enquiryDttm = enquiryDttm.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
      approvalDttm = approvalDttm.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
      enquiry = EnquiryResponse(
        unitId = unitId,
        dateFrom = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
        dateTo = toDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
        name = name,
        surname = surname,
        phoneNumber = phoneNumber,
        email = email,
        country = country,
        animals = animals,
        noOfPeople = noOfPeople,
        note = note
      ),
      totalPrice = totalPrice,
      depositAmount = depositAmount,
      depositCurrency = depositCurrency,
      depositWhen = depositWhen.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli
    )

  def generateAllBookingsResponse(enquiryIds: List[Long]): AllBookingsResponse =
    AllBookingsResponse(
      enquiries = enquiryIds.map(generateBookingResponse(_))
    )
}
