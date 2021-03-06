package hr.com.blanka.apartments.command

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.pattern.{ ask, pipe }
import hr.com.blanka.apartments.command.booking.{ BookingCommand, CommandBookingActor }
import hr.com.blanka.apartments.command.contact.{ CommandContactActor, ContactCommand }
import hr.com.blanka.apartments.command.price.{ CommandPriceActor, PriceCommand }
import hr.com.blanka.apartments.utils.PredefinedTimeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

object CommandActor {
  def apply() = Props(classOf[CommandActor])
}

class CommandActor extends Actor with ActorLogging with PredefinedTimeout {

  val priceActor: ActorRef   = context.actorOf(CommandPriceActor(), "CommandPriceActor")
  val bookingActor: ActorRef = context.actorOf(CommandBookingActor(), "CommandBookingActor")
  val contactActor: ActorRef = context.actorOf(CommandContactActor(), "CommandContactActor")

  override def receive: Receive = {
    case e: PriceCommand =>
      val msgSender = sender()
      priceActor ? e pipeTo msgSender
    case e: BookingCommand =>
      val msgSender = sender()
      bookingActor ? e pipeTo msgSender
    case e: ContactCommand =>
      val msgSender = sender()
      contactActor ? e pipeTo msgSender
  }
}
