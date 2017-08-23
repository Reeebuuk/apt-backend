package hr.com.blanka.apartments

import java.time.{ LocalDate, ZoneOffset }

import hr.com.blanka.apartments.http.model._

object RequestResponseGenerators {

  val USER_ID                 = "userId"
  val UNIT_ID                 = 1
  val DATE_FROM: LocalDate    = LocalDate.now().withMonth(11).withDayOfMonth(5)
  val DATE_TO: LocalDate      = LocalDate.now().withMonth(11).withDayOfMonth(12)
  val NAME                    = "John"
  val SURNAME                 = "Cockroach"
  val PHONE_NUMBER            = "35395443443"
  val EMAIL                   = "john.cockr@gmail.com"
  val ADDRESS                 = "12 street"
  val CITY                    = "Lisbon"
  val COUNTRY                 = "Portugal"
  val ANIMALS                 = "Donkey"
  val NO_OF_PEOPLE            = "2+2"
  val NOTE                    = "We like camp fire indoors"
  val DEPOSIT_AMOUNT          = BigDecimal(100)
  val CURRENCY                = "EUR"
  val DAY_PRICE               = BigDecimal(50)
  val TIME_SAVED: LocalDate   = LocalDate.now().withMonth(11).withDayOfMonth(2)
  val DEPOSIT_PAID: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(1)

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
                             address: String = ADDRESS,
                             city: String = CITY,
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
      depositAmount: BigDecimal = DEPOSIT_AMOUNT,
      currency: String = CURRENCY
  ): DepositPaidRequest =
    DepositPaidRequest(userId = userId,
                       bookingId = bookingId,
                       depositAmount = depositAmount,
                       currency = currency)

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

  def generateBookingResponse(
      bookingId: Long,
      timeSaved: LocalDate = TIME_SAVED,
      unitId: Int = UNIT_ID,
      fromDate: LocalDate = DATE_FROM,
      toDate: LocalDate = DATE_TO,
      name: String = NAME,
      surname: String = SURNAME,
      phoneNumber: String = PHONE_NUMBER,
      email: String = EMAIL,
      address: String = ADDRESS,
      city: String = CITY,
      country: String = COUNTRY,
      animals: String = ANIMALS,
      noOfPeople: String = NO_OF_PEOPLE,
      note: String = NOTE,
      totalPrice: BigDecimal = BigDecimal(0),
      depositAmount: BigDecimal = DEPOSIT_AMOUNT,
      depositCurrency: String = CURRENCY,
      depositWhen: LocalDate = DEPOSIT_PAID
  ): BookingResponse =
    BookingResponse(
      bookingId = bookingId,
      timeSaved = timeSaved.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
      unitId = unitId,
      dateFrom = fromDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
      dateTo = toDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli,
      name = name,
      surname = surname,
      phoneNumber = phoneNumber,
      email = email,
      address = address,
      city = city,
      country = country,
      animals = animals,
      noOfPeople = noOfPeople,
      note = note,
      totalPrice = totalPrice,
      depositAmount = depositAmount,
      depositCurrency = depositCurrency,
      depositWhen = depositWhen.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli
    )

  def generateAllBookingsResponse(bookingIds: List[Long]): AllBookingsResponse =
    AllBookingsResponse(
      bookings = bookingIds.map(generateBookingResponse(_))
    )
}
