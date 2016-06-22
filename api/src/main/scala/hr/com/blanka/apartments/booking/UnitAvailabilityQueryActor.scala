package hr.com.blanka.apartments.booking

import akka.actor.{Actor, ActorLogging}
import hr.com.blanka.apartments.booking.protocol.BookingSaved
import hr.com.blanka.apartments.price.protocol.DayMonth
import org.joda.time.LocalDate

class UnitAvailabilityQueryActor extends Actor with ActorLogging {

  override def receive = active(Map[String, Map[LocalDate, List[Int]]]())

  def active(currentAvailableUnits: Map[String, Map[LocalDate, List[Int]]]): Receive = {
    case BookingSaved(booking, _) =>
      currentAvailableUnits.get(booking.userId) match {
        case None => currentAvailableUnits ++ booking.userId ->
        case Some(availableUnitsPerDays) =>
      }
  }

}
