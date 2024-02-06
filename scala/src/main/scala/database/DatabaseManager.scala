package database
import xmlmodels.Company
import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException, Statement}


class DatabaseManager(url: String, user: String, password: String) {
  private var conn: Connection = _

  // Connect to the database
  def getConnection: Connection = {
    if (conn == null) {
      conn = DriverManager.getConnection(url, user, password)
    }
    conn
  }

  // Close the connection to save resources
  def closeConnection(): Unit = {
    if (conn != null) {
      conn.close()
      conn = null
    }
  }

  @throws[SQLException]
  // Insert a company into the database
  def persistCompany(company: Company): Int = {
    val conn = getConnection

    var companyId: Int = 0

    try {
      val preparedStatement: PreparedStatement = conn.prepareStatement("INSERT INTO company(name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)
      preparedStatement.setString(1, company.name)
      preparedStatement.executeUpdate()

      val generatedKeys: ResultSet = preparedStatement.getGeneratedKeys
      if (generatedKeys.next()) {
        companyId = generatedKeys.getLong(1).toInt
      } else {
        throw new SQLException("No ID obtained.")
      }
    } finally {
      // No need to close the connection here; it will be closed in BatchXmlImporter
    }

    companyId
  }

  @throws[SQLException]
  def persistStaffAndSalary(company: Company, companyId: Int): Unit = {
    val conn = getConnection

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
  }

  def insertCompaniesAndStaffIntoDatabase(companies: List[Company]): Unit = {
    // Insert the companies and their staff into the database
    for (company <- companies) {
      try {
        val companyId: Int = persistCompany(company)
        persistStaffAndSalary(company, companyId)
      } catch {
        case e: SQLException => e.printStackTrace()
      }
    }
  }
}