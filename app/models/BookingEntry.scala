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

import models.JodaMoney._
import models.database.BookingEntries
import org.joda.time.LocalDate
import org.joda.money.Money
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.db.slick.DB.withSession
import play.api.Play.current

/**
 * A financial (accounting) bookkeeping entry, which represents money owed from one account to another.
 */
case class BookingEntry(
  id: Option[Long],
  ownerId: Long,
  bookingDate: LocalDate,
  bookingNumber: Option[Int],

  brandId: Long,
  summary: String,
  reference: Option[String],
  description: Option[String],
  url: Option[String],
  referenceDate: LocalDate,

  source: Money,
  sourcePercentage: Int //  from: Account,
  //  fromAmount: Money,
  //  to: Account,
  //  toAmount: Money,
  )

case class BookingEntrySummary(
  bookingNumber: Int,
  bookingDate: LocalDate,

  //  from: Account,
  //  fromAmount: Money,
  //  to: Account,
  //  toAmount: Money,

  source: Money,
  sourcePercentage: Int,
  brand: Brand,
  summary: String)

object BookingEntry {

  /**
   * Returns a list of entries in reverse chronological order of booking date.
   */
  def findAll: List[BookingEntrySummary] = withSession { implicit session ⇒
    val query = for {
      entry ← BookingEntries
      brand ← entry.brand
    } yield (entry.bookingNumber, entry.bookingDate, entry.sourceCurrency -> entry.sourceAmount, entry.sourcePercentage, brand, entry.summary)

    query.sortBy(_._2.desc).mapResult {
      case (number, date, source, percentage, brand, summary) ⇒ BookingEntrySummary(number, date, source, percentage, brand, summary)
    }.list
  }
}