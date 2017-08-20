package hr.com.blanka.apartments.query

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings }
import akka.pattern.{ ask, pipe }
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import hr.com.blanka.apartments.command.price.DailyPriceSaved
import hr.com.blanka.apartments.query.booking.{ BookingQuery, QueryBookingActor }
import hr.com.blanka.apartments.query.contact.{
  EmailSenderActor,
  EmailSettings,
  QueryBookingsForEmailsActor,
  QueryContactEmailsActor
}
import hr.com.blanka.apartments.query.price.{ PriceQuery, QueryPriceActor }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.language.postfixOps

object QueryActor {
  def apply(materializer: ActorMaterializer) = Props(classOf[QueryActor], materializer)
}

class QueryActor(materializer: ActorMaterializer) extends Actor with ActorLogging {

  implicit val timeout = Timeout(10 seconds)

  val commandSideReaderActor: ActorRef = ClusterSharding(context.system).start(
    typeName = "synchronizeBookingActor",
    entityProps = CommandSideReaderActor(materializer),
    settings = ClusterShardingSettings(context.system),
    extractEntityId = CommandSideReaderActor.extractEntityId,
    extractShardId = CommandSideReaderActor.extractShardId
  )

  val priceActor: ActorRef =
    context.actorOf(QueryPriceActor(commandSideReaderActor), "QueryPriceActor")
  val bookingActor: ActorRef =
    context.actorOf(QueryBookingActor(commandSideReaderActor), "QueryBookingActor")

  val emailSettings = EmailSettings(ConfigFactory.load("email"))
  val emailSenderActor: ActorRef =
    context.actorOf(EmailSenderActor(emailSettings), "EmailSenderActor")

  val queryContactEmailsActor: ActorRef =
    context.actorOf(
      QueryContactEmailsActor(commandSideReaderActor, emailSenderActor, emailSettings.fromEmail),
      "QueryContactEmailsActor"
    )
  val queryBookingsForEmailsActor: ActorRef =
    context.actorOf(
      QueryBookingsForEmailsActor(commandSideReaderActor,
                                  emailSenderActor,
                                  emailSettings.fromEmail),
      "QueryBookingsForEmailsActor"
    )

  override def receive: Receive = {
    case e: DailyPriceSaved =>
      val msgSender = sender()
      priceActor ? e pipeTo msgSender
    case e: PriceQuery =>
      val msgSender = sender()
      priceActor ? e pipeTo msgSender
    case e: BookingQuery =>
      val msgSender = sender()
      bookingActor ? e pipeTo msgSender
  }
}
