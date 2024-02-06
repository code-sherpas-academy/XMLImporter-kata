package models

import jakarta.xml.bind.annotation.{XmlAttribute, XmlElement, XmlRootElement}

@XmlRootElement
class Company {
  var id: Integer = _
  @XmlAttribute
  var name: String = _

  @XmlElement
  var staff: List[Staff] = Nil
}
