package hr.com.blanka.apartments

object ValueClasses {

  case class UserId(id: String)  extends AnyVal
  case class UnitId(id: Int)     extends AnyVal
  case class BookingId(id: Long) extends AnyVal

}
