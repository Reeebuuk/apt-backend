package hr.com.blanka.apartments.query.contact

import javax.mail._
import javax.mail.internet.{ InternetAddress, MimeMessage }

import akka.actor.{ Actor, ActorLogging, Props }

object EmailSenderActor {
  def apply(emailSettings: EmailSettings) = Props(classOf[EmailSenderActor], emailSettings)
}

class EmailSenderActor(emailSettings: EmailSettings) extends Actor with ActorLogging {

  override def receive: Receive = {
    case email: SendEmail =>
      // Check session length, would rather prefer opening it one off than every time
      log.info(s"Sending email to ${email.to}")
      log.debug(email.toString)
      val session: Session = Session.getDefaultInstance(
        emailSettings.toProperties,
        new Authenticator() {
          override protected def getPasswordAuthentication =
            new PasswordAuthentication(emailSettings.username, emailSettings.password)
        }
      )

      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(email.from))
      message.setRecipients(Message.RecipientType.TO,
                            email.to.map(new InternetAddress(_)).toArray[Address])
      message.setSubject(email.subject)
      message.setText(email.text)
      Transport.send(message)
      sender() ! email.persistenceOffset
      log.info("Email successfully sent/**/")
  }
}
