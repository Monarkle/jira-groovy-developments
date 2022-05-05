import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.history.ChangeItemBean
import java.lang.Math

//Define the variables to be used
String strResult = ""
String strTotalResult = ""
String strStatusTotals = ""
Long totalSeconds = 0
List<String> statusName = ["Development - WIP", "Comebacks", "Check-in"]
boolean wasWIP = false
def changeHistoryManager = ComponentAccessor.getChangeHistoryManager()
def changeItems = changeHistoryManager.getChangeItemsForField(issue, "status")

for(item in changeItems){
	if(statusName.contains(item.getToString())){
    	wasWIP = true
    }
}

//IF the issue was in a WIP status then process as usual
if(wasWIP == true){
  //Get time difference for each status and concatinate onto strResult
  for(sName in statusName){
    boolean containsStatus = false
  
    //Check if the issue has been in any of the status or not and if not leave them as blank
    changeItems.reverse().each { ChangeItemBean item ->
      if (item.fromString == sName) {
        containsStatus = true
      }
      if (item.toString == sName) {
        containsStatus = true
      }
    }
  
    //If the issue has been in the Status, process as usual, else elave their place as blank
    if(containsStatus == true){
      Long totalS = getTotalTime(sName)
      String timeString = TimeToString(totalS, sName)
      totalSeconds += totalS
      strStatusTotals += timeString
    } else {
      strResult += ""
    }
    String totalTimeString = TimeToString(totalSeconds, "Total time in WIP")
    strResult = totalTimeString + strStatusTotals
  }
} else {
  	strResult = "This task has not been in WIP yet."
}

//output the completed string
return strResult

//Convert seconds into a string for days, hours, minutes and seconds
private String TimeToString(Long lSeconds, String strSName) {
  def strTimeStr = strSName + ": "

  if((lSeconds/86400).intValue() > 0){
    Integer iD = (lSeconds / 86400).intValue()
    strTimeStr += iD.toString() + "d "
  }
  lSeconds %= 86400

  if((lSeconds/3600).intValue() > 0) {
    Integer iH = (lSeconds / 3600).intValue()
    strTimeStr += iH.toString() + "h "
  }
  lSeconds %= 3600

  if((lSeconds/60).intValue() > 0) {
    Integer iM = (lSeconds / 60).intValue()
    strTimeStr += iM.toString() + "m "
  }
  lSeconds %= 60

  Integer iS = lSeconds.intValue()
  strTimeStr += iS.toString() + "s|"
  return strTimeStr
}

//Calculate the total seconds in the status
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