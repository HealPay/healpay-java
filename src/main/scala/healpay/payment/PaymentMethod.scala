package healpay.payment

import com.github.nscala_time.time.Imports._

case class CreditCard(
  card_number:String,
  card_expiration:String,
  avs_street:String,
  avs_zip:String,
  card_code:String,
  card_type:String)

object CreditCard {
  def fromXml(node:scala.xml.Node):CreditCard = {
    CreditCard(
      card_number     = ( node \ "CardNumber" ).text,
      card_expiration = ( node \ "CardExpiration" ).text,
      avs_street      = ( node \ "AvsStreet" ).text,
      avs_zip         = ( node \ "AvsZip" ).text,
      card_code       = ( node \ "CardCode" ).text,
      card_type       = ( node \ "CardType").text )
  }
}
case class ElectronicCheck(
  account:String,
  account_type:String,
  routing:String,
  drivers_license:String,
  drivers_license_state:String,
  record_type:String)

object ElectronicCheck {
  def fromXml(node:scala.xml.Node):ElectronicCheck = {
    ElectronicCheck(
      account               = ( node \ "Account" ).text, 
      account_type          = ( node \ "AccountType" ).text, 
      routing               = ( node \ "Routing" ).text, 
      drivers_license       = ( node \ "DriversLicense" ).text, 
      drivers_license_state = ( node \ "DriversLicenseState" ).text, 
      record_type           = ( node \ "RecordType").text )
  }
}

case class PaymentMethod(
  method_type:String,
  method_id:Integer,
  method_name:String,
  secondary_sort:Integer,
  created:DateTime,
  modified:DateTime,
  max_balance:Double,
  balance:Double,
  auto_reload:Boolean,
  reload_schedule:String,
  reload_threshold:Double,
  reload_amount:Double,
  reload_method_id:Integer,

  credit_card:CreditCard,
  electronic_check:ElectronicCheck)

object PaymentMethod {
  def fromXml(node:scala.xml.Node):PaymentMethod = {
    PaymentMethod(
      method_type      = ( node \ "MethodType" ).text,
      method_id        = ( node \ "MethodID" ).text.toInt,
      method_name      = ( node \ "MethodName" ).text,
      secondary_sort   = ( node \ "SecondarySort" ).text.toInt,
      created          = DateTime.parse(( node \ "Created" ).text),
      modified         = DateTime.parse(( node \ "Modified" ).text),
      max_balance      = ( node \ "MaxBalance" ).text.toDouble,
      balance          = ( node \ "Balance" ).text.toDouble,
      auto_reload      = ( node \ "AutoReload" ).text.toBoolean,
      reload_schedule  = ( node \ "ReloadSchedule" ).text,
      reload_threshold = ( node \ "ReloadThreshold" ).text.toDouble,
      reload_amount    = ( node \ "ReloadAmount" ).text.toDouble,
      reload_method_id = ( node \ "ReloadMethodID" ).text.toInt,
      
      credit_card      = CreditCard.fromXml(( node \ "CreditCardData" )(0)),

      electronic_check = ElectronicCheck.fromXml(( node \ "CheckData")(0)))
  }
}
