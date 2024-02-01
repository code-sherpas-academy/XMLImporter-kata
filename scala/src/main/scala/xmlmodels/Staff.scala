package xmlmodels

import javax.xml.bind.annotation.{XmlAttribute, XmlElement, XmlRootElement}

@XmlRootElement
class Staff extends Serializable {
  @XmlAttribute
  var id: Integer = _
  @XmlElement
  var firstname: String = _
  @XmlElement
  var lastname: String = _
  @XmlElement
  var nickname: String = _
  @XmlElement
  var salary: Salary = _
}
