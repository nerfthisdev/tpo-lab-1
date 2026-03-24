package org.example.bplustree

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class BPlusTreeTest {
    @ParameterizedTest
    @MethodSource("insertScenarios")
    fun `insert produces expected trace`(
        initialKeys: List<Int>,
        insertedKey: Int,
        expectedTrace: List<String>
    ) {
        val tree = BPlusTree(maxKeys = 7)
        initialKeys.forEach(tree::insert)

        val trace = tree.insert(insertedKey)

        assertIterableEquals(expectedTrace, trace)
    }

    @ParameterizedTest
    @MethodSource("searchScenarios")
    fun `search produces expected result and trace`(
        insertedKeys: List<Int>,
        searchedKey: Int,
        expectedResult: Boolean,
        expectedTrace: List<String>
    ) {
        val tree = BPlusTree(maxKeys = 7)
        insertedKeys.forEach(tree::insert)

        val (result, trace) = tree.containsWithTrace(searchedKey)

        assertEquals(expectedResult, result)
        assertIterableEquals(expectedTrace, trace)
    }

    companion object {
        @JvmStatic
        fun insertScenarios(): List<Array<Any>> =
            listOf(
                arrayOf(
                    emptyList<Int>(),
                    10,
                    listOf("INSERT_START", "LEAF_VISIT", "LEAF_INSERT", "LEAF_OK", "INSERT_END")
                ),
                arrayOf(
                    listOf(10, 20, 30, 40, 50, 60, 70),
                    80,
                    listOf(
                        "INSERT_START",
                        "LEAF_VISIT",
                        "LEAF_INSERT",
                        "LEAF_SPLIT",
                        "ROOT_SPLIT",
                        "INSERT_END"
                    )
                ),
                arrayOf(
                    listOf(10, 20, 30, 40, 50, 60, 70, 80),
                    55,
                    listOf(
                        "INSERT_START",
                        "INTERNAL_VISIT",
                        "DESCEND_1",
                        "LEAF_VISIT",
                        "LEAF_INSERT",
                        "LEAF_OK",
                        "INSERT_END"
                    )
                )
            )

        @JvmStatic
        fun searchScenarios(): List<Array<Any>> =
            listOf(
                arrayOf(
                    listOf(10, 20, 30, 40, 50, 60, 70, 80, 55),
                    55,
                    true,
                    listOf(
                        "SEARCH_START",
                        "SEARCH_INTERNAL",
                        "SEARCH_DESCEND_1",
                        "SEARCH_IN_LEAF",
                        "SEARCH_HIT"
                    )
                ),
                arrayOf(
                    listOf(10, 20, 30, 40, 50, 60, 70, 80, 55),
                    999,
                    false,
                    listOf(
                        "SEARCH_START",
                        "SEARCH_INTERNAL",
                        "SEARCH_DESCEND_1",
                        "SEARCH_IN_LEAF",
                        "SEARCH_MISS"
                    )
                )
            )
    }
}
