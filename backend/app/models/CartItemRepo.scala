package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CartItemRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class CartItemTable(tag: Tag) extends Table[CartItem](tag, "cartItem") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def quantity = column[Int]("quantity")
    def product = column[Long]("product")
    def user = column[Long]("user")
    def * = (id, quantity, product, user) <> ((CartItem.apply _).tupled, CartItem.unapply)
  }

  // Starting point for DB queries
  val cartItemQuery = TableQuery[CartItemTable]

  def all(): Future[Seq[CartItem]] = db.run {
    cartItemQuery.result
  }

  def create(quantity: Int, product: Long, user: Long): Future[CartItem] = db.run {
    (cartItemQuery.map(c => (c.quantity, c.product, c.user))
      returning cartItemQuery.map(_.id)
      into { case ((quantity, product, user), id) => CartItem(id, quantity, product, user) }
      ) += (quantity, product, user)
  }

  def getById(id: Long): Future[Option[CartItem]] = db.run {
    cartItemQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, cartItem: CartItem): Future[Unit] = db.run {
    cartItemQuery
      .filter(_.id === id)
      .update(cartItem)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    cartItemQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
