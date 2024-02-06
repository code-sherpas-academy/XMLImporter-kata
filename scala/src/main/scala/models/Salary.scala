package models

import jakarta.xml.bind.annotation.{XmlAttribute, XmlRootElement, XmlValue}

@XmlRootElement
class Salary {
  @XmlAttribute
  var currency: String = _

  @XmlValue
  var value: Int = _
}
