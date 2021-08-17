import java.text.DecimalFormat
import kotlin.math.roundToInt

class  Util {

    fun roundTo2Decimals(number: Double): Double {
        return try {
            (number * 100.0).roundToInt() / 100.0
        } catch (e: Exception) {
            e.printStackTrace()
            number
        }
    }

    fun roundTo2DecimalStringValue(number: Double): String? {
        return try {
            val df = DecimalFormat("##,##,##,##,###.##")
            val roundedNumber = roundTo2Decimals(number)
            val fractionalPart = roundedNumber - roundedNumber.toInt()
            if (fractionalPart > 0) {
                df.format(roundedNumber)
            } else {
                df.format(roundedNumber.toInt())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            number.toString()
        }
    }

}