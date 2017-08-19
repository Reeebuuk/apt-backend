package hr.com.blanka.apartments

import java.time.LocalDate

import hr.com.blanka.apartments.ValueClasses.{ UnitId, UserId }
import hr.com.blanka.apartments.common.Enquiry

object InternalGenerators {

  val USER_ID = UserId("userId")
  val UNIT_ID = UnitId(1)

  def generateEnquiry(unitId: UnitId = UNIT_ID,
                      fromDate: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(5),
                      toDate: LocalDate = LocalDate.now().withMonth(11).withDayOfMonth(12),
                      name: String = "John",
                      surname: String = "Cockroach",
                      phoneNumber: String = "+35395443443",
                      email: String = "john.cockroach@gmail.com",
                      address: String = "7 Street",
                      city: String = "London",
                      country: String = "UK",
                      animals: String = "One elephant",
                      noOfPeople: String = "2+2",
                      note: String = "We like to barbecue indoors"): Enquiry =
    Enquiry(
      unitId = unitId,
      dateFrom = fromDate,
      dateTo = toDate,
      name = name,
      surname = surname,
      phoneNumber = phoneNumber,
      email = email,
      address = address,
      city = city,
      country = country,
      animals = animals,
      noOfPeople = noOfPeople,
      note = note
    )
}
