package com.decipher.kotlin.testing

import com.decipher.kotlin.countries.wsdl.GetCountryRequest
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.http.*
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.http.HttpMethod

import org.springframework.http.HttpEntity

import java.io.StringReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants


data class Country(val name: String, val population: String, val capital: String, val currency: String)

@RestController
class WSDLController {
    private  val xmlMapper: XmlMapper = XmlMapper().apply {
        registerKotlinModule()
    }

    @PostMapping("/country")
    fun getCountry(@RequestBody request: GetCountryRequest): Country?{
        println("Country: ${request.name}")
        val xmlString = """<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
				  xmlns:gs="http://spring.io/guides/gs-producing-web-service">
   <soapenv:Header/>
   <soapenv:Body>
      <gs:getCountryRequest>
         <gs:name>${request.name?:"Spain"}</gs:name>
      </gs:getCountryRequest>
   </soapenv:Body>
</soapenv:Envelope>"""
        val restTemplate = RestTemplate()
        val header = HttpHeaders()
        header.contentType = MediaType.TEXT_XML
        val entity: HttpEntity<*> = HttpEntity(xmlString, header)
        val response = restTemplate.exchange(
            "http://localhost:8081/ws", HttpMethod.POST, entity,
            String::class.java
        )
        val xif = XMLInputFactory.newInstance()
        val xr = xif.createXMLStreamReader(StringReader(response.body))
        var country: Country? = null
        while (xr.hasNext()) {
            xr.next()
            if (xr.eventType == XMLStreamConstants.START_ELEMENT && xr.localName == "country") {
                println(xr.localName)
                country = xmlMapper.readValue(xr, Country::class.java)
                break
            }
        }
        return country
    }
}
