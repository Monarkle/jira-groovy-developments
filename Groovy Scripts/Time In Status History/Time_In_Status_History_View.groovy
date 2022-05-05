import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.history.ChangeItemBean
import java.lang.Math

//Define the variables to be used
String strResult = ""
String strOut = ""
List<Long> rt = [0L]
List<String> statusName = []
def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def changeItems = changeHistoryManager.getChangeItemsForField(issue, "status")

//Validate that there is only one of each change item name
for(item in changeItems){
  if(statusName.contains(item.getToString())){
    //Do Nothing
  } else {
    statusName << item.getToString()
  }
}

//Get time difference for each status and concatinate onto strResult
for(sName in statusName){
  Long totalS = getTotalTime(sName)
	String totalString = TimeToString(totalS, sName)
  strOut += totalString
}

strResult = """<ul>""" + strOut + """</ul>"""
return strResult

private String TimeToString(Long lSeconds, String strSName) {
  def strTimeStr = """<li>""" + strSName + ": "

  if((lSeconds/86400).intValue() > 0){
    Integer iD = (lSeconds / 86400).intValue()
    strTimeStr += iD.toString() + "d, "
  }
  lSeconds %= 86400

  if((lSeconds/3600).intValue() > 0) {
    Integer iH = (lSeconds / 3600).intValue()
    strTimeStr += iH.toString() + "h, "
  }
  lSeconds %= 3600

  if((lSeconds/60).intValue() > 0) {
    Integer iM = (lSeconds / 60).intValue()
    strTimeStr += iM.toString() + "m, "
  }
  lSeconds %= 60

  Integer iS = lSeconds.intValue()
  strTimeStr += iS.toString() + "s" + """</li>"""
  return strTimeStr
}

private Long getTotalTime(String strName) {
	List<Long> rt = [0L]
  def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
  def changeItems = changeHistoryManager.getChangeItemsForField(issue, "status")
	changeItems.reverse().each { ChangeItemBean item ->
    def timeDiff = System.currentTimeMillis() - item.created.getTime()

    if (item.fromString == strName) {
      rt << -timeDiff
    }
    if (item.toString == strName) {
      rt << timeDiff
    }
  }
          
  def newRT = rt as long []
  rt.clear()
  def s = newRT.sum()/ 1000 as long
  return s
}

