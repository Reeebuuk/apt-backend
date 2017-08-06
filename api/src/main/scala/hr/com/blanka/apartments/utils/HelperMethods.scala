package hr.com.blanka.apartments.utils

import java.time.{ Duration, LocalDate }

trait HelperMethods {

  def iterateThroughDays(from: LocalDate, to: LocalDate): List[LocalDate] =
    (0l to Duration.between(from.atStartOfDay(), to.atStartOfDay()).toDays)
      .map(from.plusDays)
      .toList

}

object HelperMethods extends HelperMethods
