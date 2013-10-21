/*
 * Happy Melly Teller
 * Copyright (C) 2013, Happy Melly http://www.happymelly.com
 *
 * This file is part of the Happy Melly Teller.
 *
 * Happy Melly Teller is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Happy Melly Teller is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Happy Melly Teller.  If not, see <http://www.gnu.org/licenses/>.
 *
 * If you have questions concerning this license or the applicable additional terms, you may contact
 * by email Sergey Kotlov, sergey.kotlov@happymelly.com or
 * in writing Happy Melly One, Handelsplein 37, Rotterdam, The Netherlands, 3071 PR
 */

package models

import org.joda.money.{ Money, CurrencyUnit }
import models.database.Accounts
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB.withSession
import play.api.Play.current

case class Account(id: Option[Long], organisationId: Option[Long], personId: Option[Long], currency: CurrencyUnit, active: Boolean) {

  /** Resolves the holder for this account **/
  def accountHolder = (organisationId, personId) match {
    case (Some(o), None) ⇒ Organisation.find(o)
      .orElse(throw new IllegalStateException(s"Organisation with id $o (account holder for account ${id.getOrElse("(NEW)")}) does not exist"))
      .get
    case (None, Some(p)) ⇒ Person.find(p)
      .orElse(throw new IllegalStateException(s"Person with id $p (account holder for account ${id.getOrElse("(NEW)")}) does not exist"))
      .get
    case (None, None) ⇒ Levy
    case _ ⇒ throw new IllegalStateException(s"Account $id has both organisation and person for holder")
  }

  def balance: Money = Money.of(currency, 0)
}

object Account {
  def find(holder: AccountHolder): Account = withSession { implicit session ⇒
    val query = holder match {
      case o: Organisation ⇒ Query(Accounts).filter(_.organisationId === o.id)
      case p: Person ⇒ Query(Accounts).filter(_.personId === p.id)
      case Levy ⇒ Query(Accounts).filter(_.organisationId isNull).filter(_.personId isNull)
    }
    query.first()
  }

  def find(id: Long): Option[Account] = withSession { implicit session ⇒
    Query(Accounts).filter(_.id === id).firstOption()

  }
}

