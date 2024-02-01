package sql

import xmlmodels.{Company, Staff}

import java.sql.{Connection, PreparedStatement, ResultSet, SQLException, Statement}

case class CompanySqlProvider(){

  private val insertCompanyStatement = "INSERT INTO company(name) VALUES (?)"
  private val insertStaffStatement = "INSERT INTO staff(id,company_id, first_name, last_name, nick_name) VALUES (?,?,?,?,?)"
  private val insertSalaryStatement = "INSERT INTO salary(staff_id, currency, value) VALUES (?,?,?)"

  private val sqlProvider = SqlProvider()

  def importCompany(company: Company): Unit = {
    this.sqlProvider.executeCommand(this.importCompany(company, _))
  }

  private def importCompany(company: Company, connection: Connection): Unit = {
    val companyId: Int = this.insertCompanyAndGetNewId(company.name, connection)

    for (staff <- company.staff) {
      this.insertStaff(staff, companyId, connection)
      this.insertSalary(staff, connection)
    }
  }

  //  Breaking SRP here, but I don't want to expose sql internals to the outside world
  @throws[SQLException]
  private def insertCompanyAndGetNewId(companyName: String, connection: Connection): Int = {
    val preparedStatement = prepareConnectionStatement(connection, insertCompanyStatement)
    setInsertCompanyStatementParams(companyName, preparedStatement)
    preparedStatement.executeUpdate()

    getNewCompanyId(preparedStatement)
  }

  private def setInsertCompanyStatementParams(companyName: String, preparedStatement: PreparedStatement): Unit = {
    preparedStatement.setString(1, companyName)
  }

  @throws[SQLException]
  private def getNewCompanyId(preparedStatement: PreparedStatement): Int = {
    val generatedKeys: ResultSet = preparedStatement.getGeneratedKeys
    if (generatedKeys.next()) {
      generatedKeys.getLong(1).toInt
    } else {
      throw new SQLException("No ID obtained.")
    }
  }

  private def insertStaff(staff: Staff, companyId: Int, connection: Connection): Unit = {
    val preparedStatementStaff = prepareConnectionStatement(connection, insertStaffStatement)
    setInsertStaffStatementParams(staff, companyId, preparedStatementStaff)
    preparedStatementStaff.executeUpdate()
  }

  private def setInsertStaffStatementParams(staff: Staff, companyId: Int, preparedStatementStaff: PreparedStatement): Unit = {
    preparedStatementStaff.setInt(1, staff.id)
    preparedStatementStaff.setInt(2, companyId)
    preparedStatementStaff.setString(3, staff.firstname)
    preparedStatementStaff.setString(4, staff.lastname)
    preparedStatementStaff.setString(5, staff.nickname)
  }

  private def insertSalary(staff: Staff, connection: Connection): Unit = {
    val preparedStatementSalary = prepareConnectionStatement(connection, insertSalaryStatement)
    setInsertSalaryStatementParams(staff, preparedStatementSalary)
    preparedStatementSalary.executeUpdate()
  }

  private def setInsertSalaryStatementParams(staff: Staff, preparedStatementSalary: PreparedStatement): Unit = {
    preparedStatementSalary.setInt(1, staff.id)
    preparedStatementSalary.setString(2, staff.salary.currency)
    preparedStatementSalary.setInt(3, staff.salary.value)
  }

  private def prepareConnectionStatement(connection: Connection, statement: String): PreparedStatement = {
    connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)
  }
}
