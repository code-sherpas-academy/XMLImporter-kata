import org.assertj.core.api.Assertions._
import org.scalatest.funsuite.AnyFunSuite
import xmlmodels.{Company, Salary, Staff}

import java.io.File
import java.nio.file.{Path, Paths}
import java.sql.{Connection, DriverManager, ResultSet}
import scala.collection.JavaConverters.asJavaIterableConverter
import scala.collection.mutable.ArrayBuffer

class BatchXmlImporterShould extends AnyFunSuite {

  val path: Path = Paths.get(s"${sys.props("user.dir")}${File.separator}src${File.separator}main${File.separator}resources")

  private def clearTables(): Unit = {
    val conn = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")
    try {
      val preparedStatement = conn.prepareStatement(
        "DELETE FROM salary; DELETE FROM staff; DELETE FROM company")
      preparedStatement.executeUpdate()
    } finally {
      conn.close()
    }
  }

  private def getAllCompanies: List[Company] = {
    val companies: ArrayBuffer[Company] = ArrayBuffer[Company]()
    val conn: Connection = DriverManager.getConnection(
      "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")
    try {
      getCompanies(companies, conn)
      getStaff(companies, conn)
      getSalary(companies, conn)
    } finally {
      conn.close()
    }
    companies.toList
  }

  private def getSalary(companies: ArrayBuffer[Company], conn: Connection): Unit = {
    for {
      company <- companies
      staff <- company.staff
    } yield {
      val resultSet: ResultSet = conn.createStatement().executeQuery("SELECT * FROM salary WHERE staff_id = " + staff.id)
      while (resultSet.next()) {
        val salary: Salary = new Salary()
        salary.currency = resultSet.getString("currency")
        salary.value =  resultSet.getInt("value")
        staff.salary = salary
      }
    }
  }

  private def getStaff(companies: ArrayBuffer[Company], conn: Connection): Unit = {
    for (company <- companies) {
      val staffs: ArrayBuffer[Staff] = ArrayBuffer()
      val resultSet: ResultSet = conn.createStatement().executeQuery("SELECT * FROM staff WHERE company_id = " + company.id)
      while (resultSet.next()) {
        val staff: Staff = new Staff()
        staff.id = resultSet.getInt("id")
        staff.firstname = resultSet.getString("first_name")
        staff.lastname = resultSet.getString("last_name")
        staff.nickname = resultSet.getString("nick_name")
        staffs += staff
      }
      company.staff = staffs.toArray
    }
  }

  private def getCompanies(companies: ArrayBuffer[Company], conn: Connection): Unit = {
    val resultSet: ResultSet = conn.createStatement().executeQuery("SELECT * FROM company")
    while (resultSet.next()) {
      val company: Company = new Company()
      company.id = resultSet.getInt("id")
      company.name = resultSet.getString("name")
      companies += company
    }
  }

  test("import xml into database") {
    val companyImporter: CompanyImporter = CompanyImporter()
    clearTables()

    val parsedCompanies = BatchXmlParser.parseXmlFilesToCompanies(path)
    companyImporter.importCompanies(parsedCompanies)

    val companies: List[Company] = getAllCompanies
    assertThat(companies.asJava).hasSize(2)
  }
}
