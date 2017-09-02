package hr.com.blanka.apartments.http.routes

import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.directives.BasicDirectives.extract
import hr.com.blanka.apartments.common.ValueClasses.UserId

trait UserDirectives {

  //fix later
  def extractUser: Directive1[UserId] =
    extract(_.request.headers.find(_.name() == "user") match {
      case None    => UserId("user")
      case Some(u) => UserId(u.value())
    })
}
