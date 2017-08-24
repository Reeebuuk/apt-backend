package hr.com.blanka.apartments.validation

import java.time.LocalDate

import hr.com.blanka.apartments.common.ValueClasses.UnitId
import hr.com.blanka.apartments.utils.DateHelperMethods
import org.scalactic.Accumulation._
import org.scalactic._

object BasicValidation extends DateHelperMethods {

  import ErrorMessages._

  private def notPastDate(date: LocalDate, rangeSide: String): LocalDate Or One[ErrorMessage] =
    if (date.getDayOfYear >= LocalDate.now().getDayOfYear)
      Good(date)
    else
      Bad(One(dateIsInPastErrorMessage(rangeSide, date)))

  def getDuration(from: LocalDate, to: LocalDate): Int Or Every[ErrorMessage] = {
    val dates = withGood(notPastDate(from, "From"), notPastDate(to, "To")) { (from, to) =>
      (from, to)
    }

    dates.flatMap(x => {
      val duration = iterateThroughDaysIncludingLast(from = x._1, to = x._2).length
      if (duration >= 0)
        Good(duration)
      else
        Bad(Every(toDateBeforeFromDateErrorMessage(from, to)))
    })
  }

  // Temp until unit addition side is added
  def validUnitId(unitId: UnitId): UnitId Or One[ErrorMessage] =
    if (unitId.id > 0 && unitId.id < 4)
      Good(unitId)
    else
      Bad(One("Unit id doesn't exist"))
}
