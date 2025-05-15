package v2.lib

trait BasicReport:
	def printInformation(pref: String): Unit

trait Report extends BasicReport:
	def depsList: List[String]
