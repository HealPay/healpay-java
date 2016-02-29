package healpay.payment

import java.security.MessageDigest
import javax.xml.bind.annotation.adapters.HexBinaryAdapter

import scalaj.http._

// TODO: Currently this is just the most bare implementation.
// TODO: Handle server errors
// TODO: Return something better than xml
// TODO: Transaction methods > customers > batch > card batches
class SoapGateway(url:String, source_key:String, pin:String) {
  private def generate_seed:String = System.nanoTime.toString

  private def generate_hash_pin(seed:String):String = {
    val sha = MessageDigest.getInstance("SHA-1")
    val pre_hash = source_key + seed + pin
    val digest = sha.digest(pre_hash.getBytes("UTF-8"))

    (new HexBinaryAdapter).marshal(digest)
  }

  private def generate_security_token:scala.xml.Elem = {
    val seed = generate_seed
    val hash_value = generate_hash_pin(seed)

    <Token xsi:type="ns1:ueSecurityToken">
      <PinHash xsi:type="ns1:ueHash">
        <HashValue xsi:type="xsd:string">{hash_value}</HashValue>
        <Seed xsi:type="xsd:string">{seed}</Seed>
        <Type xsi:type="xsd:string">sha1</Type>
      </PinHash>
      <SourceKey xsi:type="xsd:string">{source_key}</SourceKey>
    </Token>
  }

  private def generate_search_param(search_field:String, search_type:String, search_value:String):scala.xml.Elem = {
    <SearchParam xsi:type="ns1:SearchParam">
      <Field xsi:type="xsd:string">{search_field}</Field>
      <Type xsi:type="xsd:string">{search_type}</Type>
      <Value xsi:type="xsd:string">{search_value}</Value>
    </SearchParam>
  }

  private def generate_envelope(body:scala.xml.Elem):scala.xml.Elem = {
    <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:ns1="urn:healpay" xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
    xmlns:SOAP-ENC="http://schemas.xmlsoap.org/soap/encoding/" 
    SOAP-ENV:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
      <SOAP-ENV:Body>
        {body}
      </SOAP-ENV:Body>
    </SOAP-ENV:Envelope>
  }

  private def generate_request(envelope:scala.xml.Elem):String = {
    val request = s"""<?xml version="1.0" encoding="utf-8"?>""" + envelope

    Http(url).postData(request).header("Content-type", "text/xml; charset=utf-8").asString.body
  }

  // TODO: Handle arguments
  def search_customers:String = {
    generate_request(
      generate_envelope(
        <ns1:searchCustomers>
          {generate_security_token}
          <Search xsi:type="ns1:SearchParamArray">
            {generate_search_param("Enabled", "eq", "true")}
          </Search>
        </ns1:searchCustomers>
      )
    )
  }
}
