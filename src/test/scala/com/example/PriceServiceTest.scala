package com.example

import akka.actor.Props
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{HttpEntity, MediaTypes}
import com.example.crudapi.http.BaseService
import com.example.crudapi.http.routes.{CalculatePriceForRangeDto, PriceForRangeDto}
import com.example.crudapi.utils.DateUtils
import org.joda.time.{DateTime, DateTimeZone}
import org.json4s.DefaultFormats
import org.scalatest.concurrent.ScalaFutures
import spray.json._

class PriceServiceTest extends BaseServiceTest with ScalaFutures with BaseService with DateUtils with SprayJsonSupport {

  implicit val ec = system.dispatcher

  val processor = system.actorOf(Props(), "processorActor")
  val view = system.actorOf(Props(), "processorActor")

  implicit val format = DefaultFormats

  "Price service" should {
    "retrieve price for single day" in {
      val today = new DateTime().toDateTime(DateTimeZone.UTC).withTime(12, 0, 0, 0).getMillis
      val tomorrow = afterDay(today)
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[JsArray] should be(PriceForRangeDto(1, BigDecimal(35)).toJson)
      }
    }

    "return correct price if the duration is 7 day in same price range" in {
      val today = new DateTime().toDateTime(DateTimeZone.UTC).withTime(12, 0, 0, 0).getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[JsArray] should be(PriceForRangeDto(1, BigDecimal(245)).toJson)
      }
    }

    "return correct price if the duration is 7 day in different price ranges" in {
      val today = new DateTime().toDateTime(DateTimeZone.UTC).withDate(2015, 7, 19).withTime(12, 0, 0, 0).getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[JsArray] should be(PriceForRangeDto(1, BigDecimal(340)).toJson)
      }
    }

    "return correct price if the duration is 7 day in different years" in {
      val today = new DateTime().toDateTime(DateTimeZone.UTC).withDate(2015, 12, 30).withTime(12, 0, 0, 0).getMillis
      val tomorrow = new DateTime(today).plusDays(7).getMillis
      val requestEntity = HttpEntity(MediaTypes.`application/json`, CalculatePriceForRangeDto(1, today, tomorrow).toJson.toString())

      Post("/v1/price/calculate", requestEntity) ~> routes(processor, view) ~> check {
        responseAs[JsArray] should be(PriceForRangeDto(1, BigDecimal(245)).toJson)
      }
    }


    /*    "retrieve customer by id" in {
          Get("/customers/1") ~> customersRoute ~> check {
            responseAs[JsObject] should be(testCustomers.head.toJson)
          }
        }

        "update customer by id and retrieve it" in {
          val newCustomerfirstname = "UpdatedCustomerfirstname"
          val requestEntity = HttpEntity(MediaTypes.`application/json`, JsObject("firstname" -> JsString(newCustomerfirstname)).toString())
          Post("/customers/1", requestEntity) ~> customersRoute ~> check {
            responseAs[JsObject] should be(testCustomers.head.copy(firstname = newCustomerfirstname).toJson)
            //        whenReady(getCustomerById(1)) { result =>
            //          result.get.firstname should be(newCustomerfirstname)
            //        }
          }
        }*/

  }

}

