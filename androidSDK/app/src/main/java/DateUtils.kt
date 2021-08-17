import com.spritehealth.sdk.SpriteHealthClient
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.datatype.DatatypeConstants.*


class DateUtils {
    var calendar: Calendar = GregorianCalendar()
    var timeZone: TimeZone? =TimeZone.getTimeZone(SpriteHealthClient.member.timeZone)
     val INPUT_DATE_FORMAT = "MM/dd/yyyy"
     val INPUT_DATETIME_FORMAT = "MM/dd/yyyy hh:mm a"
     val OUTPUT_DATE_FORMAT = "MMM dd, yyyy"
     val OUTPUT_DATETIME_FORMAT = "MMM dd, yyyy hh:mm a"


    fun getDateParser(inputFormat: String?): SimpleDateFormat {
        var inputFormat = inputFormat
        inputFormat = inputFormat ?: INPUT_DATETIME_FORMAT
        val sdf = SimpleDateFormat(inputFormat)
        sdf.timeZone = timeZone
        return sdf
    }

    
    fun getDate(date: String?): Date {
        val sdf: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        sdf.timeZone = timeZone
        return sdf.parse(date)
    }

    
    fun getDateFromThreeCharMonth(date: String?): Date {
        val sdf: DateFormat = SimpleDateFormat("MMM dd, yyyy")
        sdf.timeZone = timeZone
        return sdf.parse(date)
    }

    fun getMMMddyyyyFromHyphenatedDateString(dateString: String, isWithTime: Boolean): String? {
        val dateStr = dateString.replace("-", "/")
        return getMMMddyyyyDateString(dateStr, isWithTime)
    }

