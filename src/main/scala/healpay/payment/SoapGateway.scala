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
  private def generateSeed:String = System.nanoTime.toString

  private def generateHashPin(seed:String):String = {
    val sha = MessageDigest.getInstance("SHA-1")
    val pre_hash = source_key + seed + pin
    val digest = sha.digest(pre_hash.getBytes("UTF-8"))

    (new HexBinaryAdapter).marshal(digest)
  }

  private def sendRequest(name:String, body:scala.xml.Elem):scala.xml.Node = {
    import scala.xml.{Elem, Text, Null, TopScope}

    val request_node = envelope(Elem("ns1", name, Null, TopScope, Text(""))
      .copy(child = securityToken ++ body.child))

    val result = Http(url)
      .postData(request_node.toString)
      .header("Content-type", "text/xml; charset=utf-8")
      .asString.body

    val result_node = scala.xml.XML.loadString(result)

    if (( result_node \\ "Fault" ).length > 0) {
      throw new ServerFaultException(( result_node \\ "faultstring" ).text)
    }

    result_node
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

  private def securityToken:scala.xml.Elem = {
    val seed = generateSeed
    val hash_value = generateHashPin(seed)

    <Token>
      <PinHash>
        <HashValue>{hash_value}</HashValue>
        <Seed>{seed}</Seed>
        <Type>sha1</Type>
      </PinHash>
      <SourceKey>{source_key}</SourceKey>
    </Token>
  }

  private def searchParams(params: List[(String, String, String)]):scala.xml.Elem = {
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

  def searchCustomers(params: List[(String, String, String)], 
                      match_all:Boolean = false, 
                      start:Integer = 0, 
                      limit:Integer = -1,
                      sort:String = ""):CustomerSearchResult = {

    val response = sendRequest("searchCustomers", 
      <body>
        {searchParams(params)}
        { if (match_all) <MatchAll>{match_all}</MatchAll> }
        <Start>{start}</Start>
        { if (limit > -1) <Limit>{limit}</Limit> }
        { if (sort.length > 0) <Sort>{sort}</Sort> }
      </body>)

    val result = response \\ "searchCustomersReturn"
    if (result.length != 1) {
      throw new UnexpectedServerResponseException("Got something other than a single customer search response")
    }

    CustomerSearchResult.fromXml(result.head)
  }
}
