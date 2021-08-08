package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PromotionRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class PromotionTable(tag: Tag) extends Table[Promotion](tag, "promotion") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def * = (id, name) <> ((Promotion.apply _).tupled, Promotion.unapply)
  }

  // Starting point for DB queries
  val promotionQuery = TableQuery[PromotionTable]

  def all(): Future[Seq[Promotion]] = db.run {
    promotionQuery.result
  }

  def create(name: String): Future[Promotion] = db.run {
    (promotionQuery.map(c => c.name)
      returning promotionQuery.map(_.id)
      into { case (name, id) => Promotion(id, name) }
      ) += (name)
  }

  def getById(id: Long): Future[Option[Promotion]] = db.run {
    promotionQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, promotion: Promotion): Future[Unit] = db.run {
    promotionQuery
      .filter(_.id === id)
      .update(promotion)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    promotionQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
