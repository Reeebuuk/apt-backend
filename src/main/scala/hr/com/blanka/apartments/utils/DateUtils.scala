package hr.com.blanka.apartments.utils

import com.github.nscala_time.time.Imports._

trait DateUtils {

  def calculateDate(day : Int, month: Int) : Long = {
    val dateFrom = new DateTime().toDateTime(DateTimeZone.UTC).withTime(12, 0, 0, 0)
    dateFrom.withDate(dateFrom.getYear, month, day).getMillis
  }

  def calculateDate(date: Long) : Long = {
    val dateFrom = new DateTime(date).toDateTime(DateTimeZone.UTC).withTime(12, 0, 0, 0)
    dateFrom.withDate(dateFrom.getYear, dateFrom.getMonthOfYear, dateFrom.getDayOfMonth).getMillis
  }

  def previousDay(date: Long) : Long = new DateTime(date).toDateTime(DateTimeZone.UTC).minusDays(1).withTime(12, 0, 0, 0).getMillis

  def afterDay(date: Long) : Long = new DateTime(date).toDateTime(DateTimeZone.UTC).plusDays(1).withTime(12, 0, 0, 0).getMillis

  def formatToString(date: Long): String = {
    val dateLog = new DateTime(date).toDateTime(DateTimeZone.UTC).toString("dd-MM-yyyy")
    dateLog
  }

  def toUTC(date: Long) : Long = new DateTime(date).toDateTime(DateTimeZone.UTC).withTime(12, 0, 0, 0).getMillis

  val startOfTheYear = new DateTime().toDateTime(DateTimeZone.UTC).withTime(12, 0, 0, 0).withMonthOfYear(1).withDayOfMonth(1).getMillis
  val endOfTheYear = new DateTime().toDateTime(DateTimeZone.UTC).withTime(12, 0, 0, 0).withMonthOfYear(12).withDayOfMonth(31).getMillis
}
