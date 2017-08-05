package hr.com.blanka.apartments.command

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.pattern.{ ask, pipe }
import akka.util.Timeout
import hr.com.blanka.apartments.command.booking.{ BookingCommand, CommandBookingActor }
import hr.com.blanka.apartments.command.price.{ CommandPriceActor, PriceCommand }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object CommandActor {
  def apply() = Props(classOf[CommandActor])
}

class CommandActor extends Actor with ActorLogging {

  implicit val timeout = Timeout(10 seconds)

  val priceActor: ActorRef   = context.actorOf(CommandPriceActor(), "CommandPriceActor")
  val bookingActor: ActorRef = context.actorOf(CommandBookingActor(), "CommandBookingActor")

  override def receive: Receive = {
    case e: PriceCommand =>
      val msgSender = sender()
      priceActor ? e pipeTo msgSender
    case e @ (_: BookingCommand) =>
      val msgSender = sender()
      bookingActor ? e pipeTo msgSender
  }
}
