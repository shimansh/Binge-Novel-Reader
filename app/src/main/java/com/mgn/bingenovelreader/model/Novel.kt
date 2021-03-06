package com.mgn.bingenovelreader.model

class Novel {
    var id: Long = -1
    var name: String? = null
    var url: String? = null
    var author: String? = null
    var imageUrl: String? = null
    var rating: String? = null
    var shortDescription: String? = null
    var longDescription: String? = null
    var imageFilePath: String? = null
    var genres: List<String>? = null
    var favorite: Int = 0
    var currentWebPageId: Long = -1

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val novel = o as Novel?
        return if (name != null) name == novel!!.name else novel!!.name == null
    }

    override fun hashCode(): Int {
        return if (name != null) name!!.hashCode() else 0
    }
}
