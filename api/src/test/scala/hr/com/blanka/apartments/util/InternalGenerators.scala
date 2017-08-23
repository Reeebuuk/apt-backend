package hr.com.blanka.apartments.util

import java.time.LocalDate

import hr.com.blanka.apartments.ValueClasses.{ UnitId, UserId }
import hr.com.blanka.apartments.common.Enquiry

object InternalGenerators extends Constants {

  def generateEnquiry(unitId: UnitId = UnitId(UNIT_ID),
                      fromDate: LocalDate = DATE_FROM,
                      toDate: LocalDate = DATE_TO,
                      name: String = NAME,
                      surname: String = SURNAME,
                      phoneNumber: String = PHONE_NUMBER,
                      email: String = EMAIL,
                      address: String = ADDRESS,
                      city: String = CITY,
                      country: String = COUNTRY,
                      animals: String = ANIMALS,
                      noOfPeople: String = NO_OF_PEOPLE,
                      note: String = NOTE): Enquiry =
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
