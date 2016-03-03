package healpay.payment

import scalaj.http._

case class BillingAddress(
  city:String, 
  company:String, 
  country:String, 
  email:String, 
  fax:String, 
  first_name:String, 
  last_name:String, 
  phone:String, 
  state:String, 
  street:String, 
  street2:String, 
  zip:String)

object BillingAddress {
  def fromXml(node: scala.xml.Node):BillingAddress = {
    BillingAddress(( node \ "City" ).text,
    ( node \ "Company" ).text,
    ( node \ "Country" ).text,
    ( node \ "Email" ).text,
    ( node \ "Fax" ).text,
    ( node \ "FirstName" ).text,
    ( node \ "LastName" ).text,
    ( node \ "Phone" ).text,
    ( node \ "State" ).text,
    ( node \ "Street" ).text,
    ( node \ "Street2" ).text,
    ( node \ "Zip" ).text)
  }
}
