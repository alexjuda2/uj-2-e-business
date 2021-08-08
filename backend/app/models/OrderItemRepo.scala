package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderItemRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class OrderItemTable(tag: Tag) extends Table[OrderItem](tag, "orderItem") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def quantity = column[Int]("quantity")
    def product = column[Long]("product")
    def order = column[Long]("order")
    def * = (id, quantity, product, order) <> ((OrderItem.apply _).tupled, OrderItem.unapply)
  }

  // Starting point for DB queries
  val orderItemQuery = TableQuery[OrderItemTable]

  def all(): Future[Seq[OrderItem]] = db.run {
    orderItemQuery.result
  }

  def create(quantity: Int, product: Long, order: Long): Future[OrderItem] = db.run {
    (orderItemQuery.map(c => (c.quantity, c.product, c.order))
      returning orderItemQuery.map(_.id)
      into { case ((quantity, product, order), id) => OrderItem(id, quantity, product, order) }
      ) += (quantity, product, order)
  }

  def getById(id: Long): Future[Option[OrderItem]] = db.run {
    orderItemQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, orderItem: OrderItem): Future[Unit] = db.run {
    orderItemQuery
      .filter(_.id === id)
      .update(orderItem)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    orderItemQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
