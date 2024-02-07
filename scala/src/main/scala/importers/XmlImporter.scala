package importers

import factories.CompanyFactory
import implicits._
import io.FileManager
import jakarta.xml.bind.JAXBException
import models.Company

import java.io.{File, IOException}
import java.nio.file.Path
import java.sql.SQLException

class XmlImporter extends CompanyFactory{
  @throws[IOException]
  @throws[JAXBException]
  @throws[SQLException]
  def importFilesAndConvertToCompany(folderPath: Path): List[Company] = {
    val fileExtension: String = ".xml"
    val files: List[File] = FileManager.listFilesWithExtension(fileExtension)(folderPath.toFile).toList

    files.map(_.xmlToCompany)
  }
}
