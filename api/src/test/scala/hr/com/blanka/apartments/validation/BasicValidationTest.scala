package hr.com.blanka.apartments.validation

import org.joda.time.DateTime
import org.scalactic.{Bad, Good, Many, One}
import org.scalatest.{Matchers, WordSpec}

class BasicValidationTest extends WordSpec with Matchers{

  import ErrorMessages._

  "validateAndGetDurationInDays" when {
    "from date is before to date" should {
      "return bad result with error message" in {
        val to = new DateTime()
        val from = to.plusDays(1)
        BasicValidation.validateAndGetDurationInDays(from, to) should be (Bad(One(toDateBeforeFromDateErrorMessage(from, to))))
      }
    }
    "from date is in the past" should {
      "return bad result with error message" in {
        val from = new DateTime().minusDays(1)
        val to = from.plusDays(3)
        BasicValidation.validateAndGetDurationInDays(from, to) should be (Bad(One(dateIsInPastErrorMessage("From", from))))
      }
    }
    "both dates are in the past" should {
      "return bad result with two error messages" in {
        val from = new DateTime().minusDays(2)
        val to = new DateTime().minusDays(1)
        BasicValidation.validateAndGetDurationInDays(from, to) should be (Bad(Many(dateIsInPastErrorMessage("From", from), dateIsInPastErrorMessage("To", to))))
      }
    }
    "valid date range is passed in it" should {
      "return the duration between from and to in days" in {
        val from = new DateTime()
        val to = from.plusDays(5)
        BasicValidation.validateAndGetDurationInDays(from, to) should be (Good(5))
      }
    }
  }

}
