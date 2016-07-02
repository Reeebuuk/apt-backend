package hr.com.blanka.apartments.validation

import org.joda.time.{DateTime, Days}
import org.scalactic.Accumulation._
import org.scalactic._

object BasicValidation {

  import ErrorMessages._

  private def notPastDate(date: DateTime, rangeSide: String) : DateTime Or One[ErrorMessage] = {
    if (date.getDayOfYear >= new DateTime().getDayOfYear)
      Good(date)
    else
      Bad(One(dateIsInPastErrorMessage(rangeSide, date)))
  }

  def validateAndGetDurationInDays(from: DateTime, to: DateTime): Int Or Every[ErrorMessage] = {
    val dates = withGood(notPastDate(from, "From"), notPastDate(to, "To")){ (from, to) => (from, to)}

    dates.flatMap(x => {
      val duration = Days.daysBetween(x._1.toLocalDate, x._2.toLocalDate).getDays
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
