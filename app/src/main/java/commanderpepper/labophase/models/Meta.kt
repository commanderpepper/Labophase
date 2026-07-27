package commanderpepper.labophase.models

sealed class Meta(val id: Int, val name: String) {
    object OP16           : Meta(id = 1, name = "OP16")
    object OP16WithST     : Meta(id = 2, name = "OP16.5")
    object ExtraGrandBattle : Meta(id = 3, name = "Extra Grand Battle")
}

val METAS_LIST = listOf(
    Meta.OP16, Meta.OP16WithST, Meta.ExtraGrandBattle
)

fun metaById(id: Int): Meta? = METAS_LIST.firstOrNull { it.id == id }
