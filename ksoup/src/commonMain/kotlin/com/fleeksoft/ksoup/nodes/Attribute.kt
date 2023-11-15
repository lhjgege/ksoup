package com.fleeksoft.ksoup.nodes

import okio.IOException
import com.fleeksoft.ksoup.SerializationException
import com.fleeksoft.ksoup.helper.Validate
import com.fleeksoft.ksoup.internal.StringUtil
import com.fleeksoft.ksoup.nodes.Document.OutputSettings.Syntax
import com.fleeksoft.ksoup.ported.Cloneable

/**
 * A single key + value attribute. (Only used for presentation.)
 */
open class Attribute : Map.Entry<String, String?>, Cloneable<Attribute> {
    private var attributeKey: String

    /*@Nullable*/
    private var attributeValue: String?

    /*@Nullable*/
    var parent: Attributes?

    /**
     * Create a new attribute from unencoded (raw) key and value.
     * @param key attribute key; case is preserved.
     * @param value attribute value (may be null)
     * @see .createFromEncoded
     */
    constructor(key: String, /*@Nullable*/ value: String?) : this(key, value, null)

    /**
     * Get the attribute key.
     * @return the attribute key
     */
    override val key: String
        get() = attributeKey

    /**
     * Set the attribute key; case is preserved.
     * @param key the new key; must not be null
     */
    fun setKey(key: String) {
        var key = key
        key = key.trim { it <= ' ' }
        Validate.notEmpty(key) // trimming could potentially make empty, so validate here
        if (parent != null) {
            val i: Int = parent!!.indexOfKey(this.attributeKey)
            if (i != Attributes.NotFound) parent!!.keys[i] = key
        }
        this.attributeKey = key
    }

    override val value: String
        /**
         * Get the attribute value. Will return an empty string if the value is not set.
         * @return the attribute value
         */
        get() = Attributes.checkNotNull(attributeValue)

    /**
     * Check if this Attribute has a value. Set boolean attributes have no value.
     * @return if this is a boolean attribute / attribute without a value
     */
    fun hasDeclaredValue(): Boolean {
        return attributeValue != null
    }

    /**
     * Set the attribute value.
     * @param newValue the new attribute value; may be null (to set an enabled boolean attribute)
     * @return the previous value (if was null; an empty string)
     */
    fun setValue(/*@Nullable*/ newValue: String?): String? {
        var oldVal = this.attributeValue
        if (parent != null) {
            val i: Int = parent!!.indexOfKey(attributeKey)
            if (i != Attributes.NotFound) {
                oldVal = parent!![attributeKey] // trust the container more
                parent!!.vals[i] = newValue
            }
        }
        this.attributeValue = newValue
        return Attributes.checkNotNull(oldVal)
    }

    /**
     * Get the HTML representation of this attribute; e.g. `href="index.html"`.
     * @return HTML
     */
    fun html(): String {
        val sb: StringBuilder = StringUtil.borrowBuilder()
        try {
            html(sb, Document("").outputSettings())
        } catch (exception: IOException) {
            throw SerializationException(exception)
        }
        return StringUtil.releaseBuilder(sb)
    }

    @Throws(IOException::class)
    protected fun html(accum: Appendable, out: Document.OutputSettings) {
        html(attributeKey, attributeValue, accum, out)
    }

    /**
     * Create a new attribute from unencoded (raw) key and value.
     * @param key attribute key; case is preserved.
     * @param val attribute value (may be null)
     * @param parent the containing Attributes (this Attribute is not automatically added to said Attributes)
     * @see .createFromEncoded
     */
    constructor(
        key: String, /*@Nullable*/
        value: String?, /*@Nullable*/
        parent: Attributes?,
    ) {
        var key = key
        key = key.trim { it <= ' ' }
        Validate.notEmpty(key) // trimming could potentially make empty, so validate here
        this.attributeKey = key
        this.attributeValue = value
        this.parent = parent
    }

    /**
     * Get the string representation of this attribute, implemented as [.html].
     * @return string
     */
    override fun toString(): String {
        return html()
    }

    fun isDataAttribute(): Boolean = isDataAttribute(attributeKey)

    /**
     * Collapsible if it's a boolean attribute and value is empty or same as name
     *
     * @param out output settings
     * @return Returns whether collapsible or not
     */
    protected fun shouldCollapseAttribute(out: Document.OutputSettings): Boolean {
        return shouldCollapseAttribute(attributeKey, attributeValue, out)
    }

    override fun equals(/*@Nullable*/ o: Any?): Boolean { // note parent not considered
        if (this === o) return true
        if (o == null || this::class != o::class) return false
        val attribute: Attribute = o as Attribute
        if (attributeKey != attribute.attributeKey) return false
        return if (attributeValue != null) attributeValue == attribute.attributeValue else attribute.attributeValue == null
    }

    override fun hashCode(): Int { // note parent not considered
        var result = attributeKey.hashCode()
        result = 31 * result + if (attributeValue != null) attributeValue.hashCode() else 0
        return result
    }

