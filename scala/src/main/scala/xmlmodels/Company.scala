package xmlmodels

import javax.xml.bind.annotation._

@XmlRootElement
class Company {
  var id: Integer = _
  @XmlAttribute
  var name: String = _

  @XmlElement
  var staff: Array[Staff] = Array.empty
}
