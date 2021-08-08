package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WishListRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class WishListTable(tag: Tag) extends Table[WishList](tag, "wishList") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def user = column[Long]("user")
    def * = (id, name, user) <> ((WishList.apply _).tupled, WishList.unapply)
  }

  // Starting point for DB queries
  val wishListQuery = TableQuery[WishListTable]

  def all(): Future[Seq[WishList]] = db.run {
    wishListQuery.result
  }

  def create(name: String, user: Long): Future[WishList] = db.run {
    (wishListQuery.map(c => (c.name, c.user))
      returning wishListQuery.map(_.id)
      into { case ((name, user), id) => WishList(id, name, user) }
      ) += (name, user)
  }

  def getById(id: Long): Future[Option[WishList]] = db.run {
    wishListQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, wishList: WishList): Future[Unit] = db.run {
    wishListQuery
      .filter(_.id === id)
      .update(wishList)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    wishListQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
