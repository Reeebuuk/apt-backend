package hr.com.blanka.apartments.common

import java.time.LocalDate

import hr.com.blanka.apartments.ValueClasses.UnitId

case class Enquiry(unitId: UnitId,
                   dateFrom: LocalDate,
                   dateTo: LocalDate,
                   name: String,
                   surname: String,
                   phoneNumber: String,
                   email: String,
                   address: String,
                   city: String,
                   country: String,
                   animals: String,
                   noOfPeople: String,
                   note: String)
