package hr.com.blanka.apartments.utils

import akka.util.Timeout

import scala.concurrent.duration._
import scala.language.postfixOps

trait PredefinedTimeout {

  implicit val timeout: Timeout = Timeout(10 seconds)
}
