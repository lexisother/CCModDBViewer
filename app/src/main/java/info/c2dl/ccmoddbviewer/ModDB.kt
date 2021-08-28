package info.c2dl.ccmoddbviewer

typealias ModDB = Map<String, Mod>

data class Mod(
    val metadata: ModMeta
)

data class ModMeta(
    val name: String,
    val version: String,
    val description: String? = null,
    val homepage: String? = null,
    val license: String? = null,
    val ccmodDependencies: Map<String, String>? = null,
)
