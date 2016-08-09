package monk

import java.time.{Instant, LocalDate}
import java.util.Date

/**
 * Created by mark on 9/22/2015.
 */
class Util {

}

object util {
  def dateToLocalDate(date:Date):LocalDate = {
    val instant:Instant = date.toInstant
    //new LocalDate(instant)
    null
  }

  def localDateToDate(localDate:LocalDate) = {
    new Date(localDate.toEpochDay())
  }
}