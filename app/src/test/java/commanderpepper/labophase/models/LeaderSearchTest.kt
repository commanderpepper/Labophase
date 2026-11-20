package commanderpepper.labophase.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LeaderSearchTest {

    private val leaders = listOf(Leader.GYRobin, Leader.UGLuffy, Leader.PYRobin, Leader.YLuffy, Leader.RShanks)

    @Test
    fun `blank query returns the full list`() {
        assertEquals(leaders, leaders.searchByName(""))
        assertEquals(leaders, leaders.searchByName("   "))
    }

    @Test
    fun `matches character name case-insensitively`() {
        assertEquals(listOf(Leader.GYRobin, Leader.PYRobin), leaders.searchByName("ROBIN"))
    }

    @Test
    fun `matches partial character name`() {
        assertEquals(listOf(Leader.UGLuffy, Leader.YLuffy), leaders.searchByName("luf"))
    }

    @Test
    fun `color initials alone do not match`() {
        assertTrue(leaders.searchByName("gy").isEmpty())
        assertTrue(leaders.searchByName("py").isEmpty())
        assertTrue(leaders.searchByName("ug").isEmpty())
    }

    @Test
    fun `matches card name`() {
        assertEquals(listOf(Leader.GYRobin, Leader.PYRobin), leaders.searchByName("nico"))
        assertEquals(listOf(Leader.UGLuffy, Leader.YLuffy), leaders.searchByName("Monkey.D"))
    }

    @Test
    fun `matches a card name that differs from the display name`() {
        val withSunday = leaders + Leader.UAllSunday
        assertEquals(listOf(Leader.UAllSunday), withSunday.searchByName("ms. all"))
        assertEquals(listOf(Leader.RGOden), listOf(Leader.RGOden, Leader.RShanks).searchByName("kouzuki"))
    }

    @Test
    fun `matches part of a card name containing quotes`() {
        assertEquals(listOf(Leader.YKid), listOf(Leader.YKid, Leader.RShanks).searchByName("captain"))
    }

    @Test
    fun `query with a space matches the full display name`() {
        assertEquals(listOf(Leader.GYRobin), leaders.searchByName("gy robin"))
    }

    @Test
    fun `surrounding whitespace is ignored`() {
        assertEquals(listOf(Leader.RShanks), leaders.searchByName("  shanks "))
    }

    @Test
    fun `no match returns an empty list`() {
        assertTrue(leaders.searchByName("zoro").isEmpty())
    }

    @Test
    fun `keeps the original order`() {
        val reversed = leaders.reversed()
        assertEquals(listOf(Leader.PYRobin, Leader.GYRobin), reversed.searchByName("robin"))
    }
}
