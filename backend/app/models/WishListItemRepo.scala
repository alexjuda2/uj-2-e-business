package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WishListItemRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class WishListItemTable(tag: Tag) extends Table[WishListItem](tag, "wishListItem") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def product = column[Long]("product")
    def wishList = column[Long]("wishList")
    def * = (id, product, wishList) <> ((WishListItem.apply _).tupled, WishListItem.unapply)
  }

  // Starting point for DB queries
  val wishListItemQuery = TableQuery[WishListItemTable]

  def all(): Future[Seq[WishListItem]] = db.run {
    wishListItemQuery.result
  }

  def create(product: Long, wishList: Long): Future[WishListItem] = db.run {
    (wishListItemQuery.map(c => (c.product, c.wishList))
      returning wishListItemQuery.map(_.id)
      into { case ((product, wishList), id) => WishListItem(id, product, wishList) }
      ) += (product, wishList)
  }

  def getById(id: Long): Future[Option[WishListItem]] = db.run {
    wishListItemQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, wishListItem: WishListItem): Future[Unit] = db.run {
    wishListItemQuery
      .filter(_.id === id)
      .update(wishListItem)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    wishListItemQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
