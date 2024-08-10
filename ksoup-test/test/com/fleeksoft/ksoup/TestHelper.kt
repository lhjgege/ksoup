package com.fleeksoft.ksoup

import com.fleeksoft.ksoup.ported.io.BufferReader
import korlibs.io.file.VfsFile
import korlibs.io.file.fullName
import korlibs.io.file.std.uniVfs

object TestHelper {

    suspend fun readGzipResource(file: String): BufferReader {
        return readGzipFile(getResourceAbsolutePath(file).uniVfs)
    }

    fun getResourceAbsolutePath(resourceName: String): String {
        if (Platform.isWindows()) {
            return "../../../../testResources/$resourceName"
        } else if (Platform.isJS()) {
            return "https://raw.githubusercontent.com/fleeksoft/ksoup/release/ksoup-test/testResources/$resourceName"
        }
        return "${BuildConfig.PROJECT_ROOT}/ksoup-test/testResources/$resourceName"
    }

    suspend fun getFileAsString(file: VfsFile): String {
        val bytes: ByteArray = if (file.fullName.endsWith(".gz")) {
            readGzipFile(file).readAllBytes()
        } else {
            readFile(file).readAllBytes()
        }
        return bytes.decodeToString()
    }

    suspend fun resourceFilePathToStream(path: String): BufferReader {
        val file = this.getResourceAbsolutePath(path).uniVfs
        return pathToStream(file)
    }

    suspend fun pathToStream(file: VfsFile): BufferReader {
        return if (file.fullName.endsWith(".gz")) {
            readGzipFile(file)
        } else {
            readFile(file)
        }
    }
}
