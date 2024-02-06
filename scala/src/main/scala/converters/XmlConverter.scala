package converters

import jakarta.xml.bind.{JAXBContext, Unmarshaller}
import models.Company

import java.io.File

object XmlConverter {
  def xmlToCompany(file: File): Company = { // Note: could use a Try here
    val jaxbContext: JAXBContext = JAXBContext.newInstance(classOf[Company])
    val jaxbUnmarshaller: Unmarshaller = jaxbContext.createUnmarshaller
    jaxbUnmarshaller.unmarshal(file).asInstanceOf[Company]
  }
}
