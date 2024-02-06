import java.io.{File, IOException}
import java.nio.file.Path
import xmlmodels.Company

import javax.xml.bind.{JAXBContext, JAXBException, Unmarshaller}

case object BatchXmlParser {

  private val fileExtension = ".xml"

  @throws[IOException]
  @throws[JAXBException]
  def parseXmlFilesToCompanies(folderPath: Path): List[Company] = {
    val paths: List[File] = getXmlFilesFromPath(folderPath)

    for {
      path <- paths
    } yield {
      val file: File = new File(path.toString)
      parseXmlFileToCompany(file)
    }
  }

  private def getFilesFromFolderPath(folderPath: Path): Array[File] = {
    folderPath.toFile.listFiles()
  }

  private def filterFilesByFileExtension(fileExtension: String, these: Array[File]) = {
    these.filter(_.isFile).filter(_.toString.endsWith(fileExtension))
  }

  private def getXmlFilesFromPath(folderPath: Path) = {
    val files: Array[File] = getFilesFromFolderPath(folderPath)
    filterFilesByFileExtension(fileExtension, files).toList
  }

  @throws[JAXBException]
  private def parseXmlFileToCompany(file: File) = {
    val jaxbContext: JAXBContext = JAXBContext.newInstance(classOf[Company])
    val jaxbUnmarshaller: Unmarshaller = jaxbContext.createUnmarshaller
    jaxbUnmarshaller.unmarshal(file).asInstanceOf[Company]
  }
}
