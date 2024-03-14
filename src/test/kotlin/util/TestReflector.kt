@file:OptIn(ExperimentalUnsignedTypes::class)

package util

import kotlin.math.pow

/**
 * Test-only class for both testing the Standardized Protocol implementation
 * and also illustrating class reflection from raw byte data using five
 * popular native types
 *
 * @property p1 2-byte integer
 * @property p2 4-byte string literal
 * @property p3 1-byte boolean array
 * @property p4 8-byte long
 * @property p5 4-byte float per IEEE-754
 */
class TestReflector(
    val p1: Int,
    val p2: String,
    val p3: BooleanArray,
    val p4: Long,
    val p5: Float
) {
    companion object {
        private const val P1_OFFSET = 0
        private const val P2_OFFSET = 2
        private const val P3_OFFSET = 6
        private const val P4_OFFSET = 7
        private const val P5_OFFSET = 15
        private const val P1_SIZE = 2
        private const val P2_SIZE = 4
        private const val P3_SIZE = 1
        private const val P4_SIZE = 8
        private const val P5_SIZE = 4

        @JvmStatic
        fun getP1Bytes(payload: ByteArray): ByteArray =
            payload.slice(
                IntRange(
                    P1_OFFSET,
                    (P1_OFFSET + P1_SIZE) - 1
                )
            ).toByteArray()

        @JvmStatic
        fun getP2Bytes(payload: ByteArray): ByteArray =
            payload.slice(
                IntRange(
                    P2_OFFSET,
                    (P2_OFFSET + P2_SIZE) - 1
                )
            ).toByteArray()

        @JvmStatic
        fun getP3Bytes(payload: ByteArray): ByteArray =
            payload.slice(
                IntRange(
                    P3_OFFSET,
                    (P3_OFFSET + P3_SIZE) - 1
                )
            ).toByteArray()

        @JvmStatic
        fun getP4Bytes(payload: ByteArray): ByteArray =
            payload.slice(
                IntRange(
                    P4_OFFSET,
                    (P4_OFFSET + P4_SIZE) - 1
                )
            ).toByteArray()

        @JvmStatic
        fun getP5Bytes(payload: ByteArray): ByteArray =
            payload.slice(
                IntRange(
                    P5_OFFSET,
                    (P5_OFFSET + P5_SIZE) - 1
                )
            ).toByteArray()

        @JvmStatic
        fun fromUBytes(payload: ByteArray): TestReflector {
            val p1 = getP1Bytes(payload).toShiftedInt()
            val p2 = String(getP2Bytes(payload))
            val p3 = getP3Bytes(payload).toBooleanArray()
            val p4 = getP4Bytes(payload).toLong()
            val p5 = getP5Bytes(payload).toFloat()
            return TestReflector(p1, p2, p3, p4, p5)
        }
    }
}

/**
 * Shifts through a [ByteArray] of arbitrary length to compound a [Long] value
 *
 * @return compounded [Long] value
 */
fun ByteArray.toLong(): Long {
    var value = 0L
    for (b in this) { value = (value shl 8) + (b.toInt() and 255) }
    return value
}

/**
 * Takes a 4-byte array and computes a Float32 according to the IEEE754 spec
 *
 * @return [Float] from 3 bytes of value data, and one byte of exponent data
 */
fun ByteArray.toFloat(): Float {
    if(this.size != 4) return 0.0f
    val sig = this.slice(IntRange(0,2))
        .toByteArray()
        .toShiftedInt()
    val exp = this[3].toShiftedInt().shr(4)
    val isPos = this[3].toInt() % 2 == 0

    return sig.toFloat() * 10f.pow(if (isPos) exp else -exp)
}

/**
 * Computes a [BooleanArray] from a [ByteArray] by first calculating a binary
 * string and then taking the 8 individual bits as [Boolean] flags
 *
 * @return computed array of flags
 */
fun ByteArray.toBooleanArray(): BooleanArray {
    val sb = StringBuilder()
    map {
         val s = String.format(
             "%8s",
             Integer.toBinaryString(it.toInt())
         ).replace(' ', '0')
        sb.append(s)
    }
    return with(arrayListOf<Boolean>()) {
        sb.forEach { c ->
            add(c.toString().toInt() == 1)
        }
        this.toBooleanArray().reversedArray()
    }
}

/**
 * [UByteArray] wrapper for compounding an integer value by shifting through a
 * [ByteArray]
 *
 * @return compounded [Int] value
 */
fun UByteArray.toShiftedInt() = this.toByteArray().toShiftedInt()

/**
 * Compounds an integer value by shifting through a [ByteArray]
 *
 * @return compounded [Int] value
 */
fun ByteArray.toShiftedInt(): Int {
    var value = 0
    for (b in this) { value = (value shl 8) + (b.toInt() and 255) }
    return value
}

/**
 * Shifts a [Byte] to ensure all bits are used in calculation
 *
 * @return calculated [Int] value
 */
fun Byte.toShiftedInt(): Int = (0 shl 8) + (this.toInt() and 255)

/**
 * Shifts a [UByte] to ensure all bits are used in calculation
 *
 * @return calculated [Int] value
 */
fun UByte.toShiftedInt(): Int = this.toByte().toShiftedInt()
