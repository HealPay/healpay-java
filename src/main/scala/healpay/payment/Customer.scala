package healpay.payment

import scala.collection.mutable.ListBuffer

import com.github.nscala_time.time.Imports._

case class Customer(
  customer_number:String, 
  id:String, 
  notes:String, 
  url:String, 
  created:DateTime, 
  modified:DateTime,

  custom_data:String, 
  custom_fields:List[FieldValue], 

  billing_address:BillingAddress, 
  payment_methods:List[PaymentMethod])

object Customer {
  def fromXml(node:scala.xml.Node):Customer = {
    var payment_methods = ListBuffer.empty[PaymentMethod]
    var custom_fields = ListBuffer.empty[FieldValue]
    //val pp = new scala.xml.PrettyPrinter(80,2)

    for (payment_method <- ( node \ "PaymentMethods" \ "item" )) {
      payment_methods += ( PaymentMethod.fromXml(payment_method) )
    }

    for (field_value <- ( node \ "CustomFields" \ "item" )) {
      custom_fields += ( FieldValue.fromXml(field_value) )
    }

    Customer(
      customer_number = ( node \ "CustNum").text,
      id = ( node \ "CustomerID" ).text,
      notes = ( node \ "Notes" ).text,
      url = ( node \ "URL" ).text,
      created = DateTime.parse(( node \ "Created" ).text),
      modified = DateTime.parse(( node \ "Modified" ).text),

      custom_data = ( node \ "CustomData" ).text,
      custom_fields = custom_fields.toList,

      billing_address = BillingAddress.fromXml(( node \ "BillingAddress")(0)),
      payment_methods = payment_methods.toList)
  }
}
