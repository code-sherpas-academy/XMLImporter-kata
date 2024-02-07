package factories

import models.Company
import sql.PostgreSQLConnector

trait CompanyFactory {
  implicit private val conn: PostgreSQLConnector = new PostgreSQLConnector()

  def createCompany(company: Company): Unit = {
    val tryCompanyId = conn.insertCompany(company)
    tryCompanyId.map { companyId =>
      for (staff <- company.staff) {
        conn.insertStaff(staff, companyId)
        conn.insertSalary(staff.salary, staff.id)
      }
    }
  }
}