    fun getTime(dateString: String?): String? {
        var dateString = dateString
        val dateFormat = "MM/dd/yyyy hh:mm a"
        try {
            val inFormatter: DateFormat = SimpleDateFormat(dateFormat)
            val outFormatter: DateFormat = SimpleDateFormat("hh:mm a")
            val dt: Date = inFormatter.parse(dateString)
            dateString = outFormatter.format(dt)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dateString
    }

    fun getMMMddyyyyDateString(dateString: String?, isWithTime: Boolean): String? {
        var dateString = dateString
        val dateFormat: String
        return if (dateString != null) {
            dateFormat = if (isWithTime) {
                "MM/dd/yyyy hh:mm a"
            } else {
                "MM/dd/yyyy"
            }
            try {
                val inFormatter: DateFormat = SimpleDateFormat(dateFormat)
                val outFormatter: DateFormat = SimpleDateFormat("MMM dd, yyyy")
                //sdf.setTimeZone(WPConfig.MEMBER_TIMEZONE);
                val dt: Date = inFormatter.parse(dateString)
                dateString = outFormatter.format(dt)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            dateString
        } else null
    }

    fun getHyphenatedDateString(date: Date?): String? {
        if (date != null) {
            val cal: Calendar = Calendar.getInstance()
            cal.time = date
            cal.timeZone = timeZone
            val sdf: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            sdf.timeZone = timeZone
            return sdf.format(cal.time)
        }
        return null
    }

    fun getFormattedDateString(date: Date?, outputFormat: String?): String? {
        var outputFormat = outputFormat
        if (date != null) {
            val cal: Calendar = Calendar.getInstance()
            cal.time = date
            cal.timeZone = timeZone
            outputFormat = outputFormat ?: OUTPUT_DATETIME_FORMAT
            val sdf: DateFormat = SimpleDateFormat(outputFormat)
            sdf.timeZone = timeZone
            return sdf.format(cal.time)
        }
        return null
    }


    fun getDateWithFullTimestamp(date: String?): Date {
        val sdf: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
        sdf.timeZone = timeZone
        return sdf.parse(date)
    }


    fun getDateSlashFormattedWithTimeStamp(date: String?): Date {
        val sdf: DateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a")
        sdf.timeZone = timeZone
        return sdf.parse(date)
    }


    fun getDateSlashFormattedWithTimeStampInUSFormat(date: String?): Date {
        val sdf: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
        sdf.timeZone = timeZone
        return sdf.parse(date)
    }

    val currentDate: Date
        get() {
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeZone = timeZone
            return calendar.time
        }


    val currentDateWithoutTimestamp: Date
        get() {
            calendar.timeZone = timeZone
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
            dateFormat.calendar = calendar
            dateFormat.timeZone = timeZone
            return dateFormat.parse(dateFormat.format(Date()))
        }

    val currentDateWithTimestamp: Date
        get() {
            calendar.timeZone = timeZone
            val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy hh:mm a")
            dateFormat.calendar = calendar
            dateFormat.timeZone = timeZone
            return dateFormat.parse(dateFormat.format(Date()))
        }

    fun nextDate(date: Date?, days: Int): Date {
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, days)
        return calendar.time
    }

    fun getStringDate(date: Date?): String? {
        if (date != null) {
            val cal: Calendar = Calendar.getInstance()
            cal.time = date
            cal.timeZone = timeZone
            val sdf: DateFormat = SimpleDateFormat("MM/dd/yyyy")
            sdf.timeZone = timeZone
            return sdf.format(cal.time)
        }
        return null
    }

    fun getStringDateWithTime(date: Date?): String? {
        if (date != null) {
            val cal: Calendar = Calendar.getInstance()
            cal.time = date
            cal.timeZone = timeZone
            val sdf: DateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm a")
            sdf.timeZone = timeZone
            return sdf.format(cal.time)
        }
        return null
    }

    fun getEndDate(startDate: Date?, duration: Int): Date? {
        var endDate: Date? = null
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        try {
            if (startDate != null) calendar.time = startDate
            calendar.add(Calendar.MONTH, duration)
            endDate = calendar.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return endDate
    }

    fun getNextDate(date: Date?, duration: Int): Date? {
        var endDate: Date? = null
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeZone = timeZone
        try {
            if (date != null) calendar.time = date
            calendar.add(Calendar.DATE, duration)
            endDate = calendar.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return endDate
    }

    /*
	 * This method return the next date based on increment by Days, Weeks,
	 * Months, Years
	 */
    fun getNextDate(startDate: Date?, duration: Int, unit: String?): Date? {
        var endDate: Date? = null
        try {
            if (startDate != null) {
                val calendar: Calendar = Calendar.getInstance()
                calendar.timeZone = timeZone
                calendar.time = startDate

                /* temp comment..to remove compile error...remove this comment and dont checkin
				switch (unit.toUpperCase()) {
				case "DAYS":
					calendar.add(Calendar.DATE, duration);
					break;
				case "WEEKS":
					calendar.add(Calendar.WEEK_OF_YEAR, duration);
					break;
				case "MONTHS":
					calendar.add(Calendar.MONTH, duration);
					break;
				case "YEARS":
					calendar.add(Calendar.YEAR, duration);
					break;
				default:// DAYS
					calendar.add(Calendar.DATE, duration);
				}
				 */endDate = calendar.time
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return endDate
    }

    fun getDate(date: String?, format: String?): Date {
        val sdf: DateFormat = SimpleDateFormat(format)
        sdf.timeZone = timeZone
        return sdf.parse(date)
    }

   
    fun getDate(date: String?, days: Int): Date {
        val cal: Calendar = Calendar.getInstance()
        cal.timeZone = timeZone
        cal.time = getDate(date)
        return cal.time
    }

    fun getUnixTimeStamp(date: Date?): Long? {
        return if (date != null) {
            val cal: Calendar = Calendar.getInstance()
            cal.time = date
            cal.timeZone = timeZone
            cal.timeInMillis / 1000L
        } else {
            null
        }
    }

    fun getFormatConverted(
        inputFormat: String?,
        outputFormat: String?,
        inputDateStr: String
    ): String {
        try {
            val sdf1 = SimpleDateFormat(inputFormat ?: "dd-MM-yyyy")
            val sdf2 = SimpleDateFormat(outputFormat ?: "MM/dd/yyyy")
            val triggerDateValue: Date = sdf1.parse(inputDateStr)
            return sdf2.format(triggerDateValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return inputDateStr
    }

    /*
	 * This method return the next date based on increment by Days, Weeks,
	 * Months, Years
	 */
    fun getNextDate(
        startDate: Date?,
        mins: Int?
    ): Date? {
        var endDate: Date? = null
        try {
            if (startDate != null) {
                val calendar = Calendar.getInstance()
                calendar.time = startDate
                calendar.add(Calendar.MINUTE, mins!!)
                endDate = calendar.time
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return endDate
    }

}