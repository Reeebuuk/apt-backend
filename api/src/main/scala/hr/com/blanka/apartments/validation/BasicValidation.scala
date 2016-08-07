package hr.com.blanka.apartments.validation

import org.joda.time.{Days, LocalDate}
import org.scalactic.Accumulation._
import org.scalactic._

object BasicValidation {

  import ErrorMessages._

  private def notPastDate(date: LocalDate, rangeSide: String) : LocalDate Or One[ErrorMessage] = {
    if (date.getDayOfYear >= new LocalDate().getDayOfYear)
      Good(date)
    else
      Bad(One(dateIsInPastErrorMessage(rangeSide, date)))
  }

  def validateDuration(from: LocalDate, to: LocalDate): Int Or Every[ErrorMessage] = {
    val dates = withGood(notPastDate(from, "From"), notPastDate(to, "To")){ (from, to) => (from, to)}

    dates.flatMap(x => {
      val duration = Days.daysBetween(x._1, x._2).getDays
      if (duration >= 0)
        Good(duration)
      else
        Bad(Every(toDateBeforeFromDateErrorMessage(from, to)))
    })
  }

  // Temp until unit addition side is added
  def validUnitId(unitId: Int) : Int Or One[ErrorMessage] = {
    if (unitId > 0 && unitId < 4)
      Good(unitId)
    else
      Bad(One("Unit id doesn't exist"))
  }
}
