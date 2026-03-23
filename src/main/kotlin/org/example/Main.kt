package org.example

import org.example.bplustree.BPlusTree
import org.example.domain.BridgeScene
import org.example.domain.Captain
import org.example.domain.Detainee
import org.example.domain.Guard
import org.example.math.TaylorSin

fun main() {
    val sin = TaylorSin.evaluate(Math.PI / 6)
    println("sin(pi / 6) ~= $sin")

    val tree = BPlusTree(maxKeys = 7)
    listOf(10, 20, 5, 6, 12, 30, 7, 17).forEach(tree::insert)
    println("B+ tree contains 12: ${tree.contains(12)}")

    val scene = BridgeScene(
        captain = Captain("Captain"),
        guard = Guard("Guard"),
        detainees = mutableListOf(Detainee("First"), Detainee("Second"))
    )
    scene.executeEscort()
    println("Captain alone on bridge: ${scene.isCaptainAlone()}")
}
