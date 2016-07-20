package hr.com.blanka.apartments.validation

import org.joda.time.LocalDate

object ErrorMessages {

  def dateIsInPastErrorMessage(rangeSide: String, date: LocalDate) = s"$rangeSide date: $date is in the past"
  def toDateBeforeFromDateErrorMessage(from: LocalDate, to: LocalDate) = s"To date $to is before from date $from"
  def persistingDailyPricesErrorMessage = s"Error while persisting daily prices"
}
