package hr.com.blanka.apartments.price.command

import akka.actor.{ActorLogging, Actor, Props}
import hr.com.blanka.apartments.Configured
import hr.com.blanka.apartments.http.routes.SavePriceForRangeDto
import hr.com.blanka.apartments.price.PriceCommandProtocol.{PriceForRangeSaved, SavePriceCommandResponse}
import org.scalactic.Good
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection

import scala.concurrent.ExecutionContext.Implicits.global

object CommandPriceRangeActor {

  def apply() = Props(classOf[CommandPriceRangeActor])
}

class CommandPriceRangeActor extends Actor with ActorLogging with Configured {

  override def receive: Receive = {
    case SavePriceForRangeDto(unitId, from, to, price) => {
      val dataSource = configured[DefaultDB]
      val collection = dataSource("test.collections").asInstanceOf[BSONCollection]

      val lala = collection.count()
      sender ! SavePriceCommandResponse(Good(PriceForRangeSaved))
    }
  }

}
