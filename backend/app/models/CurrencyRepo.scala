package models

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CurrencyRepo @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.{profile, db}
  import profile.api._

  class CurrencyTable(tag: Tag) extends Table[Currency](tag, "currency") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def code = column[String]("code")
    def symbol = column[String]("symbol")
    def * = (id, code, symbol) <> ((Currency.apply _).tupled, Currency.unapply)
  }

  // Starting point for DB queries
  val currencyQuery = TableQuery[CurrencyTable]

  def all(): Future[Seq[Currency]] = db.run {
    currencyQuery.result
  }

  def create(code: String, symbol: String): Future[Currency] = db.run {
    (currencyQuery.map(c => (c.code, c.symbol))
      returning currencyQuery.map(_.id)
      into { case ((code, symbol), id) => Currency(id, code, symbol) }
      ) += (code, symbol)
  }

  def getById(id: Long): Future[Option[Currency]] = db.run {
    currencyQuery.filter(_.id === id).result.headOption
  }

  def update(id: Long, currency: Currency): Future[Unit] = db.run {
    currencyQuery
      .filter(_.id === id)
      .update(currency)
      .map(_ => ())
  }

  def delete(id: Long): Future[Unit] = db.run {
    currencyQuery
      .filter(_.id === id)
      .delete
      .map(_ => ())
  }
}
