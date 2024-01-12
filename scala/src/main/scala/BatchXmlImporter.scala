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

    val companies: List[Company] = for {
      path <- paths
    } yield {
      val file: File = new File(path.toString)
      val jaxbContext: JAXBContext = JAXBContext.newInstance(classOf[Company])
      val jaxbUnmarshaller: Unmarshaller = jaxbContext.createUnmarshaller
      jaxbUnmarshaller.unmarshal(file).asInstanceOf[Company]
    }

    for (company <- companies) {
      val conn: Connection = DriverManager.getConnection(
        "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")

      try {
        var companyId: Int = 0

        val preparedStatement: PreparedStatement = conn.prepareStatement("INSERT INTO company(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
        preparedStatement.setString(1, company.name)
        preparedStatement.executeUpdate()

        val generatedKeys: ResultSet = preparedStatement.getGeneratedKeys
        if (generatedKeys.next()) {
          companyId = generatedKeys.getLong(1).toInt
        } else {
          throw new SQLException("No ID obtained.")
        }

        for (staff <- company.staff) {
          val preparedStatementStaff: PreparedStatement = conn.prepareStatement(
            "INSERT INTO staff(id,company_id, first_name, last_name, nick_name) VALUES (?,?,?,?,?)")
          preparedStatementStaff.setInt(1, staff.id)
          preparedStatementStaff.setInt(2, companyId)
          preparedStatementStaff.setString(3, staff.firstname)
          preparedStatementStaff.setString(4, staff.lastname)
          preparedStatementStaff.setString(5, staff.nickname)
          preparedStatementStaff.executeUpdate()

          val preparedStatementSalary: PreparedStatement = conn.prepareStatement(
            "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)")
          preparedStatementSalary.setInt(1, staff.id)
          preparedStatementSalary.setString(2, staff.salary.currency)
          preparedStatementSalary.setInt(3, staff.salary.value)
          preparedStatementSalary.executeUpdate()
        }
      } finally {
        if (conn != null) conn.close()
      }
    }
  }

  private def listFiles(fileExtension: String)(file: File): Array[File] = {
    val these: Array[File] = file.listFiles()
    these ++ these.filter(_.isFile).filter(_.toString.endsWith(fileExtension)).flatMap(listFiles(fileExtension))
  }
}
