# HealPay Payment Gateway Java API

This API is implemented in Scala and is bound to the gateway's SOAP API.

The API is split into two components. Data objects returned by the API, and the methods for performing requests to the gateway.

## Pre-requisites 

Please install both [Scala](http://scala-lang.org) and [sbt](http://scalaxb.org)


## Using the API
Do note that the API is currently 'in development' may not yet be usable. The current API is defined in `src/main/scala/healpay/SoapGateway.scala`. To see an example of the API in use, see `src/main/scala/sandbox.scala`

## Extending the API
Please see the searchCustomers method in `src/main/scala/healpay/SoapGateway.scala` for an example of how to implement an API call.

```
# Building the jar file
sbt packageBin

# Generating documentation
sbt doc

# Runing tests
sbt test

# Running the sandbox demo application
sbt "run https://sandbox.healpay.com/soap/gate/SOAPID SOURCE_KEY PIN"
```
