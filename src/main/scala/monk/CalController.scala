package monk

import java.io.{DataInputStream, FileInputStream, BufferedReader, InputStreamReader}
import java.text.{DecimalFormat, SimpleDateFormat}
import java.time.LocalDate
import java.util.{UUID, Calendar, Date}
import javax.annotation.PostConstruct

import javax.faces.bean.{ApplicationScoped, ManagedBean, ManagedProperty, SessionScoped}
import javax.faces.event.ActionEvent


import org.primefaces.context.RequestContext
import org.primefaces.event.{ScheduleEntryMoveEvent, UnselectEvent, SelectEvent}
import org.primefaces.model.{DefaultScheduleEvent, DefaultScheduleModel, ScheduleModel}

import scala.beans.BeanProperty
import scala.collection.mutable.ArrayBuffer
import scala.io.Source


/**
 * Created by marksloan on 6/19/14.
 */

@ManagedBean(name = "calController")
@ApplicationScoped
class CalController extends Serializable {
  @BeanProperty
  var eventModel: ScheduleModel = new DefaultScheduleModel()
  @BeanProperty
  var amt = 0.0
  @BeanProperty
  var amtStr = ""
  @BeanProperty
  var name = ""
  @BeanProperty
  var org = ""
  @BeanProperty
  var id: String = null
  @BeanProperty
  var selectedEventType = ""
  @BeanProperty
  var dialogSelectedCashFloRec: CashFloRec = null
  @BeanProperty
  var recs1: java.util.ArrayList[CashFloRec] = new java.util.ArrayList[CashFloRec]()
  @BeanProperty
  var selectedCashFloRecs: ArrayBuffer[CashFloRec] = new ArrayBuffer[CashFloRec]()
  @BeanProperty
  var dialogHdr: String = null
  @BeanProperty
  var deleteBtnDisabled: String = "true"
  @BeanProperty
  var eventDisplay: String = "summary"
  @BeanProperty
  var selectedCashFloDate: Date = new Date()


  @PostConstruct
  def init {
    DB.open("C:\\cashflo\\cashflodata")
    addRecurringMonthlyExpenses()

    //some comments
    updateEventModel()
  }

  def doMothlyAction (actionEvent:ActionEvent) {
    println("in monthly action")

  }

  def updateRecurrenceModel() {\n    println(\"in recurrence")
  }

  def parseLine(line:String) : CashFloRec = {
    val data = line.split(" ")
    val name = data(0).replaceFirst("name:", "")
    val amt = data(1).replaceFirst("amt:", "")
    val org = data(2).replaceFirst("org:", "")
    val cat = data(3).replaceFirst("cat:", "")
    val day = data(4).replaceFirst("amt:", "")
    new CashFloRec(name, amt.toDouble, org, cat)

  }

  def addRecurringMonthlyExpenses(): Unit = {
    val filename = "C:\\cashflo\\monthly-expenses.txt"
    for (line <- Source.fromFile(filename).getLines()) {
      val cashFloRec = parseLine(line)

    }

    val c:Calendar = Calendar.getInstance();   // this takes current date
    c.set(Calendar.DAY_OF_MONTH, 1);
    println("date of begining of Month " + c.getTime());

  }

  def round2(num:Double) :String =
  {
    val twoDForm:DecimalFormat = new DecimalFormat("##.00");
    twoDForm.format(num)
  }


  def updateEventModel() {

    eventModel.clear()

    val cashFloDays = DB.getAllCashFloDays()
    while (cashFloDays.hasNext) {
      val cfd = cashFloDays.next()


      if (eventDisplay == "summary") {
        val balEvent: DefaultScheduleEvent = new DefaultScheduleEvent("Bal:" + round2(cfd.endBal), cfd.date, cfd.date)
        balEvent.setAllDay(true)
        balEvent.setEditable(false)
        eventModel.addEvent(balEvent)
        val incomeEvent: DefaultScheduleEvent = new DefaultScheduleEvent("Inc:" + round2(cfd.getTotalIncome()), cfd.date, cfd.date)
        incomeEvent.setAllDay(true)
        eventModel.addEvent(incomeEvent)
        val expenditureEvent: DefaultScheduleEvent = new DefaultScheduleEvent("Exp:" + round2(cfd.getTotalExpenditures()), cfd.date, cfd.date)
        expenditureEvent.setAllDay(true)
        eventModel.addEvent(expenditureEvent)
      } else if (eventDisplay == "exp") {
        for (cashFloRec <- cfd.getExpenditures()) {
          val expEvent: DefaultScheduleEvent = new DefaultScheduleEvent(cashFloRec.getName + ":" + round2(cashFloRec.getAmt) + ":" + cashFloRec.getOrg, cfd.date, cfd.date)
          expEvent.setAllDay(true)
          test
          eventModel.addEvent(expEvent)

        }
      } else if (eventDisplay == "inc") {
        for (cashFloRec <- cfd.getIncomes()) {
          val incEvent: DefaultScheduleEvent = new DefaultScheduleEvent(cashFloRec.getName + ":" + round2(cashFloRec.getAmt), cfd.date, cfd.date)
          incEvent.setAllDay(true)
          eventModel.addEvent(incEvent)
        }
      }
    }
  }

