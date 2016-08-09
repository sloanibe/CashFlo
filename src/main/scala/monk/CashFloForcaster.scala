package monk

import java.util.{UUID, Date}


/**
 * Created by marksloan on 6/21/14.
 */

object CashFloForcaster {

  def addIncome(date:Date, name:String, amt:Double, org:String ) = synchronized {
    val cashFloDay = DB.getCashFloDay(date).next()
    cashFloDay.addIncome(new CashFloRec(name, amt, org, ""))
    cashFloDay.calcEndBal()
    DB.updateCashFloDay(cashFloDay)

    // now cascade the new updates for the rest of the year
    var previousCFD = cashFloDay
    val daysToUpdate = DB.getCashFloDaysAfter(date)
    while(daysToUpdate.hasNext) {
      val nextCfd = daysToUpdate.next()
      nextCfd.startBal = previousCFD.endBal
      nextCfd.calcEndBal()
      DB.updateCashFloDay(nextCfd)
      previousCFD = nextCfd
    }
  }

  def addExpense(date:Date, name:String, amt:Double, org:String ) = synchronized {
    val cashFloDay = DB.getCashFloDay(date).next()
    cashFloDay.addExpenditure(new CashFloRec(name, amt, org, ""))
    cashFloDay.calcEndBal()
    DB.updateCashFloDay(cashFloDay)

    // now cascade the new updates for the rest of the year
    var previousCFD = cashFloDay
    val daysToUpdate = DB.getCashFloDaysAfter(date)
    while(daysToUpdate.hasNext) {
      val nextCfd = daysToUpdate.next()
      nextCfd.startBal = previousCFD.endBal
      nextCfd.calcEndBal()
      DB.updateCashFloDay(nextCfd)
      previousCFD = nextCfd
    }
  }

  def updateIncome(id:String, date:Date, name:String, amt:Double, org:String) {
    val cashFloDay = DB.getCashFloDay(date).next()
    val incomes = cashFloDay.getIncomes()
    var recToUpdate:CashFloRec = null
    incomes.foreach((rec) => {
      if(rec.id.compareTo(id) == 0) recToUpdate=rec
    })
    recToUpdate.amt = amt
    recToUpdate.org = org
    recToUpdate.name = name
    cashFloDay.calcEndBal()

    var previousCFD = cashFloDay
    val daysToUpdate = DB.getCashFloDaysAfter(date)
    while(daysToUpdate.hasNext) {
      val nextCfd = daysToUpdate.next()
      nextCfd.startBal = previousCFD.endBal
      nextCfd.calcEndBal()
      DB.updateCashFloDay(nextCfd)
      previousCFD = nextCfd
    }
  }

  def updateExpense(id:String, date:Date,  name:String, amt:Double, org:String) {
    val cashFloDay = DB.getCashFloDay(date).next()
    val expenditures = cashFloDay.expenditures
    var recToUpdate:CashFloRec = null
    expenditures.foreach((rec) => {
      if(rec.id.compareTo(id) == 0) recToUpdate=rec
    })
    recToUpdate.amt = amt
    recToUpdate.org = org
    recToUpdate.name = name
    cashFloDay.calcEndBal()

    var previousCFD = cashFloDay
    val daysToUpdate = DB.getCashFloDaysAfter(date)
    while(daysToUpdate.hasNext) {
      val nextCfd = daysToUpdate.next()
      nextCfd.startBal = previousCFD.endBal
      nextCfd.calcEndBal()
      DB.updateCashFloDay(nextCfd)
      previousCFD = nextCfd
    }
  }

  def moveExpense(id:String, oldDate:Date, newDate:Date, amt:Double, org:String): Unit = {

  }

  def deleteExpense(date:Date, id:String) = synchronized {
    val cashFloDay = DB.getCashFloDay(date).next()
    val expenditures = cashFloDay.getExpenditures()
    var recToDelete:CashFloRec = null
    expenditures.foreach((rec) => {
      if(rec.id.compareTo(id) == 0) recToDelete=rec
    })
    if(recToDelete != null) {
      println("deleting expense")
      expenditures -= recToDelete
    }
    cashFloDay.calcEndBal()
    DB.updateCashFloDay(cashFloDay)

    // now cascade the new updates for the rest of the year
    var previousCFD = cashFloDay
    val daysToUpdate = DB.getCashFloDaysAfter(date)
    while(daysToUpdate.hasNext) {
      val nextCfd = daysToUpdate.next()
      nextCfd.startBal = previousCFD.endBal
      nextCfd.calcEndBal()
      DB.updateCashFloDay(nextCfd)
      previousCFD = nextCfd
    }
  }

  def deleteIncome(date:Date, id:String) = synchronized {
    val cashFloDay = DB.getCashFloDay(date).next()
    val incomes = cashFloDay.getIncomes()
    var recToDelete:CashFloRec = null
    incomes.foreach((rec) => if(rec.id.equals(id)) recToDelete=rec)
    if(recToDelete != null) incomes -= recToDelete
    cashFloDay.calcEndBal()
    DB.updateCashFloDay(cashFloDay)

    // now cascade the new updates for the rest of the year
    var previousCFD = cashFloDay
    val daysToUpdate = DB.getCashFloDaysAfter(date)
    while(daysToUpdate.hasNext) {
      val nextCfd = daysToUpdate.next()
      nextCfd.startBal = previousCFD.endBal
      nextCfd.calcEndBal()
      DB.updateCashFloDay(nextCfd)
      previousCFD = nextCfd
    }
  }

}
