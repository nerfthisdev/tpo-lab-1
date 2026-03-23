package org.example.bplustree

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BPlusTreeTest {
    @Test
    fun `insertion without split visits expected points`() {
        val tree = BPlusTree(maxKeys = 7)

        val trace = tree.insert(10)

        assertContentEquals(
            listOf("INSERT_START", "LEAF_VISIT", "LEAF_INSERT", "LEAF_OK", "INSERT_END"),
            trace
        )
    }

    @Test
    fun `root leaf split produces reference trace`() {
        val tree = BPlusTree(maxKeys = 7)
        listOf(10, 20, 30, 40, 50, 60, 70).forEach(tree::insert)

        val trace = tree.insert(80)

        assertContentEquals(
            listOf(
                "INSERT_START",
                "LEAF_VISIT",
                "LEAF_INSERT",
                "LEAF_SPLIT",
                "ROOT_SPLIT",
                "INSERT_END"
            ),
            trace
        )
    }

    @Test
    fun `internal insertion trace matches reference path`() {
        val tree = BPlusTree(maxKeys = 7)
        listOf(10, 20, 30, 40, 50, 60, 70, 80).forEach(tree::insert)

        val trace = tree.insert(55)

        assertContentEquals(
            listOf(
                "INSERT_START",
                "INTERNAL_VISIT",
                "DESCEND_1",
                "LEAF_VISIT",
                "LEAF_INSERT",
                "LEAF_OK",
                "INSERT_END"
            ),
            trace
        )
    }

    @Test
    fun `search hit follows expected checkpoints`() {
        val tree = BPlusTree(maxKeys = 7)
        listOf(10, 20, 30, 40, 50, 60, 70, 80, 55).forEach(tree::insert)

        val (result, trace) = tree.containsWithTrace(55)

        assertTrue(result)
        assertContentEquals(
            listOf(
                "SEARCH_START",
                "SEARCH_INTERNAL",
                "SEARCH_DESCEND_1",
                "SEARCH_IN_LEAF",
                "SEARCH_HIT"
            ),
            trace
        )
    }

    @Test
    fun `search miss follows expected checkpoints`() {
        val tree = BPlusTree(maxKeys = 7)
        listOf(10, 20, 30, 40, 50, 60, 70, 80, 55).forEach(tree::insert)

        val (result, trace) = tree.containsWithTrace(999)

        assertFalse(result)
        assertContentEquals(
            listOf(
                "SEARCH_START",
                "SEARCH_INTERNAL",
                "SEARCH_DESCEND_1",
                "SEARCH_IN_LEAF",
                "SEARCH_MISS"
            ),
            trace
        )
    }
}
