package hr.com.blanka.apartments

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.typesafe.config.Config
import de.heikoseeberger.akkahttpplayjson.PlayJsonSupport
import hr.com.blanka.apartments.Generators._
import hr.com.blanka.apartments.Main._
import hr.com.blanka.apartments.http.model.PriceForRangeResponse
import play.api.libs.json.Json

import scala.language.implicitConversions

class PriceTest extends BaseIntegrationTest {

  import PlayJsonSupport._

  override def testConfig: Config =
    IntegrationConf.config(classOf[PriceTest].getSimpleName, cassandraPort)

  "Price service should save multiple prices and fetch results" in {

    val firstPrice = generateSavePriceRangeRequest(price = BigDecimal(1)) //5.11-12.11
    val firstRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(firstPrice).toString())

    Post("/price", firstRequestEntity) ~> commandPriceRoute(command) ~> check {
      status should be(OK)
    }

    val secondPrice = generateSavePriceRangeRequest(
      from = firstPrice.to.minusDays(1), //11.11-17.11
      to = firstPrice.to.plusDays(5),
      price = BigDecimal(10)
    )
    val secondRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(secondPrice).toString())

    Post("/price", secondRequestEntity) ~> commandPriceRoute(command) ~> check {
      status should be(OK)
    }

    val lookupRequest =
      generateLookupPriceForRangeRequest(from = firstPrice.from.plusDays(3), //8.11
                                         to = firstPrice.from.plusDays(8)) //12.11
    val lookupRequestEntity =
      HttpEntity(MediaTypes.`application/json`, Json.toJson(lookupRequest).toString())

    eventually {
      Post("/price/calculate", lookupRequestEntity) ~> queryPriceRoute(query) ~> check {
        Unmarshal(response.entity.httpEntity)
          .to[PriceForRangeResponse]
          .value
          .get
          .get
          .price should be(BigDecimal(23))
      }
    }
  }
}
