/*
 *
 * Credit Card Number
 * https://github.com/sualeh/credit_card_number
 * Copyright (c) 2014-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package us.fatehi.creditcardnumber;


import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static us.fatehi.creditcardnumber.Utility.non_digit;

import java.io.Serializable;
import java.util.Date;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.YearMonth;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Parses and represents a card expiration date.
 */
public final class ExpirationDate
  extends BaseRawData
  implements Serializable
{

  private static final DateTimeFormatter formatter = DateTimeFormatter
    .ofPattern("yyMM");

  private static final long serialVersionUID = 422773685360335298L;

  private final YearMonth expirationDate;

  /**
   * No expiration date.
   */
  public ExpirationDate()
  {
    this((String) null);
  }

  /**
   * Expiration date from date.
   *
   * @param date
   *        Date
   */
  public ExpirationDate(final Date date)
  {
    super(null);
    if (date != null)
    {
      expirationDate = YearMonth.of(date.getYear() + 1900, date.getMonth() + 1);
    }
    else
    {
      expirationDate = null;
    }
  }

  /**
   * Expiration date from year and month.
   *
   * @param year
   *        Year
   * @param month
   *        Month
   */
  public ExpirationDate(final int year, final int month)
  {
    super(null);
    expirationDate = YearMonth.of(year, month);
  }

  /**
   * Expiration date parsed from raw track data.
   *
   * @param rawExpirationDate
   *        Raw track data for expiration date.
   */
  public ExpirationDate(final String rawExpirationDate)
  {
    super(rawExpirationDate);
    final String expirationDateString = non_digit
      .matcher(trimToEmpty(rawExpirationDate)).replaceAll("");
    YearMonth expirationDate;
    try
    {
      expirationDate = YearMonth.parse(expirationDateString, formatter);
    }
    catch (final Exception e)
    {
      expirationDate = null;
    }
    this.expirationDate = expirationDate;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof ExpirationDate))
    {
      return false;
    }
    final ExpirationDate other = (ExpirationDate) obj;
    if (expirationDate == null)
    {
      if (other.expirationDate != null)
      {
        return false;
      }
    }
    else if (!expirationDate.equals(other.expirationDate))
    {
      return false;
    }
    return true;
  }

  /**
   * @see us.fatehi.creditcardnumber.RawData#exceedsMaximumLength()
   */
  @Override
  public boolean exceedsMaximumLength()
  {
    return trimToEmpty(getRawData()).length() > 4;
  }

  /**
   * Gets the card expiration date. Returns null if no date is
   * available.
   *
   * @return Card expiration date.
   */
  public YearMonth getExpirationDate()
  {
    return expirationDate;
  }

  /**
   * Gets the card expiration date, as a java.util.Date object. Returns
   * null if no date is available.
   *
   * @return Card expiration date.
   */
  public Date getExpirationDateAsDate()
  {
    if (hasExpirationDate())
    {
      final LocalDateTime endOfMonth = expirationDate.atEndOfMonth()
        .atStartOfDay().plus(1, ChronoUnit.DAYS).minus(1, ChronoUnit.NANOS);
      final Instant instant = endOfMonth.atZone(ZoneId.systemDefault())
        .toInstant();
      final Date date = new Date(instant.toEpochMilli());
      return date;
    }
    else
    {
      return null;
    }
  }

  /**
   * Checks whether the card expiration date is available.
   *
   * @return True if the card expiration date is available.
   */
  public boolean hasExpirationDate()
  {
    return expirationDate != null;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (expirationDate == null? 0: expirationDate.hashCode());
    return result;
  }

  /**
   * Whether the card has expired.
   *
   * @return True if the the card has expired.
   */
  public boolean isExpired()
  {
    if (!hasExpirationDate())
    {
      return true;
    }
    else
    {
      return expirationDate.atEndOfMonth().isBefore(LocalDate.now());
    }
  }

  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    if (hasExpirationDate())
    {
      return expirationDate.toString();
    }
    else
    {
      return "";
    }
  }

}
