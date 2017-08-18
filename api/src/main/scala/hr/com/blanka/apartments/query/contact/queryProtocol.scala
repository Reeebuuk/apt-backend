package hr.com.blanka.apartments.query.contact

case class SendEmail(from: String,
                     to: String,
                     subject: String,
                     text: String,
                     persistenceOffset: Long)
