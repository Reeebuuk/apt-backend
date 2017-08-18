package hr.com.blanka.apartments.query.contact

case class SendEmail(from: String,
                     to: List[String],
                     subject: String,
                     text: String,
                     persistenceOffset: Long)
