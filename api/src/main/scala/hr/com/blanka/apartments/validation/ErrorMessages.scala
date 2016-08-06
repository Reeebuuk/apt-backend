package hr.com.blanka.apartments.validation

import org.joda.time.LocalDate

object ErrorMessages {

  val dateFormat = "YYYY-MM-dd"

  def dateIsInPastErrorMessage(rangeSide: String, date: LocalDate) = s"$rangeSide date: ${date.toString(dateFormat)} is in the past"
  def toDateBeforeFromDateErrorMessage(from: LocalDate, to: LocalDate) = s"To date ${to.toString(dateFormat)} is before from date ${from.toString(dateFormat)}"
  def persistingDailyPricesErrorMessage = s"Error while persisting daily prices"
}
