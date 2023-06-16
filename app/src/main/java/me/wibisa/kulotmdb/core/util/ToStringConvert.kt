package me.wibisa.kulotmdb.core.util

import me.wibisa.kulotmdb.core.data.remote.response.Genre
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

fun convertMinutesToString(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60

    return "$hours h $remainingMinutes m"
}

fun convertToCurrencyString(number: Int): String {
    val formattedNumber = number.toBigDecimal().setScale(0).toString()
    val currencyString = "$$formattedNumber"

    return currencyString.replace(Regex("(\\d)(?=(\\d{3})+\$)"), "$1.")
}

fun convertDateFormat(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    return outputFormat.format(date)
}

fun convertDecimal(number: Double): String {
    val decimalFormat = DecimalFormat("#.#")
    return decimalFormat.format(number)
}

fun convertGenresToString(genres: List<Genre>): String {
    return genres.joinToString(", ") { it.name }
}