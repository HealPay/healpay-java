package healpay.payment

import java.security.MessageDigest
import javax.xml.bind.annotation.adapters.HexBinaryAdapter

import scalaj.http._

import healpay.payment._

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

  private def send_request(envelope:scala.xml.Elem):scala.xml.Node = {
    val request = s"""<?xml version="1.0" encoding="utf-8"?>""" + envelope
    val result = Http(url).postData(request).header("Content-type", "text/xml; charset=utf-8").asString.body
    scala.xml.XML.loadString(result)
  }

  private def envelope(body:scala.xml.Elem):scala.xml.Elem = {
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

  private def security_token:scala.xml.Elem = {
    val seed = generate_seed
    val hash_value = generate_hash_pin(seed)

    <Token>
      <PinHash>
        <HashValue>{hash_value}</HashValue>
        <Seed>{seed}</Seed>
        <Type>sha1</Type>
      </PinHash>
      <SourceKey>{source_key}</SourceKey>
    </Token>
  }

  private def search_params(params: List[(String, String, String)]):scala.xml.Elem = {
    <Search>
    {
      params.map( param_tuple => {
        val (search_field, search_type, search_value) = param_tuple
        <SearchParam>
          <Field>{search_field}</Field>
          <Type>{search_type}</Type>
          <Value>{search_value}</Value>
        </SearchParam>
      })
    }
    </Search>
  }

  def search_customers(params: List[(String, String, String)], match_all:Boolean = false, start:Integer = 0, limit:Integer = -1, sort:String = ""):CustomerSearchResult = {
    val response = send_request(
      envelope(
        <ns1:searchCustomers>
          {security_token}
          {search_params(params)}
          { if (match_all) <MatchAll>{match_all}</MatchAll> }
          <Start>{start}</Start>
          { if (limit > -1) <Limit>{limit}</Limit> }
          { if (sort.length > 0) <Sort>{sort}</Sort> }
        </ns1:searchCustomers>
      )
    )

    if (( response \\ "Fault" ).length > 0) {
      throw new ServerFaultException(( response \\ "faultstring" ).text)
    }

    val result = response \\ "searchCustomersReturn"
    if (result.length < 1) {
      throw new UnexpectedServerResponseException("Got something other than a customer search response")
    }

    CustomerSearchResult.fromXml(result.head)
  }
}
