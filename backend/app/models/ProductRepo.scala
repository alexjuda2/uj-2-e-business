package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{Future, ExecutionContext}

@Singleton
class ProductRepo @Inject()(dbConfigProvider: DatabaseConfigProvider, categoryRepo: CategoryRepo)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // TODO: replace with explicit imports

  import dbConfig._
  import profile.api._

  // The starting point for all queries on the table.
  private val productQuery = TableQuery[ProductTable]
  private val categoryQuery = TableQuery[categoryRepo.CategoryTable]

  // TODO: add categoryRepo & cat

  // List all records in the db.
  def all(): Future[Seq[Product]] = db.run {
    productQuery.result
  }

  def getById(id: Long): Future[Option[Product]] = db.run {
    productQuery.filter(_.id === id).result.headOption
  }

  def create(name: String, description: String, category: Long): Future[Product] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (productQuery.map(p => (p.name, p.description, p.category))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning productQuery.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into { case ((name, description, category), id) => Product(id, name, description, category) }
      // And finally, insert the product into the database
      ) += (name, description, category)
  }

  private class ProductTable(tag: Tag) extends Table[Product](tag, "product") {
    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the model object.
     *
     * In this case, we are simply passing parameters to the case class'
     * apply and unapply methods.
     */
    def * = (id, name, description, category) <> ((Product.apply _).tupled, Product.unapply)

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def description = column[String]("description")

    def category = column[Long]("category")

    def category_fk = foreignKey("cat_fk", category, categoryQuery)(_.id)
  }

  // TODO: add remaining repo operations
}
