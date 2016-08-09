package monk

import java.io.File

import com.db4o.{ObjectContainer, Db4oEmbedded}
import com.db4o.config.EmbeddedConfiguration

import com.db4o.cs.Db4oClientServer
import com.db4o.{ObjectSet, ObjectContainer, Db4oEmbedded}
import com.db4o.config.EmbeddedConfiguration
import com.db4o.query.Predicate
import java.util.{GregorianCalendar, Calendar, Date}
import org.apache.commons.lang3.time.DateUtils


/**
 * Created by marksloan on 6/20/14.
 */
class DB {}

object DB {
  //static init
  var config: EmbeddedConfiguration = Db4oEmbedded.newConfiguration();
  config.common().objectClass(classOf[CashFloDay]).cascadeOnUpdate(true)
  config.common().objectClass(classOf[CashFloDay]).cascadeOnDelete(true)

  var db: ObjectContainer = null

  def updateCashFloDay(cashFloDay: CashFloDay) {
    db.store(cashFloDay)
    db.commit()
  }

  def datesEqual(date1: Date, date2: Date): Boolean = {
    if (DateUtils.truncatedCompareTo(date1, date2, Calendar.DAY_OF_MONTH) == 0) true else false
  }

  def date1GreaterDate2(date1: Date, date2: Date): Boolean = {
    if (DateUtils.truncatedCompareTo(date1, date2, Calendar.DAY_OF_MONTH) > 0) true else false
  }

  def getAllCashFloDays(): ObjectSet[CashFloDay] = {
    val results: ObjectSet[CashFloDay] = db.query(new Predicate[CashFloDay]() {
      override def `match`(p1: CashFloDay): Boolean = {
        p1 != null
      }
    })
    results
  }

  def deleteAllCashFloDays() {
    val days = getAllCashFloDays()
    while (days.hasNext) {
      db.delete(days.next())
    }
    db.commit()
  }

  def getCashFloDaysAfter(date: Date): ObjectSet[CashFloDay] = {
    val results: ObjectSet[CashFloDay] = db.query(new Predicate[CashFloDay]() {
      override def `match`(p1: CashFloDay): Boolean = {
        date1GreaterDate2(p1.date, date)
      }
    })
    results
  }

  def getCashFloDay(date: Date): ObjectSet[CashFloDay] = {
    val results: ObjectSet[CashFloDay] = db.query(new Predicate[CashFloDay]() {
      override def `match`(p1: CashFloDay): Boolean = {
        datesEqual(p1.date, date)
      }
    })
    results
  }

  def initCashFloYear(year: Int) {
    println("Initing cashflo year!!!!")
    val startCal = new GregorianCalendar(year, 0, 1)
    for (day <- 1 to 365) {
      val cfd = new CashFloDay(DateUtils.truncate(startCal.getTime, Calendar.DAY_OF_MONTH), 0, 0)
      DB.updateCashFloDay(cfd)
      startCal.add(Calendar.DAY_OF_MONTH, 1)
    }
    db.commit()
  }

  def close() {
    if (db != null) {
      while (!db.close()) db.close()
    }
  }

  def open(filePath: String) {
    close()
    val file: File = new File(filePath)
    val fileExists = file.exists()

    config = Db4oEmbedded.newConfiguration();
    config.common().objectClass(classOf[CashFloDay]).cascadeOnUpdate(true)
    config.common().objectClass(classOf[CashFloDay]).cascadeOnDelete(true)
    db = Db4oEmbedded.openFile(config, filePath);
    //var server = Db4oClientServer.openServer(filePath, 0);
    //var db = server.openClient();
    if (!fileExists) {
      println("initing year")
      initCashFloYear(2016)
    }
  }

}