package hr.com.blanka.apartments.query.booking

import java.time.LocalDate

import org.scalatest.{ FreeSpec, Matchers }

class BookedDatesActorTest extends FreeSpec with Matchers {

  import hr.com.blanka.apartments.InternalGenerators._

  "BookedDatesActor with no intercepts" in {

    val enquiry                       = generateEnquiry()
    val actualResult: List[BookedDay] = BookedDatesActor.markNewDates(List.empty, enquiry = enquiry)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = enquiry.dateFrom, firstDay = true, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(2), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(3), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(4), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(5), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(6), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(7), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with no intercepts, idempotency" in {

    val enquiry = generateEnquiry()
    val actualResult1: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, enquiry = enquiry)
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(actualResult1, enquiry = enquiry)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = enquiry.dateFrom, firstDay = true, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(2), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(3), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(4), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(5), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(6), firstDay = false, lastDay = false),
        BookedDay(day = enquiry.dateFrom.plusDays(7), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, beginning not on the edge" in {

    val firstEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(8),
    )
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, enquiry = firstEnquiry)

    val secondEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(4),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(7),
    )
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, enquiry = secondEnquiry)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = secondEnquiry.dateFrom.plusDays(0), firstDay = true, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(2), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(3), firstDay = false, lastDay = false)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, ending not on the edge" in {

    val firstEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(8),
    )
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, enquiry = firstEnquiry)

    val secondEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(6),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(9),
    )
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, enquiry = secondEnquiry)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = secondEnquiry.dateFrom.plusDays(0), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(2), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(3), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, beginning at the edge" in {

    val firstEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(6),
    )
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, enquiry = firstEnquiry)

    val secondEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(6),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(8),
    )
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, enquiry = secondEnquiry)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = secondEnquiry.dateFrom.plusDays(0), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(2), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, ending at the edge" in {

    val firstEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(7),
    )
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, enquiry = firstEnquiry)

    val secondEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(7),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(9),
    )
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, enquiry = secondEnquiry)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = secondEnquiry.dateFrom.plusDays(0), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(2), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, within" in {

    val firstEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(9),
    )
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, enquiry = firstEnquiry)

    val secondEnquiry = generateEnquiry(
      fromDate = LocalDate.now().withMonth(11).withDayOfMonth(6),
      toDate = LocalDate.now().withMonth(11).withDayOfMonth(8),
    )
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, enquiry = secondEnquiry)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = secondEnquiry.dateFrom.plusDays(0), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = secondEnquiry.dateFrom.plusDays(2), firstDay = false, lastDay = false)
      )

    actualResult shouldBe expectedResult
  }
}
