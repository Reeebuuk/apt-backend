package hr.com.blanka.apartments.utils

import java.time.{ Duration, LocalDate }

trait DateHelperMethods {

  def iterateThroughDaysIncludingLast(from: LocalDate, to: LocalDate): List[LocalDate] =
    (0l to Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays)
      .map(from.plusDays)
      .toList

  def iterateThroughDaysExcludingLast(from: LocalDate, to: LocalDate): List[LocalDate] =
    (0l until Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays)
      .map(from.plusDays)
      .toList

}

object DateHelperMethods extends DateHelperMethods
