package com.fleeksoft.ksoup.issues

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.TestHelper
import com.fleeksoft.ksoup.runTest
import okio.Path.Companion.toPath
import kotlin.test.Test
import kotlin.test.assertEquals

class GithubIssuesTests {
    @Test
    fun testIssue20DuplicateElements() {
        //    https://github.com/fleeksoft/ksoup/issues/20
        runTest {
            Ksoup.parse(TestHelper.getFileAsString(TestHelper.getResourceAbsolutePath("htmltests/issue20.html.gz").toPath()))
//            Ksoup.parseGetRequest("https://www.dm530w.org/")
                .apply {
                    body().select("div[class=firs l]")
                        .firstOrNull()?.let { element ->
                            val titles = element.select("div[class=dtit]")
                            val contents = element.select("div[class=img]")
                            println("titles: ${titles.size}, contents: ${contents.size}")
                            assertEquals(6, titles.size)
                            assertEquals(6, contents.size)
                        }
                }
        }
    }
}
