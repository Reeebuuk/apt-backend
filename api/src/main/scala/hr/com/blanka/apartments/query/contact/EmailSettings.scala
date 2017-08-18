package hr.com.blanka.apartments.query.contact

import java.util.Properties

import com.typesafe.config.Config

case class EmailSettings(host: String,
                         socketPort: Int,
                         socketClass: String,
                         auth: Boolean,
                         port: Int,
                         username: String,
                         password: String,
                         fromEmail: String) {
  def toProperties: Properties = {
    val props = new Properties()
    props.put("mail.smtp.host", host)
    props.put("mail.smtp.socketFactory.port", socketPort.toString)
    props.put("mail.smtp.socketFactory.class", socketClass)
    props.put("mail.smtp.auth", auth.toString)
    props.put("mail.smtp.port", port.toString)
    props
  }
}

object EmailSettings {

  def apply(config: Config): EmailSettings = new EmailSettings(
    host = config.getString("host"),
    socketPort = config.getInt("socketFactory.port"),
    socketClass = config.getString("socketFactory.class"),
    auth = config.getBoolean("auth"),
    port = config.getInt("port"),
    username = System.getenv("USERNAME"),
    password = System.getenv("PASSWORD"),
    fromEmail = config.getString("fromEmail")
  )
}
