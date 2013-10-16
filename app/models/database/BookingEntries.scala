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

package models.database

import com.github.tototoshi.slick.JodaSupport._

import models.JodaMoney._
import models.{ BookingEntry, License }
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

/**
 * `BookingEntry` database table mapping.
 */
object BookingEntries extends Table[BookingEntry]("BOOKING_ENTRY") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def ownerId = column[Long]("OWNER_ID")
  def bookingDate = column[LocalDate]("BOOKING_DATE")
  def bookingNumber = column[Int]("BOOKING_NUMBER")

  def brandId = column[Long]("BRAND_ID")
  def summary = column[String]("SUMMARY")
  def description = column[Option[String]]("DESCRIPTION")
  def url = column[Option[String]]("URL")
  def reference = column[Option[String]]("REFERENCE")
  def referenceDate = column[LocalDate]("REFERENCE_DATE")

  def sourceCurrency = column[String]("SOURCE_CURRENCY", O.DBType("CHAR(3)"))
  def sourceAmount = column[BigDecimal]("SOURCE_AMOUNT", O.DBType("DECIMAL(13,3)"))
  def sourcePercentage = column[Int]("SOURCE_PERCENTAGE")

  def owner = foreignKey("OWNER_FK", ownerId, People)(_.id)
  def brand = foreignKey("BRAND_FK", brandId, Brands)(_.id)

  def * = id.? ~ ownerId ~ bookingDate ~ bookingNumber.? ~ brandId ~ summary ~ description ~ url ~ reference ~
    referenceDate ~ sourceCurrency ~ sourceAmount ~ sourcePercentage <> (
      { (e) ⇒
        e match {
          case (id, ownerId, bookingDate, bookingNumber, brandId, summary, description, url, reference, referenceDate, sourceCurrency, sourceAmount, sourcePercentage) ⇒
            BookingEntry(id, ownerId, bookingDate, bookingNumber, brandId, summary, description, url, reference, referenceDate, sourceCurrency -> sourceAmount, sourcePercentage)
        }
      },
      { (e: BookingEntry) ⇒
        Some((e.id, e.ownerId, e.bookingDate, e.bookingNumber, e.brandId, e.summary, e.description, e.url,
          e.reference, e.referenceDate, e.source.getCurrencyUnit.getCode, e.source.getAmount, e.sourcePercentage))
      })

}