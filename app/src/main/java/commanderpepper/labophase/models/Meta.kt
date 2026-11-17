package commanderpepper.labophase.models

sealed class Meta(val id: Int, val name: String) {
    object OP16           : Meta(id = 1, name = "OP16")
    object OP16WithST     : Meta(id = 2, name = "OP16.5")
    object OP17           : Meta(id = 4, name = "OP17")
    object EB05           : Meta(id = 5, name = "EB05")
    object OP18           : Meta(id = 6, name = "OP18")
    object ExtraGrandBattle : Meta(id = 3, name = "Extra Grand Battle")
}

val METAS_LIST = listOf(
    Meta.OP16, Meta.OP16WithST, Meta.OP17, Meta.EB05, Meta.OP18, Meta.ExtraGrandBattle
)

val DEFAULT_META: Meta = Meta.OP18

fun metaById(id: Int): Meta? = METAS_LIST.firstOrNull { it.id == id }
