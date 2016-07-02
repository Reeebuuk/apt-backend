package hr.com.blanka.apartments.validation

import org.joda.time.DateTime

object ErrorMessages {

  def dateIsInPastErrorMessage(rangeSide: String, date: DateTime) = s"$rangeSide date: $date is in the past"
  def toDateBeforeFromDateErrorMessage(from: DateTime, to: DateTime) = s"To date $to is before from date $from"
  def persistingDailyPricesErrorMessage = s"Error while persisting daily prices"
}
