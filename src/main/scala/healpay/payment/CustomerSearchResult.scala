package healpay.payment

import scala.collection.mutable.ListBuffer

case class CustomerSearchResult(
  matched:Integer, 
  returned:Integer, 
  start_index:Integer,
  limit:Integer,

  customers:List[Customer])

object CustomerSearchResult {
  def fromXml(node: scala.xml.Node):CustomerSearchResult = {
    var customers = ListBuffer.empty[Customer]
    //val pp = new scala.xml.PrettyPrinter(80,2)

    //println(pp.format(node))
    for (customer <- ( node \ "Customers" \ "item" )) {
      customers += Customer.fromXml(customer)
    }

    CustomerSearchResult(
      matched = ( node \ "CustomersMatched" ).text.toInt,
      returned = ( node \ "CustomersReturned" ).text.toInt,
      start_index = ( node \ "StartIndex" ).text.toInt,
      limit = ( node \ "Limit" ).text.toInt,

      customers = customers.toList
    )
  }
}
