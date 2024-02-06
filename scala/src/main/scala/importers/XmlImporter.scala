package importers

import implicits._
import io.FileManager
import jakarta.xml.bind.JAXBException
import models.Company
import sql.PostgreSQLConnector

import java.io.{File, IOException}
import java.nio.file.Path
import java.sql.SQLException

class XmlImporter {
  private val conn = new PostgreSQLConnector()

  @throws[IOException]
  @throws[JAXBException]
  @throws[SQLException]
  def importFiles(folderPath: Path): Unit = {
    val fileExtension: String = ".xml"
    val paths: List[File] = FileManager.listFilesWithExtension(fileExtension)(folderPath.toFile).toList

    val companies: List[Company] = for {
      path <- paths
    } yield {
      val file: File = new File(path.toString)
      file.xmlToCompany
    }

    companies.foreach { company =>
      processCompany(company)
    }

    conn.closeConnection()
  }

  private def processCompany(company: Company): Unit = {
    val tryCompanyId = conn.insertCompany(company)
    tryCompanyId.map { companyId =>
      for (staff <- company.staff) {
        conn.insertStaff(staff, companyId)
        conn.insertSalary(staff.salary, staff.id)
      }
    }
  }
}
