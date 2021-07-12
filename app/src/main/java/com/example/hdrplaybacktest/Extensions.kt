package com.example.hdrplaybacktest

import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.*
import android.util.TypedValue
import androidx.core.text.toSpannable
import java.lang.StringBuilder
import kotlin.math.roundToInt


class StringBuilderAppendLineExt {
    private val sb = StringBuilder()

    operator fun String.unaryPlus() {
        sb.appendLine(this)
    }

    override fun toString(): String {
        return sb.toString()
    }
}

fun buildString(operations: StringBuilderAppendLineExt.() -> Unit): String {
    val sbe = StringBuilderAppendLineExt()
    return sbe.apply(operations).toString()
}

class SpannableStringBuilderExt(private val style: Style) {

    data class Style(
        val sectionTitleStyle: Int,
        val sectionTitleFontSize: Int,
        val sectionTitleColor: Int,
        val sectionLineSpacing: Int = 1,
        val sectionIndentation: Int = 2,
        val titleTextSpacing: Int = 2,
        val titleStyle: Int,
        val titleFontSize: Int,
        val titleColor: Int,
        val textStyle: Int,
        val textFontSize: Int,
        val textColor: Int
    )

    companion object {
        val STYLE_NORMAL = Style(
            sectionTitleFontSize = 18.sp,
            sectionTitleStyle = Typeface.BOLD,
            sectionTitleColor = Color.BLACK,
            titleStyle = Typeface.BOLD,
            titleFontSize = 15.sp,
            titleColor = Color.BLACK,
            textStyle = Typeface.NORMAL,
            textFontSize = 13.sp,
            textColor = Color.DKGRAY
        )

        val STYLE_NO_SECTION_TITLE = Style(
            sectionTitleFontSize = 0,
            sectionTitleStyle = Typeface.NORMAL,
            sectionTitleColor = Color.BLACK,
            sectionLineSpacing = 1,
            sectionIndentation = 0,
            titleStyle = Typeface.BOLD,
            titleFontSize = 15.sp,
            titleColor = Color.BLACK,
            textStyle = Typeface.NORMAL,
            textFontSize = 13.sp,
            textColor = Color.DKGRAY
        )
    }

    private val sb = SpannableStringBuilder()

    fun section(t: String, action: SpannableStringBuilder.() -> Unit): SpannableStringBuilder {
        return sb.append(SpannableString(t.addLine(style.sectionLineSpacing)).apply {
            setSpan(
                StyleSpan(style.sectionTitleStyle),
                0, t.length + style.sectionLineSpacing,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(style.sectionTitleColor),
                0, t.length + style.sectionLineSpacing,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            setSpan(
                AbsoluteSizeSpan(style.sectionTitleFontSize),
                0, t.length + style.sectionLineSpacing,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
        })
            .apply(action)

    }

    fun SpannableStringBuilder.title(t: String): SpannableStringBuilder {
        return this.append(
            SpannableString("".addSpace(style.sectionIndentation) + t.addSpace(style.titleTextSpacing)).apply {
                setSpan(
                    StyleSpan(style.titleStyle),
                    0, t.length + style.sectionIndentation + style.titleTextSpacing,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(style.titleColor),
                    0, t.length + style.sectionIndentation + style.titleTextSpacing,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(
                    AbsoluteSizeSpan(style.titleFontSize),
                    0, t.length + style.sectionIndentation + style.titleTextSpacing,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
        )
    }

    infix fun SpannableStringBuilder.text(t: String) {
        this.appendLine(
            SpannableString(t).apply {
                setSpan(
                    StyleSpan(style.textStyle),
                    0, t.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(
                    ForegroundColorSpan(style.textColor),
                    0, t.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(
                    AbsoluteSizeSpan(style.textFontSize),
                    0, t.length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
        )
    }

    fun toSpannable(): Spannable = sb.toSpannable()
}

fun buildSpannable(
    style: SpannableStringBuilderExt.Style,
    operations: SpannableStringBuilderExt.() -> Unit
): Spannable {
    val sbe = SpannableStringBuilderExt(style)
    return sbe.apply(operations).toSpannable()
}

fun String.addSpace(s: Int): String {
    var res = this
    repeat(s) {
        res += " "
    }
    return res
}

fun String.addLine(l: Int): String {
    var res = this
    repeat(l) {
        res += "\n"
    }
    return res
}


inline val <reified T : Number> T.sp
    get() = this.spFloat.roundToInt()

inline val <reified T : Number> T.spFloat
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
const val K = 1024
const val M = K * K
const val G = M * K
inline val Long.asFileSizeString: String
    get() = when {
        this >= G -> "${(this.toDouble() / G).round(2)} GB"
        this in M until G -> "${(this.toDouble() / M).round(2)} MB"
        this in K until M -> "${(this.toDouble() / K).round(2)} KB"
        this in 0 until K -> "$this B"
        else -> "Error"
    }

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (this * multiplier).roundToInt() / multiplier
}

fun a() {
    buildString {
        +"text"
        +"hello world"
    }

    buildSpannable(SpannableStringBuilderExt.STYLE_NORMAL) {
        section("title") {
            title("Hello") text "world"
            title("Hello") text "Android"
        }
    }
}

