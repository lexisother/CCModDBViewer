package info.c2dl.ccmoddbviewer

data class ModDB(
    var mods: Map<String, Mod>? = null,
)
data class Mod(
    var name: String? = null,
    var description: String? = null,
    var page: List<Page>,
    var archive_link: String? = null,
    var hash: Hash? = null,
    var version: String? = null,
)
data class Page(
    var name: String? = null,
    var url: String? = null,
)
data class Hash(
    var sha256: String? = null,
)
