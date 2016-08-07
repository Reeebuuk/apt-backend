package hr.com.blanka.apartments.utils

import org.joda.time.{Days, LocalDate}

trait HelperMethods {

  def iterateThroughDays(from: LocalDate, to: LocalDate): List[LocalDate] =
    (0 to Days.daysBetween(from, to).getDays).map(from.plusDays).toList

}
