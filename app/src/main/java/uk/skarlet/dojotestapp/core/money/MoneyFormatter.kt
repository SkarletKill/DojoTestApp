package uk.skarlet.dojotestapp.core.money

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class MoneyFormatter(private val locale: () -> Locale) {

    @Inject
    constructor() : this({ Locale.getDefault() })

    fun format(money: Money): String {
        val format = NumberFormat.getCurrencyInstance(locale()).apply {
            currency = money.currency
            minimumFractionDigits = money.currency.defaultFractionDigits
            maximumFractionDigits = money.currency.defaultFractionDigits
        }
        val major = BigDecimal.valueOf(money.minorUnits, money.currency.defaultFractionDigits)
        return format.format(major)
    }
}
