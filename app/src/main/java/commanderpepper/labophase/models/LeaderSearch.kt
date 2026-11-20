package commanderpepper.labophase.models

/** The name without its color initials, e.g. "Robin" for "GY Robin". */
val Leader.characterName: String get() = name.substringAfter(' ')

/**
 * Filters leaders by name, keeping the list's order. Matches the character name and the
 * card name, so "robin" finds "GY Robin" and "trafalgar" finds the Laws, but "y" doesn't
 * find every Yellow leader. A query with a space can also match the full display name,
 * e.g. "gy robin".
 */
fun List<Leader>.searchByName(query: String): List<Leader> {
    val q = query.trim()
    if (q.isEmpty()) return this
    return filter {
        it.characterName.contains(q, ignoreCase = true) ||
            it.cardName.contains(q, ignoreCase = true) ||
            (q.contains(' ') && it.name.contains(q, ignoreCase = true))
    }
}
