package com.decipher.kotlin.testing

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.io.StringReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants.START_ELEMENT


//@JacksonXmlRootElement(na)
data class Country(val name: String, val population: String, val capital: String, val currency: String)

@SpringBootTest
class TestingApplicationTests {

    @Test
    fun contextLoads() {
        val xml = """
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Header/>
	<SOAP-ENV:Body>
		<ns2:getCountryResponse xmlns:ns2="http://spring.io/guides/gs-producing-web-service">
			<ns2:country>
				<ns2:name>Spain</ns2:name>
				<ns2:population>46704314</ns2:population>
				<ns2:capital>Madrid</ns2:capital>
				<ns2:currency>EUR</ns2:currency>
			</ns2:country>
		</ns2:getCountryResponse>
	</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
"""

        val xmlMapper = XmlMapper().apply {
            registerKotlinModule()
        }

        val xif = XMLInputFactory.newInstance()
        val xr = xif.createXMLStreamReader(StringReader(xml))

        while (xr.hasNext()) {
            xr.next()
            if (xr.eventType == START_ELEMENT && xr.localName == "country") {
                println(xr.localName)
//				if ("car" == xr.localName) {
					val country: Country = xmlMapper.readValue(xr, Country::class.java)
					println(country)
//					if ("21056" == car.getPlate()) break
//				}
            }
        }
    }

}
