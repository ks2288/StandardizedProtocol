package util

import dev.specter.auxi.ext.toBooleanArray
import dev.specter.auxi.ext.toFloat
import dev.specter.auxi.ext.toLong
import dev.specter.auxi.ext.toShiftedInt

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
