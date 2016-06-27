package hr.com.blanka.apartments.command

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout
import hr.com.blanka.apartments.command.booking.{BookingCommand, CommandBookingActor}
import hr.com.blanka.apartments.command.price.{CommandPriceActor, PriceCommand}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object CommandActor {
  def apply() = Props(classOf[CommandActor])
}

class CommandActor extends Actor with ActorLogging {

  implicit val timeout = Timeout(3 seconds)

  val priceActor = context.actorOf(CommandPriceActor(), "CommandPriceActor")
  val bookingActor = context.actorOf(CommandBookingActor(), "CommandBookingActor")

  override def receive: Receive = {
    case e : PriceCommand =>
      val msgSender = sender()
      (priceActor ? e).map(msgSender ! _)
    case e : BookingCommand =>
      val msgSender = sender()
      (bookingActor ? e).map(msgSender ! _)
  }
}
