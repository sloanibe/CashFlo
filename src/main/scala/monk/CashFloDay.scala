package monk

import scala.collection.mutable.ArrayBuffer
import java.util.Date

/**
 * Created by marksloan on 6/19/14.
 */
class CashFloDay(var date: Date,
                 var startBal: Double,
                 var endBal: Double ) {

  var expenditures = new ArrayBuffer[CashFloRec]
  var incomes = new ArrayBuffer[CashFloRec]

  def addExpenditure(exp:CashFloRec) {
    expenditures += exp
  }

  def removeExpenditure(expToRemove:CashFloRec): Unit = {
    for (exp <- expenditures) {
      if(exp.getAmt == expToRemove.getAmt && exp.getName == expToRemove.getName && exp.getOrg == expToRemove.getOrg) {
        expenditures -= exp
        return
      }
    }
  }

  def addIncome(income:CashFloRec) {
    incomes += income
  }

  def calcEndBal():Double = {
    var incomeTotal:Double = 0.0
    var expendituresTotal = 0.0

    incomes.foreach(incomeTotal +=_.amt)
    expenditures.foreach(expendituresTotal +=_.amt)
    endBal = startBal + (incomeTotal - expendituresTotal)
    endBal
  }

  def getTotalIncome():Double = {
    var total = 0.0
    incomes.foreach((incomeRec) => total += incomeRec.amt)
    total
  }

  def getTotalExpenditures():Double = {
    var total = 0.0
    expenditures.foreach((expenditureRec) => total += expenditureRec.amt)
    total
  }

  def getIncomes():ArrayBuffer[CashFloRec] = {
    return incomes
  }

  def getExpenditures():ArrayBuffer[CashFloRec] = {
    return expenditures
  }

}
