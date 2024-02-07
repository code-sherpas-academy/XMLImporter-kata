import factories.CompanyFactory
import importers.XmlImporter
import models.{Company, Salary, Staff}
import org.assertj.core.api.Assertions._
import org.scalatest.funsuite.AnyFunSuite
import sql.PostgreSQLConnector

import java.io.File
import java.nio.file.Path
import java.sql.{Connection, DriverManager, ResultSet}
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters.SeqHasAsJava

class BatchXmlImporterShould extends AnyFunSuite with CompanyFactory {
  implicit private val conn: PostgreSQLConnector = new PostgreSQLConnector()

  val path: Path = Path.of(s"${sys.props("user.dir")}${File.separator}src${File.separator}main${File.separator}resources")

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
        salary.value = resultSet.getInt("value")
        staff.salary = salary
      }
    }
  }

  private def getStaff(companies: ArrayBuffer[Company], conn: Connection): Unit = {
    for (company <- companies) {
      var staffs: ArrayBuffer[Staff] = ArrayBuffer()
      val resultSet: ResultSet = conn.createStatement().executeQuery("SELECT * FROM staff WHERE company_id = " + company.id)
      while (resultSet.next()) {
        var staff: Staff = new Staff()
        staff.id = resultSet.getInt("id")
        staff.firstname = resultSet.getString("first_name")
        staff.lastname = resultSet.getString("last_name")
        staff.nickname = resultSet.getString("nick_name")
        staffs += staff
      }
      company.staff = staffs.toList
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


    val batchXmlImporter: XmlImporter = new XmlImporter
    clearTables()

    val companiesToCreate = batchXmlImporter.importFilesAndConvertToCompany(path)
    companiesToCreate.foreach(createCompany)

    val companies: List[Company] = getAllCompanies
    assertThat(companies.asJava).hasSize(2)
  }
}
