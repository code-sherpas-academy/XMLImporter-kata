import converters.XmlConverter
import models.Company

import java.io.File

package object implicits {
  implicit class FileImplicits(file: File) {
    def xmlToCompany: Company = {
      XmlConverter.xmlToCompany(file)
    }
  }
}
