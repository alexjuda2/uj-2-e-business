package models

import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.Result
//import slick.ast.ScalaBaseType.intType
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CategoryRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
//  import profile.api.{Tag, Table, TableQuery }
  import profile.api._

  class CategoryTable(tag: Tag) extends Table[Category](tag, "category") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> ((Category.apply _).tupled, Category.unapply)
  }

  // Starting point for DB queries
  val categoryQuery = TableQuery[CategoryTable]

  def all(): Future[Seq[Category]] = db.run {
    categoryQuery.result
  }

  def create(name: String): Future[Category] = db.run {
    (categoryQuery.map(c => (c.name))
      returning categoryQuery.map(_.id)
      into ((name, id) => Category(id, name))
      ) += (name)
  }

  def getById(id: Long): Future[Option[Category]] = db.run {
    categoryQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, category: Category): Future[Unit] = db.run {
    categoryQuery
      .filter(_.id === id)
      .update(category)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    categoryQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
