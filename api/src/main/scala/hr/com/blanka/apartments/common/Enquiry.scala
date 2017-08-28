package hr.com.blanka.apartments.common

import java.time.LocalDate

import ValueClasses.UnitId

case class Enquiry(unitId: UnitId,
                   dateFrom: LocalDate,
                   dateTo: LocalDate,
                   name: String,
                   surname: String,
                   phoneNumber: String,
                   email: String,
                   country: String,
                   animals: String,
                   noOfPeople: String,
                   note: String)