    override fun clone(): Attribute {
        val attribute = Attribute(attributeKey, attributeValue)
        attribute.parent = this.parent
        return attribute
    }

    companion object {
        private val booleanAttributes = arrayOf(
            "allowfullscreen",
            "async",
            "autofocus",
            "checked",
            "compact",
            "declare",
            "default",
            "defer",
            "disabled",
            "formnovalidate",
            "hidden",
            "inert",
            "ismap",
            "itemscope",
            "multiple",
            "muted",
            "nohref",
            "noresize",
            "noshade",
            "novalidate",
            "nowrap",
            "open",
            "readonly",
            "required",
            "reversed",
            "seamless",
            "selected",
            "sortable",
            "truespeed",
            "typemustmatch",
        )

        @Throws(IOException::class)
        protected fun html(
            key: String,
            /*@Nullable*/
            value: String?,
            accum: Appendable,
            out: Document.OutputSettings,
        ) {
            val resultKey: String = getValidKey(key, out.syntax()) ?: return // can't write it :(
            htmlNoValidate(resultKey, value, accum, out)
        }

        @Throws(IOException::class)
        fun htmlNoValidate(
            key: String,
            /*@Nullable*/
            value: String?,
            accum: Appendable,
            out: Document.OutputSettings,
        ) {
            // structured like this so that Attributes can check we can write first, so it can add whitespace correctly
            accum.append(key)
            if (!shouldCollapseAttribute(key, value, out)) {
                accum.append("=\"")
                Entities.escape(
                    accum,
                    Attributes.checkNotNull(value),
                    out,
                    inAttribute = true,
                    normaliseWhite = false,
                    stripLeadingWhite = false,
                    trimTrailing = false,
                )
                accum.append('"')
            }
        }

        private val xmlKeyValid: Regex =
            Regex("[a-zA-Z_:][-a-zA-Z0-9_:.]*")
        private val xmlKeyReplace: Regex =
            Regex("[^-a-zA-Z0-9_:.]")
        private val htmlKeyValid: Regex =
            Regex("[^\\x00-\\x1f\\x7f-\\x9f \"'/=]+")
        private val htmlKeyReplace: Regex =
            Regex("[\\x00-\\x1f\\x7f-\\x9f \"'/=]")

        fun getValidKey(key: String, syntax: Syntax): String? {
            return when (syntax) {
                Syntax.xml -> {
                    if (!xmlKeyValid.matches(key)) {
                        val newKey = xmlKeyReplace.replace(key, "")
                        if (xmlKeyValid.matches(newKey)) newKey else null
                    } else {
                        key
                    }
                }

                Syntax.html -> {
                    if (!htmlKeyValid.matches(key)) {
                        val newKey = htmlKeyReplace.replace(key, "")
                        if (htmlKeyValid.matches(newKey)) newKey else null
                    } else {
                        key
                    }
                }
            }
        }

        /*@Nullable*/
        /*fun getValidKey(key: String?, syntax: Syntax): String? {
            // we consider HTML attributes to always be valid. XML checks key validity
            var key = key
            if (syntax === Syntax.xml && !xmlKeyValid.matcher(key).matches()) {
                key = xmlKeyReplace.matcher(key).replaceAll("")
                return if (xmlKeyValid.matcher(key)
                        .matches()
                ) {
                    key
                } else {
                    null // null if could not be coerced
                }
            } else if (syntax === Syntax.html && !htmlKeyValid.matcher(key).matches()) {
                key = htmlKeyReplace.matcher(key).replaceAll("")
                return if (htmlKeyValid.matcher(key)
                        .matches()
                ) {
                    key
                } else {
                    null // null if could not be coerced
                }
            }
            return key
        }*/

        /**
         * Create a new Attribute from an unencoded key and a HTML attribute encoded value.
         * @param unencodedKey assumes the key is not encoded, as can be only run of simple \w chars.
         * @param encodedValue HTML attribute encoded value
         * @return attribute
         */
        fun createFromEncoded(unencodedKey: String, encodedValue: String): Attribute {
            val value: String = Entities.unescape(encodedValue, true)
            return Attribute(unencodedKey, value, null) // parent will get set when Put
        }

        protected fun isDataAttribute(key: String): Boolean {
            return key.startsWith(Attributes.dataPrefix) && key.length > Attributes.dataPrefix.length
        }

        // collapse unknown foo=null, known checked=null, checked="", checked=checked; write out others
        protected fun shouldCollapseAttribute(
            key: String,
            /*@Nullable*/
            value: String?,
            out: Document.OutputSettings,
        ): Boolean {
            return out.syntax() === Syntax.html &&
                    (
                            value == null || (
                                    value.isEmpty() || value.equals(
                                        key,
                                        ignoreCase = true,
                                    )
                                    ) && isBooleanAttribute(key)
                            )
        }

        /**
         * Checks if this attribute name is defined as a boolean attribute in HTML5
         */
        fun isBooleanAttribute(key: String): Boolean {
            return booleanAttributes.toList().binarySearch { it.compareTo(key.lowercase()) } >= 0
        }
    }
}
