package app.kreate.android.utils.innertube

import android.telephony.TelephonyManager
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.core.content.getSystemService
import app.kreate.android.Preferences
import it.fast4x.rimusic.appContext
import it.fast4x.rimusic.enums.Languages
import me.knighthat.innertube.request.Localization
import java.util.Locale


val CURRENT_LOCALE: Localization by derivedStateOf {
    Localization(HOST_LANGUAGE, GEO_LOCATION)
}

// hl
val HOST_LANGUAGE: String by derivedStateOf {
    when ( Preferences.APP_LANGUAGE.value ) {
        Languages.System ->
            try {
                enumValueOf<Languages>(Locale.getDefault().language).code
            } catch (_: IllegalArgumentException) {
                "en"
            }

        else -> Preferences.APP_LANGUAGE.value.code
    }
}

// gl
val GEO_LOCATION: String by derivedStateOf {
    var countryCode = Preferences.APP_REGION.value
    if( countryCode.isBlank() || countryCode !in Locale.getISOCountries() )
        countryCode = getSystemCountryCode()

    countryCode
}

fun getSystemCountryCode(): String {
    var countryCode = Locale.getDefault().country
    if( countryCode !in Locale.getISOCountries() )
        countryCode = appContext().getSystemService<TelephonyManager>()
                                  ?.networkCountryIso
                                  ?.uppercase()
                                  .orEmpty()

    return countryCode.ifBlank { "US" }
}