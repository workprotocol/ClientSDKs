import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.DecimalFormat
import kotlin.math.roundToInt

class  Util {
    private val DEFAULT_PARAMS_ENCODING = "UTF-8"

    protected fun getParamsEncoding(): String? {
        return DEFAULT_PARAMS_ENCODING
    }

    fun encodeParameters(
        params: MutableMap<String, String>
    ): String? {
        val paramsEncoding=getParamsEncoding()
        val encodedParams = StringBuilder()
        return try {
            for ((key, value) in params) {
                require(!(key == null || value == null)) {
                    String.format(
                        "Request#getParams() or Request#getPostParams() returned a map "
                                + "containing a null key or value: (%s, %s). All keys "
                                + "and values must be non-null.",
                        key, value
                    )
                }
                encodedParams.append(URLEncoder.encode(key, paramsEncoding))
                encodedParams.append('=')
                encodedParams.append(URLEncoder.encode(value, paramsEncoding))
                encodedParams.append('&')
            }
            encodedParams.toString()
        } catch (uee: UnsupportedEncodingException) {
            throw RuntimeException("Encoding not supported: $paramsEncoding", uee)
        }
    }

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