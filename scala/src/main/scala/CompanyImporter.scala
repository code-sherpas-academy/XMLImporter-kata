import sql.CompanySqlProvider
import xmlmodels.Company

import java.sql.SQLException

case class CompanyImporter() {
  private val companySqlProvider = CompanySqlProvider()

  @throws[SQLException]
  def importCompanies(companies: List[Company]): Unit = {
      for (company <- companies) {
        this.companySqlProvider.importCompany(company)
      }
  }
}
