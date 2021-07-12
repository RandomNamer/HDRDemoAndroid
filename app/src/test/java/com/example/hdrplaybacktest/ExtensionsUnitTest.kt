package com.example.hdrplaybacktest

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExtensionsUnitTest {

    @Test
    fun testStringBuilderExt(){
        val s = buildString {
            +"hello"
            +"world"
        }
        assertEquals("hello\nworld\n", s)
    }

    @Test
    fun testSpannableStringBuilderExt(){
        val s = buildSpannable(SpannableStringBuilderExt.STYLE_TEST){
            title("Hello") text "world"
            title("Hello") text "Android"
        }
        assert(true)
    }
}