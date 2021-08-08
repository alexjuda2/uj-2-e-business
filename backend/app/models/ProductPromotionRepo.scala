package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ProductPromotionRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class ProductPromotionTable(tag: Tag) extends Table[ProductPromotion](tag, "productPromotion") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def product = column[Long]("product")
    def promotion = column[Long]("promotion")
    def * = (id, product, promotion) <> ((ProductPromotion.apply _).tupled, ProductPromotion.unapply)
  }

  // Starting point for DB queries
  val productPromotionQuery = TableQuery[ProductPromotionTable]

  def all(): Future[Seq[ProductPromotion]] = db.run {
    productPromotionQuery.result
  }

  def create(product: Long, promotion: Long): Future[ProductPromotion] = db.run {
    (productPromotionQuery.map(c => (c.product, c.promotion))
      returning productPromotionQuery.map(_.id)
      into { case ((product, promotion), id) => ProductPromotion(id, product, promotion) }
      ) += (product, promotion)
  }

  def getById(id: Long): Future[Option[ProductPromotion]] = db.run {
    productPromotionQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, productPromotion: ProductPromotion): Future[Unit] = db.run {
    productPromotionQuery
      .filter(_.id === id)
      .update(productPromotion)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    productPromotionQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
