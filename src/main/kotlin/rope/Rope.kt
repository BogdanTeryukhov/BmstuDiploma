package rope

sealed class Rope {

    abstract val length: Int

    data class Leaf(val text: String) : Rope() {
        override val length: Int = text.length
    }

    data class Node(
        val left: Rope,
        val right: Rope
    ) : Rope() {
        override val length: Int = left.length + right.length
    }

    companion object {

        fun fromString(s: String): Rope = Leaf(s)

        fun concat(a: Rope, b: Rope): Rope {
            if (a.length == 0) return b
            if (b.length == 0) return a
            return Node(a, b)
        }
    }
}