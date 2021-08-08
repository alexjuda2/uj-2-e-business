package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class ReviewTable(tag: Tag) extends Table[Review](tag, "review") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def content = column[String]("content")
    def rating = column[Int]("rating")
    def user = column[Long]("user")
    def * = (id, content, rating, user) <> ((Review.apply _).tupled, Review.unapply)
  }

  // Starting point for DB queries
  val reviewQuery = TableQuery[ReviewTable]

  def all(): Future[Seq[Review]] = db.run {
    reviewQuery.result
  }

  def create(content: String, rating: Int, user: Long): Future[Review] = db.run {
    (reviewQuery.map(c => (c.content, c.rating, c.user))
      returning reviewQuery.map(_.id)
      into { case ((content, rating, user), id) => Review(id, content, rating, user) }
      ) += (content, rating, user)
  }

  def getById(id: Long): Future[Option[Review]] = db.run {
    reviewQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, review: Review): Future[Unit] = db.run {
    reviewQuery
      .filter(_.id === id)
      .update(review)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    reviewQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
