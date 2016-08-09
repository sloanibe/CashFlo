package monk

import java.io.Serializable
import java.util.UUID
import scala.beans.BeanProperty


/**
 * Created by marksloan on 6/20/14.
 */

class CashFloRec(@BeanProperty var name:String, @BeanProperty var amt:Double,  @BeanProperty var org:String, @BeanProperty var cat:String) extends Serializable {
  @BeanProperty var id:String = UUID.randomUUID().toString()
}

object CashFloRec {
  def cashFloRecFromEventTitle(title:String):CashFloRec = {
    val strings = title.split(":")
    val name = strings(0)
    val amt = strings(1)
    val org = strings(2)
    new CashFloRec(name, amt.toDouble, org, "")
  }
}


