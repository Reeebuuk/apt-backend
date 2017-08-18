package hr.com.blanka.apartments.query

case class PersistenceQueryEvent(offset: Long, event: Any)

case class PersistenceOffsetSaved(offset: Long)
