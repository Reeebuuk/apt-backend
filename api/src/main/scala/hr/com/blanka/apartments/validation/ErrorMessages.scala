package hr.com.blanka.apartments.validation

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ErrorMessages {

  val dateFormat = DateTimeFormatter.ISO_DATE

  def dateIsInPastErrorMessage(rangeSide: String, date: LocalDate) =
    s"$rangeSide date: ${date.format(dateFormat)} is in the past"

  def toDateBeforeFromDateErrorMessage(from: LocalDate, to: LocalDate) =
    s"To date ${to.format(dateFormat)} is before from date ${from.format(dateFormat)}"

  def persistingDailyPricesErrorMessage = s"Error while persisting daily prices"
}
