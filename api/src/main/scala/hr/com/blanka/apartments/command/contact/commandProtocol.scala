package hr.com.blanka.apartments.command.contact

/*
 * Commands
 */

sealed trait ContactCommand

case class SaveContact(name: String, email: String, text: String) extends ContactCommand

/*
 * Validation
 */

/*
 * Events
 */

case class ContactSaved(name: String, email: String, text: String)
