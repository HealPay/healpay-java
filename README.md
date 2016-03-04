# HealPay Payment Gateway Java API

This API is implemented in Scala and is bound to the gateway's SOAP API.

The API is split into two components. Data objects returned by the API, and the methods for performing requests to the gateway.

## Pre-requisites 

Please install both [Scala](http://scala-lang.org) and [sbt](http://scalaxb.org)


## Using and extending the API
Do note that the API is currently 'in development' may not yet be usable. To see an example of how to use the API, please see `src/main/scala/sandbox.scala`

To run the sandbox, use: `sbt "run https://sandbox.healpay.com/soap/gate/SOAPID SOURCE_KEY PIN"`

## Extending the API
Please see the searchCustomers method in `src/main/scala/healpay/SoapGateway.scala` for an example of how to implement an API call.


