package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepo @Inject() (dbConfigProvider: DatabaseConfigProvider, cartItemRepo: CartItemRepo, orderItemRepo: OrderItemRepo)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def address = column[String]("address")
    def user = column[Long]("user")
    def * = (id, address, user) <> ((Order.apply _).tupled, Order.unapply)
  }

  // Starting point for DB queries
  val orderQuery = TableQuery[OrderTable]

  def all(): Future[Seq[Order]] = db.run {
    orderQuery.result
  }

  def create(address: String, user: Long): Future[Order] = db.run {
    (orderQuery.map(c => (c.address, c.user))
      returning orderQuery.map(_.id)
      into { case ((address, user), id) => Order(id, address, user) }
      ) += (address, user)
  }

  def getById(id: Long): Future[Option[Order]] = db.run {
    orderQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, order: Order): Future[Unit] = db.run {
    orderQuery
      .filter(_.id === id)
      .update(order)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    orderQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }

  def createOrderFromCart(address: String, user: Long): Future[Unit] = {
    import cartItemRepo.cartItemQuery
    import orderItemRepo.orderItemQuery

    // It would be nice to use a transaction here.
    for {
      userCartItems <- db.run(cartItemQuery.filter(_.user === user).result)
      order <- create(address, user)
      _ <- db.run(cartItemQuery
        .filter(_.user === user)
        .delete
        .map(_ => ()))
      _ <- db.run((orderItemQuery
        .map(c => (c.quantity, c.product, c.order))
        returning orderItemQuery.map(_.id)
        into { case ((quantity, product, order), id) => OrderItem(id, quantity, product, order) })
        ++= userCartItems.map(cartItem => (cartItem.quantity, cartItem.product, order.id)))
    } yield order
  }
}
