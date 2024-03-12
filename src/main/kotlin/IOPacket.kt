@file:OptIn(ExperimentalUnsignedTypes::class)

import ext.toInt
import ext.toUByteArray

/**
 * Concrete implementation of [StandardizedProtocol] for all packets being
 * sent between two application instances
 */
data class IOPacket(
    override val processId: Int,
    override val crc: Int,
    override val type: Int,
    override val index: Int,
    override val parts: Int,
    override val payload: UByteArray
) : StandardizedProtocol {

    /**
     * Concrete implementation of [StandardizedProtocol.toUBytes]
     */
    override fun toUBytes(): UByteArray = with(arrayListOf<UByte>()) {
        addAll(processId.toUByteArray(size = PID_SIZE))
        addAll(crc.toUByteArray(size = CRC_SIZE))
        addAll(type.toUByteArray(size = TYPE_SIZE))
        addAll(index.toUByteArray(size = INDEX_SIZE))
        addAll(parts.toUByteArray(size = PARTS_SIZE))
        addAll(payload)
        this.toUByteArray()
    }

    companion object {
        const val PID_OFFSET = 0
        const val PID_SIZE = 2
        const val CRC_OFFSET = 2
        const val CRC_SIZE = 2
        const val TYPE_OFFSET = 4
        const val TYPE_SIZE = 2
        const val INDEX_OFFSET = 6
        const val INDEX_SIZE = 3
        const val PARTS_OFFSET = 9
        const val PARTS_SIZE = 3
        const val PAYLOAD_OFFSET = 12

        /**
         * Static method for quickly constructing [IOPacket] instances from a
         * provided [UByteArray]
         *
         * @param bytes raw UBytes from which the object will be built
         * @return class instance with arbitrary [payload] size
         */
        @JvmStatic
        fun fromUBytes(bytes: UByteArray): IOPacket =
            with(bytes) {
                val pid = slice(
                    IntRange(
                        PID_OFFSET,
                        PID_SIZE - 1
                    )
                ).toUByteArray()
                    .toByteArray()
                    .toInt()
                val crc = slice(
                    IntRange(
                        CRC_OFFSET,
                        (CRC_OFFSET + CRC_SIZE) - 1
                    )
                ).toUByteArray()
                    .toByteArray()
                    .toInt()
                val type = slice(
                    IntRange(
                        TYPE_OFFSET,
                        (TYPE_OFFSET + TYPE_SIZE) - 1
                    )
                ).toUByteArray()
                    .toByteArray()
                    .toInt()
                val index = slice(
                    IntRange(
                        INDEX_OFFSET,
                        (INDEX_OFFSET + INDEX_SIZE) - 1
                    )
                ).toUByteArray()
                    .toByteArray()
                    .toInt()
                val parts = slice(
                    IntRange(
                        PARTS_OFFSET,
                        (PARTS_OFFSET + PARTS_SIZE) - 1
                    )
                ).toUByteArray()
                    .toByteArray()
                    .toInt()
                val payload = slice(
                    IntRange(
                        PAYLOAD_OFFSET,
                        bytes.size - 1
                    )
                ).toUByteArray()

                IOPacket(
                    processId = pid,
                    crc = crc,
                    type = type,
                    index = index,
                    parts = parts,
                    payload = payload
                )
            }
    }
}
