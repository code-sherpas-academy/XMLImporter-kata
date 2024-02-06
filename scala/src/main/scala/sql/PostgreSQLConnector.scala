package sql

import models.{Company, Salary, Staff}

import java.sql._
import scala.util.Try

class PostgreSQLConnector() {
  protected val conn: Connection = DriverManager.getConnection(
    "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres", "postgres")

  def closeConnection(): Unit = {
    conn.close()
  }

  /**
   * Insert a company into the database
   * @param company the company to insert
   * @return the id of the company
   */
  def insertCompany(company: Company): Try[Int] = {
    val preparedStatement: PreparedStatement = conn.prepareStatement(
      "INSERT INTO company(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
    preparedStatement.setString(1, company.name)
    preparedStatement.executeUpdate()

    val generatedKeys: ResultSet = preparedStatement.getGeneratedKeys
    Try {
      if (generatedKeys.next()) {
        generatedKeys.getLong(1).toInt
      } else {
        throw new SQLException("No ID obtained.")
      }
    }
  }

  /**
   * Insert a staff into the database
   * @param staff the staff to insert
   * @param companyId the id of the company
   * @return number of rows inserted
   */
  def insertStaff(staff: Staff, companyId: Int): Try[Int] = {
    Try {
      val preparedStatement: PreparedStatement = conn.prepareStatement(
        "INSERT INTO staff(id,company_id, first_name, last_name, nick_name) VALUES (?,?,?,?,?)")
      preparedStatement.setInt(1, staff.id)
      preparedStatement.setInt(2, companyId)
      preparedStatement.setString(3, staff.firstname)
      preparedStatement.setString(4, staff.lastname)
      preparedStatement.setString(5, staff.nickname)
      preparedStatement.executeUpdate()
    }
  }

  /**
   * Insert a salary into the database
   * @param salary the salary to insert
   * @param staffId the id of the staff
   * @return number of rows inserted
   */
  def insertSalary(salary: Salary, staffId: Int): Try[Int] = {
    Try {
      val preparedStatement: PreparedStatement = conn.prepareStatement(
        "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)")
      preparedStatement.setInt(1, staffId)
      preparedStatement.setString(2, salary.currency)
      preparedStatement.setInt(3, salary.value)
      preparedStatement.executeUpdate()
    }
  }
}
