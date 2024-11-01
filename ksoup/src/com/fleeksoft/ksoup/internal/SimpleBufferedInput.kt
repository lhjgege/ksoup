package com.fleeksoft.ksoup.internal

import com.fleeksoft.io.Constants
import com.fleeksoft.io.FilterInputStream
import com.fleeksoft.io.InputStream
import com.fleeksoft.io.exception.IOException
import kotlin.math.min

class SimpleBufferedInput(private val inputStream: InputStream) : FilterInputStream(inputStream) {
    private var bufPos = 0
    private var bufLength = 0
    private var bufMark = -1
    private var inReadFully = false
    private var byteBuf: ByteArray? = null

    override fun read(): Int {
        if (bufPos >= bufLength) {
            fill()
            if (bufPos >= bufLength) return -1
        }
        return byteBuf!![bufPos++].toInt() and 0xff
    }

    override fun read(dest: ByteArray, offset: Int, desiredLen: Int): Int {
        if (offset < 0 || desiredLen < 0 || desiredLen > dest.size - offset) {
            throw IndexOutOfBoundsException()
        } else if (desiredLen == 0) {
            return 0
        }
        var bufAvail = bufLength - bufPos
        if (bufAvail <= 0) {
            if (!inReadFully && bufMark < 0) {
                val read = inputStream.read(dest, offset, desiredLen)
                closeIfDone(read)
                return read
            }
            fill()
            bufAvail = bufLength - bufPos
        }
        val read = min(bufAvail, desiredLen)
        if (read <= 0) return -1
        byteBuf!!.copyInto(dest, destinationOffset = offset, startIndex = bufPos, endIndex = bufPos + read)
        bufPos += read
        return read
    }

    private fun fill() {
        if (inReadFully) return
        if (byteBuf == null) {
            byteBuf = BufferPool.borrow()
        }
        if (bufMark < 0) {
            bufPos = 0
        } else if (bufPos >= Constants.DEFAULT_BYTE_BUFFER_SIZE) {
            if (bufMark > 0) {
                val size = bufPos - bufMark
                byteBuf!!.copyInto(byteBuf!!, destinationOffset = 0, startIndex = bufMark, endIndex = bufMark + size)
                bufPos = size
                bufMark = 0
            } else {
                bufMark = -1
                bufPos = 0
            }
        }
        bufLength = bufPos
        val read = inputStream.read(byteBuf!!, bufPos, byteBuf!!.size - bufPos)
        if (read > 0) {
            bufLength = read + bufPos
            while (byteBuf!!.size - bufLength > 0) {
                if (inputStream.available() < 1) break
                val readSub = inputStream.read(byteBuf!!, bufLength, byteBuf!!.size - bufLength)
                if (readSub <= 0) break
                bufLength += readSub
            }
        }
        closeIfDone(read)
    }

    private fun closeIfDone(read: Int) {
        if (read == -1) {
            inReadFully = true
            super.close()
        }
    }

    fun getBuf(): ByteArray {
        return byteBuf!!
    }

    fun baseReadFully(): Boolean {
        return inReadFully
    }

    override fun available(): Int = if (byteBuf != null && bufLength - bufPos > 0) bufLength - bufPos else if (inReadFully) 0 else inputStream.available()

    override fun mark(readlimit: Int) {
        if (readlimit > Constants.DEFAULT_BYTE_BUFFER_SIZE) throw IllegalArgumentException("Read-ahead limit is greater than buffer size")
        bufMark = bufPos
    }

    override fun reset() {
        bufPos = bufMark
        if (bufMark < 0) throw IOException("Resetting to invalid mark")
    }

    override fun close() {
        super.close()
        if (byteBuf == null) return
        BufferPool.release(byteBuf!!)
        byteBuf = null
    }

    companion object {
        val BufferPool: SoftPool<ByteArray> = SoftPool<ByteArray> { ByteArray(Constants.DEFAULT_BYTE_BUFFER_SIZE) }
    }
}