import scala.util.Try
import scala.util.Success
import scala.util.Failure

import healpay.payment.SoapGateway

object Sandbox {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      println("You must specify a source_key and pin")
      return
    }
    val url = "https://sandbox.usaepay.com/soap/gate/A47DE151"
    val service_key = args(0)
    val pin = args(1)
    val service = new SoapGateway(url, service_key, pin)
    //val response = service.searchCustomers(List(("BillingAddress.lastname", "eq", "ASearchWithNoValidResults")), true)
    val response = Try(service.searchCustomers(List(("Enabled", "eq", "true")), true))

    response match {
      case Success(result) => println(result)
      case Failure(ex) => {
        println(s"Failure searching customers: ${ex.getMessage}")
        exit(1)
      }
    }
  }
}
