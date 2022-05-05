import com.atlassian.jira.issue.*
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.worklog.Worklog
import java.util.*
import java.text.SimpleDateFormat

def worklogManager = ComponentAccessor.getWorklogManager()

//get worklogs for issue
def logsForIssue = worklogManager.getByIssue(issue)

//create a table of worklogs via html
String fin = ''

for (Worklog worklog : logsForIssue) {
  String strTime = getTimeString(worklog.getTimeSpent())
  String strDate = formattedDate(worklog.getCreated())
  fin += strDate + ': ' + worklog.getAuthorObject().getDisplayName() + ' - ' + strTime + ' | '
}

return fin

public String getTimeString(long s) {
  def strOut = ""
  
  if ((s/28800).intValue() > 0) {
    int d = (s / 28800).intValue()
    strOut += d.toString() + "d "
  }
  
  s %= 28800
  
  if ((s/3600).intValue() > 0) {
    int h = (s / 3600).intValue()
    strOut += h.toString() + "h "
  }
  
  s %= 3600
  
  if ((s/60).intValue() > 0) {
    int m = (s / 60).intValue()
    strOut += m.toString() + "m "
  }
  
  s %= 60
  
  if (s.intValue() > 0) {
    strOut += (s.intValue()).toString() + "s"
  }
  
  return strOut
}

public String formattedDate(Date date) {  
  SimpleDateFormat formatter = new SimpleDateFormat('dd/MMMM/yyyy')
  String strOut = formatter.format(date)
  return strOut
}