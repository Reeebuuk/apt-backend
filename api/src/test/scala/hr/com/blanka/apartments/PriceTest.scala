package hr.com.blanka.apartments

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.Config
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.Generators._
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.http.model.PriceForRangeResponse
import play.api.libs.json.{ Format, Json }

import scala.language.implicitConversions

class PriceTest extends BaseIntegrationTest {

  import PlayJsonSupport._

  override def testConfig: Config =
    IntegrationConf.config(classOf[PriceTest].getSimpleName)

  "Price service should save multiple prices and fetch results" in {

    val firstPrice = generateSavePriceRangeRequest()
    val firstRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(firstPrice).toString())

    Post("/price", firstRequestEntity) ~> commandPriceRoute(command) ~> check {
      status should be(OK)
    }

    val secondPrice = generateSavePriceRangeRequest(
      from = firstPrice.to,
      to = firstPrice.to.plusDays(5),
      price = BigDecimal(60)
    )
    val secondRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(secondPrice).toString())

    Post("/price", secondRequestEntity) ~> commandPriceRoute(command) ~> check {
      status should be(OK)
    }

    val lookupRequest = generateLookupPriceForRangeRequest(from = firstPrice.from.plusDays(3),
                                                           to = firstPrice.from.plusDays(7))
    val lookupRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(lookupRequest).toString())

    eventually {
      Post("/price/calculate", lookupRequestEntity) ~> queryPriceRoute(query) ~> check {
        Unmarshal(response.entity.httpEntity)
          .to[PriceForRangeResponse]
          .map(
            _ should be(
              PriceForRangeResponse(BigDecimal(270))
            )
          )
        status should be(OK)
      }
    }
  }
}
