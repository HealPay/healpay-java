package healpay.payment

case class ServerFaultException(message:String) extends Exception(message)
case class UnexpectedServerResponseException(message:String) extends Exception(message)
