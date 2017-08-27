package hr.com.blanka.apartments.query.booking

import java.time.LocalDate

import org.scalatest.{ FreeSpec, Matchers }

class BookedDatesActorTest extends FreeSpec with Matchers {

  "BookedDatesActor with no intercepts" in {

    val fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5)
    val toDate   = LocalDate.now().withMonth(11).withDayOfMonth(8)
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, fromDate, toDate)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = fromDate, firstDay = true, lastDay = false),
        BookedDay(day = fromDate.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(2), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(3), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(4), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(5), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(6), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(7), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with no intercepts, idempotency" in {

    val fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5)
    val toDate   = LocalDate.now().withMonth(11).withDayOfMonth(8)
    val actualResult1: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, fromDate, toDate)
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(actualResult1, fromDate, toDate)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = fromDate, firstDay = true, lastDay = false),
        BookedDay(day = fromDate.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(2), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(3), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(4), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(5), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(6), firstDay = false, lastDay = false),
        BookedDay(day = fromDate.plusDays(7), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, beginning not on the edge" in {

    val fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5)
    val toDate   = LocalDate.now().withMonth(11).withDayOfMonth(8)

    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, fromDate, toDate)

    val fromDateSecond = LocalDate.now().withMonth(11).withDayOfMonth(4)
    val toDateSecond   = LocalDate.now().withMonth(11).withDayOfMonth(7)

    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, fromDateSecond, toDateSecond)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = fromDateSecond.plusDays(0), firstDay = true, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(2), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(3), firstDay = false, lastDay = false)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, ending not on the edge" in {

    val fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5)
    val toDate   = LocalDate.now().withMonth(11).withDayOfMonth(8)
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, fromDate, toDate)

    val fromDateSecond = LocalDate.now().withMonth(11).withDayOfMonth(6)
    val toDateSecond   = LocalDate.now().withMonth(11).withDayOfMonth(9)
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, fromDateSecond, toDateSecond)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = fromDateSecond.plusDays(0), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(2), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(3), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, beginning at the edge" in {

    val fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5)
    val toDate   = LocalDate.now().withMonth(11).withDayOfMonth(6)
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, fromDate, toDate)

    val fromDateSecond = LocalDate.now().withMonth(11).withDayOfMonth(6)
    val toDateSecond   = LocalDate.now().withMonth(11).withDayOfMonth(8)
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, fromDateSecond, toDateSecond)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = fromDateSecond.plusDays(0), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(2), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, ending at the edge" in {

    val fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5)
    val toDate   = LocalDate.now().withMonth(11).withDayOfMonth(7)
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, fromDate, toDate)

    val fromDateSecond = LocalDate.now().withMonth(11).withDayOfMonth(7)
    val toDateSecond   = LocalDate.now().withMonth(11).withDayOfMonth(9)
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, fromDateSecond, toDateSecond)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = fromDateSecond.plusDays(0), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(2), firstDay = false, lastDay = true)
      )

    actualResult shouldBe expectedResult
  }

  "BookedDatesActor with interception, within" in {

    val fromDate = LocalDate.now().withMonth(11).withDayOfMonth(5)
    val toDate   = LocalDate.now().withMonth(11).withDayOfMonth(9)
    val firstPeriod: List[BookedDay] =
      BookedDatesActor.markNewDates(List.empty, fromDate, toDate)

    val fromDateSecond = LocalDate.now().withMonth(11).withDayOfMonth(6)
    val toDateSecond   = LocalDate.now().withMonth(11).withDayOfMonth(8)
    val actualResult: List[BookedDay] =
      BookedDatesActor.markNewDates(firstPeriod, fromDateSecond, toDateSecond)

    val expectedResult: List[BookedDay] =
      List(
        BookedDay(day = fromDateSecond.plusDays(0), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(1), firstDay = false, lastDay = false),
        BookedDay(day = fromDateSecond.plusDays(2), firstDay = false, lastDay = false)
      )

    actualResult shouldBe expectedResult
  }
}
