import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.common.util.CollectionUtils
import com.spritehealth.sdk.SpriteHealthClient
import com.spritehealth.sdk.model.Location
import com.spritehealth.sdk.model.User
import java.io.IOException
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

    fun getDefaultMemberLocation(): Location? {
        val member: User =SpriteHealthClient.member
        if (member != null && !CollectionUtils.isEmpty(member.locations)) {
            val homeLocation: Location = member.locations!![0]
            //if (homeLocation.geoPt != null) {
                return homeLocation
            //}
        }
        return null
    }

    fun getZipCodeFromLocation(mContext: Context?, latitude: Double, longitude: Double): Int? {
        var zipCode: Int =0
        val addr: Address? = getAddressFromLocation(mContext, latitude, longitude)
        val zipCodeStr =
            if (addr == null || addr.getPostalCode() == null) "" else addr.getPostalCode()
        if (!zipCodeStr.isEmpty()) {
            zipCode = zipCodeStr.toInt()
        }
        return zipCode
    }

    fun getAddressFromLocation(mContext: Context?, latitude: Double?, longitude: Double?): Address? {

        try {
            if(latitude!=null && longitude!=null){
            val geocoder = Geocoder(mContext)
            val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 20)
            if (addresses.size > 0) {
                for (address in addresses) {
                    val postalCode: String = address.getPostalCode()
                    if (postalCode != null && !postalCode.isEmpty()) {
                        return address
                    }
                }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


}