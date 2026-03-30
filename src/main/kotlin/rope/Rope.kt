package rope

sealed class Rope {

    abstract val length: Int

    data class Leaf(val text: String) : Rope() {
        override val length: Int = text.length

        override fun toString(): String = this.text
    }

    companion object {
        fun fromString(s: String): Rope = Leaf(s)

        fun concat(a: Rope, b: Rope): Rope {
            return Leaf(a.toString() + b.toString())
        }
    }

    override fun toString(): String {
        return when (this) {
            is Leaf -> this.text
        }
    }
}