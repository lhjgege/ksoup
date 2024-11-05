package com.fleeksoft.ksoup.ported

inline fun <T : Comparable<T>> Array<out T?>.binarySearch(element: T): Int {
    var low = 0
    var high = this.size - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = get(mid)
        val cmp = compareValues(midVal, element)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return mid // key found
    }
    return -(low + 1)  // key not found
}

inline fun IntArray.binarySearch(key: Int): Int {
    var low = 0
    var high = this.size - 1

    while (low <= high) {
        val mid = (low + high) ushr 1
        val midVal = this[mid]

        if (midVal < key) low = mid + 1
        else if (midVal > key) high = mid - 1
        else return mid // key found
    }
    return -(low + 1) // key not found.
}


inline fun <T> Array<T>.binarySearchBy(comparison: (T) -> Int): Int {

    var low = 0
    var high = size - 1

    while (low <= high) {
        val mid = (low + high).ushr(1) // safe from overflows
        val midVal = get(mid)
        val cmp = comparison(midVal)

        if (cmp < 0)
            low = mid + 1
        else if (cmp > 0)
            high = mid - 1
        else
            return mid // key found
    }
    return -(low + 1)  // key not found
}