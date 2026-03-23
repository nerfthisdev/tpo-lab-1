package org.example.bplustree

class BPlusTree(private val maxKeys: Int = 7) {
    init {
        require(maxKeys >= 3) { "maxKeys must be at least 3" }
    }

    sealed class Node {
        abstract val keys: MutableList<Int>
    }

    data class InternalNode(
        override val keys: MutableList<Int> = mutableListOf(),
        val children: MutableList<Node> = mutableListOf()
    ) : Node()

    data class LeafNode(
        override val keys: MutableList<Int> = mutableListOf(),
        var next: LeafNode? = null
    ) : Node()

    private data class SplitResult(val separator: Int, val rightNode: Node)

    private var root: Node = LeafNode()

    fun insert(key: Int): List<String> {
        val trace = mutableListOf<String>()
        trace += "INSERT_START"

        val split = insertRecursive(root, key, trace)
        if (split != null) {
            trace += "ROOT_SPLIT"
            root = InternalNode(
                keys = mutableListOf(split.separator),
                children = mutableListOf(root, split.rightNode)
            )
        }

        trace += "INSERT_END"
        return trace
    }

    fun contains(key: Int): Boolean {
        val leaf = findLeaf(key, mutableListOf())
        return key in leaf.keys
    }

    fun containsWithTrace(key: Int): Pair<Boolean, List<String>> {
        val trace = mutableListOf("SEARCH_START")
        val leaf = findLeaf(key, trace)
        trace += "SEARCH_IN_LEAF"
        val result = key in leaf.keys
        trace += if (result) "SEARCH_HIT" else "SEARCH_MISS"
        return result to trace
    }

    private fun insertRecursive(node: Node, key: Int, trace: MutableList<String>): SplitResult? {
        return when (node) {
            is LeafNode -> insertIntoLeaf(node, key, trace)
            is InternalNode -> insertIntoInternal(node, key, trace)
        }
    }

    private fun insertIntoLeaf(node: LeafNode, key: Int, trace: MutableList<String>): SplitResult? {
        trace += "LEAF_VISIT"
        if (key !in node.keys) {
            node.keys += key
            node.keys.sort()
            trace += "LEAF_INSERT"
        } else {
            trace += "LEAF_DUPLICATE_SKIP"
            return null
        }

        if (node.keys.size <= maxKeys) {
            trace += "LEAF_OK"
            return null
        }

        trace += "LEAF_SPLIT"
        val middle = node.keys.size / 2
        val rightKeys = node.keys.subList(middle, node.keys.size).toMutableList()
        val leftKeys = node.keys.subList(0, middle).toMutableList()

        node.keys.clear()
        node.keys += leftKeys

        val rightNode = LeafNode(keys = rightKeys, next = node.next)
        node.next = rightNode

        return SplitResult(separator = rightNode.keys.first(), rightNode = rightNode)
    }

    private fun insertIntoInternal(node: InternalNode, key: Int, trace: MutableList<String>): SplitResult? {
        trace += "INTERNAL_VISIT"
        val childIndex = childIndex(node, key)
        trace += "DESCEND_$childIndex"
        val split = insertRecursive(node.children[childIndex], key, trace) ?: return null

        node.keys.add(childIndex, split.separator)
        node.children.add(childIndex + 1, split.rightNode)
        trace += "INTERNAL_PROMOTE"

        if (node.keys.size <= maxKeys) {
            trace += "INTERNAL_OK"
            return null
        }

        trace += "INTERNAL_SPLIT"
        val separatorIndex = node.keys.size / 2
        val promotedKey = node.keys[separatorIndex]

        val rightKeys = node.keys.subList(separatorIndex + 1, node.keys.size).toMutableList()
        val rightChildren = node.children.subList(separatorIndex + 1, node.children.size).toMutableList()

        val leftKeys = node.keys.subList(0, separatorIndex).toMutableList()
        val leftChildren = node.children.subList(0, separatorIndex + 1).toMutableList()

        node.keys.clear()
        node.keys += leftKeys
        node.children.clear()
        node.children += leftChildren

        val rightNode = InternalNode(keys = rightKeys, children = rightChildren)
        return SplitResult(separator = promotedKey, rightNode = rightNode)
    }

    private fun findLeaf(key: Int, trace: MutableList<String>): LeafNode {
        var current = root
        while (current is InternalNode) {
            trace += "SEARCH_INTERNAL"
            val nextIndex = childIndex(current, key)
            trace += "SEARCH_DESCEND_$nextIndex"
            current = current.children[nextIndex]
        }
        return current
    }

    private fun childIndex(node: InternalNode, key: Int): Int {
        var index = 0
        while (index < node.keys.size && key >= node.keys[index]) {
            index++
        }
        return index
    }
}
