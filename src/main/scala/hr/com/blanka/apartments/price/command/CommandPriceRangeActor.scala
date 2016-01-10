package hr.com.blanka.apartments.price.command

import akka.actor.{ActorLogging, Actor, Props}
import hr.com.blanka.apartments.Configured
import hr.com.blanka.apartments.http.routes.SavePriceForRangeDto
import hr.com.blanka.apartments.model.PriceForRange
import hr.com.blanka.apartments.price.PriceCommandProtocol.{PriceForRangeSaved, SavePriceCommandResponse}
import org.scalactic.Good
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.Macros

import scala.concurrent.ExecutionContext.Implicits.global

object CommandPriceRangeActor {

  def apply() = Props(classOf[CommandPriceRangeActor])
}

class CommandPriceRangeActor extends Actor with ActorLogging with Configured {

  implicit val priceForRangeFormat = Macros.handler[PriceForRange]

  override def receive: Receive = {
    case SavePriceForRangeDto(unitId, from, to, price) => {
      val dataSource = configured[DefaultDB]
      val collection = dataSource("priceForRange").asInstanceOf[BSONCollection]

      collection.insert(PriceForRange(unitId, from, to, price))
      sender ! SavePriceCommandResponse(Good(PriceForRangeSaved))
    }
  }

}
