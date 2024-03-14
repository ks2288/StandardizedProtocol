package dev.specter.sp

import dev.specter.auxi.ext.toByteArray

/**
 * Concrete implementation of [StandardizedProtocol] for all packets being
 * sent between two application instances
 */
abstract class IOPacket(
    override val processId: Int,
    override val crc: Int,
    override val type: Int,
    override val index: Int,
    override val parts: Int,
    override val payload: ByteArray
) : StandardizedProtocol {

    /**
     * Concrete implementation of [StandardizedProtocol.toBytes]
     */
    override fun toBytes(): ByteArray = with(arrayListOf<Byte>()) {
        addAll(processId.toByteArray(size = PID_SIZE).toList())
        addAll(crc.toByteArray(size = CRC_SIZE).toList())
        addAll(type.toByteArray(size = TYPE_SIZE).toList())
        addAll(index.toByteArray(size = INDEX_SIZE).toList())
        addAll(parts.toByteArray(size = PARTS_SIZE).toList())
        addAll(payload.toList())
        this.toByteArray()
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
    }
}