  def getInitialDate: Date = {
    val calendar: Calendar = Calendar.getInstance();
    calendar.set(calendar.get(Calendar.YEAR), Calendar.FEBRUARY, calendar.get(Calendar.DATE), 0, 0, 0);
    return calendar.getTime();
  }

  def addEvent() {
    println("in addevent")
    if (selectedEventType == "Income" || eventDisplay == "inc") {
      println(selectedEventType)
      if (id == null) {
        CashFloForcaster.addIncome(selectedCashFloDate, name, amt, org)
      } else {
        CashFloForcaster.updateIncome(id, selectedCashFloDate, name, amt, org)
      }
    } else {
      if (id == null) {
        CashFloForcaster.addExpense(selectedCashFloDate, name, amt, org)
      } else {
        CashFloForcaster.updateExpense(id, selectedCashFloDate, name, amt, org)
      }
    }
    updateEventModel()
  }

  def deleteEvent() {
    if (selectedEventType.equals("Income")) {
      CashFloForcaster.deleteIncome(selectedCashFloDate, id)
    } else {
      CashFloForcaster.deleteExpense(selectedCashFloDate, id)
    }
    updateEventModel()
  }

  //Called from selecting on the calendar
  def onEventSelect(selectEvent: SelectEvent) {
    val requestContext: RequestContext = RequestContext.getCurrentInstance
    deleteBtnDisabled = "true"
    id = null
    amt = 0.0
    org = ""
    name = ""

    val selEvent: DefaultScheduleEvent = selectEvent.getObject().asInstanceOf[DefaultScheduleEvent]
    selectedCashFloDate = selEvent.getStartDate
    val cfd: CashFloDay = DB.getCashFloDay(selEvent.getStartDate).next()
    if (selEvent.getTitle.contains("Bal") ) {
      requestContext.addCallbackParam("noDialogDisplay", true);
    }
    if (selEvent.getTitle.contains("Exp")) {
      selectedEventType = "Expense"
      recs1.clear()
      selectedCashFloRecs = cfd.getExpenditures()
      selectedCashFloRecs.foreach((rec) => recs1.add(rec));
    } else {
      recs1.clear()
      selectedEventType = "Income"
      selectedCashFloRecs = cfd.getIncomes()
      selectedCashFloRecs.foreach((rec) => recs1.add(rec));
    }
    val sdf: SimpleDateFormat = new SimpleDateFormat("M/dd/yyyy");
    val hdrDate: String = sdf.format(selectedCashFloDate);
    dialogHdr = selectedEventType + "s" + " " + hdrDate
  }

  def onDateSelect(e:SelectEvent) {
    println("in onDateSelet")
    var date = e.getObject().asInstanceOf[Date]
    val cal:Calendar = Calendar.getInstance();
    cal.setTime(date);
    cal.add(Calendar.DATE, +1);
    date= cal.getTime();

    selectedCashFloDate = date
    val cfd: CashFloDay = DB.getCashFloDay(date).next()
    if (eventDisplay == "exp") {
      selectedEventType = "Expense"
      recs1.clear()
      selectedCashFloRecs = cfd.getExpenditures()
      selectedCashFloRecs.foreach((rec) => recs1.add(rec));
    } else {
      recs1.clear()
      selectedEventType = "Income"
      selectedCashFloRecs = cfd.getIncomes()
      selectedCashFloRecs.foreach((rec) => recs1.add(rec));
    }
    val sdf: SimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
    val hdrDate: String = sdf.format(selectedCashFloDate);
    dialogHdr = selectedEventType + "s" + " " + hdrDate
  }

  def getDialogBtnName(): String = {
    if (id != null) "Update" else "New"
  }

  //Called from selecting in the dialog
  def onRowSelect(event: SelectEvent) {
    deleteBtnDisabled = "false"
    val cfd: CashFloRec = event.getObject.asInstanceOf[CashFloRec]
    name = cfd.getName
    amt = cfd.getAmt
    org = cfd.getOrg
    id = cfd.id
  }


  def close() {
    println("closing db")
    DB.close()
  }

}
