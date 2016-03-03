package healpay.payment

case class FieldValue(field:String, value:String)

object FieldValue {
  def fromXml(node:scala.xml.Node):FieldValue = {
    FieldValue(
      ( node \ "Field" ).text, 
      ( node \ "Value" ).text)
  }
}
