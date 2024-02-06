import java.io.{File, IOException}
import java.nio.file.Path
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException, Statement}
import jakarta.xml.bind.{JAXBContext, JAXBException, Unmarshaller}
import xmlmodels.Company

class BatchXmlImporter {

  @throws[IOException]
  @throws[JAXBException]
  @throws[SQLException]
  def importFiles(folderPath: Path): Unit = {
    val fileExtension: String = ".xml"
    val paths: List[File] = listFiles(fileExtension)(folderPath.toFile).toList

    for (path <- paths) {
      val file: File = new File(path.toString)
      val company: Company = unmarshalXml(file)
      val databaseManager = new DatabaseManager("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")

      try {
        val companyId: Int = databaseManager.persistCompany(company)
        databaseManager.persistStaffAndSalary(company, companyId)
      } finally {
        databaseManager.closeConnection()
      }
    }
  }

  private def unmarshalXml(file: File): Company = {
    val jaxbContext: JAXBContext = JAXBContext.newInstance(classOf[Company])
    val jaxbUnmarshaller: Unmarshaller = jaxbContext.createUnmarshaller
    jaxbUnmarshaller.unmarshal(file).asInstanceOf[Company]
  }

  private def listFiles(fileExtension: String)(file: File): Array[File] = {
    val these: Array[File] = file.listFiles()
    these ++ these.filter(_.isFile).filter(_.toString.endsWith(fileExtension)).flatMap(listFiles(fileExtension))
  }
}