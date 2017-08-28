package hr.com.blanka.apartments.util

import java.time.LocalDate

trait Constants {

  val USER_ID                 = "userId"
  val UNIT_ID                 = 1
  val DATE_FROM: LocalDate    = LocalDate.now().withMonth(11).withDayOfMonth(5)
  val DATE_TO: LocalDate      = LocalDate.now().withMonth(11).withDayOfMonth(12)
  val NAME                    = "John"
  val SURNAME                 = "Cockroach"
  val PHONE_NUMBER            = "35395443443"
  val EMAIL                   = "john.cockr@gmail.com"
  val COUNTRY                 = "Portugal"
  val ANIMALS                 = "Donkey"
  val NO_OF_PEOPLE            = "2+2"
  val NOTE                    = "We like camp fire indoors"
  val DEPOSIT_AMOUNT          = BigDecimal(100)
  val CURRENCY                = "EUR"
  val DAY_PRICE               = BigDecimal(50)
  val TIME_SAVED: LocalDate   = LocalDate.now().withMonth(11).withDayOfMonth(2)
  val DEPOSIT_PAID: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(1)
}
