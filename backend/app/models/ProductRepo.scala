package models

import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.Result
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductRepo @Inject()(dbConfigProvider: DatabaseConfigProvider, categoryRepo: CategoryRepo, currencyRepo: CurrencyRepo)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  private class ProductTable(tag: Tag) extends Table[Product](tag, "product") {
    def * = (id, name, description, category, currency) <> ((Product.apply _).tupled, Product.unapply)
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def description = column[String]("description")
    def category = column[Long]("category")
    def currency = column[Long]("currency")

    def categoryFk = foreignKey("cat_fk", category, categoryQuery)(_.id)
    def currencyFk = foreignKey("currency_fk", currency, currencyQuery)(_.id)
  }


  // Starting point for DB queries
  private val productQuery = TableQuery[ProductTable]
  private val categoryQuery = TableQuery[categoryRepo.CategoryTable]
  private val currencyQuery = TableQuery[currencyRepo.CurrencyTable]


  def all(): Future[Seq[Product]] = db.run {
    productQuery.result
  }

  def create(name: String, description: String, category: Long, currency: Long): Future[Product] = db.run {
    (productQuery.map(p => (p.name, p.description, p.category, p.currency))
      returning productQuery.map(_.id)
      into { case ((name, description, category, currency), id) => Product(id, name, description, category, currency) }
      ) += (name, description, category, currency)
  }

  def getById(id: Long): Future[Option[Product]] = db.run {
    productQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, product: Product): Future[Unit] = db.run {
    productQuery
      .filter(_.id === id)
      .update(product)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    productQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
