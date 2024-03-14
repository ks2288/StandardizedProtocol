@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.specter.sp

import dev.specter.auxi.ext.toInt
import dev.specter.sp.IOPacket.Companion.CRC_OFFSET
import dev.specter.sp.IOPacket.Companion.CRC_SIZE
import dev.specter.sp.IOPacket.Companion.INDEX_OFFSET
import dev.specter.sp.IOPacket.Companion.INDEX_SIZE
import dev.specter.sp.IOPacket.Companion.PARTS_OFFSET
import dev.specter.sp.IOPacket.Companion.PARTS_SIZE
import dev.specter.sp.IOPacket.Companion.PAYLOAD_OFFSET
import dev.specter.sp.IOPacket.Companion.PID_OFFSET
import dev.specter.sp.IOPacket.Companion.PID_SIZE
import dev.specter.sp.IOPacket.Companion.TYPE_OFFSET
import dev.specter.sp.IOPacket.Companion.TYPE_SIZE
import dev.specter.sp.util.CRC16Utils
import org.jetbrains.annotations.VisibleForTesting
import kotlin.math.ceil

object IOProcessor {
    /**
     * Default packet size in bytes, overridable via the [preparePackets] function
     */
    private const val DEFAULT_PACKET_SIZE = 64

    /**
     * Size of all properties other than the payload, in number of bytes
     */
    private val HEADER_SIZE: Int
        get() = CRC_SIZE + TYPE_SIZE + INDEX_SIZE + PARTS_SIZE

    /**
     * Builds a list of [IOPacket] instances from an aggregate set of [UByte]
     * values for outgoing communications
     *
     * @param processId unique identifier of the subprocess
     * @param data array of all uByte data to be sent
     * @param sliceSize size of each slice (packet payload)
     * @return list of [IOPacket] objects to be sent
     */
    @JvmStatic
    fun preparePackets(
        processId: Int,
        data: ByteArray,
        sliceSize: Int = DEFAULT_PACKET_SIZE
    ): List<IOPacket> {
        return with(arrayListOf<IOPacket>()) {
            val sliced = sliceData(
                data = data,
                sliceSize = sliceSize
            )
            sliced.forEachIndexed { i, p ->
                add(
                    object : IOPacket(
                        processId = processId,
                        crc = p.second.toInt(),
                        type = PacketType.DATA.ordinal,
                        index = i + 1,
                        parts = sliced.size,
                        payload = p.first
                    ) {}
                )
            }
            this
        }
    }

    /**
     * Takes a uByte array, and generates CRCs per a given slice count,
     * returned with a provided size
     *
     * @param data full block of uByte data to slice and CRC
     * @param sliceSize maximum size of each payload slice
     * @return list containing pairs of data slices and their CRC16 hashes
     */
    @VisibleForTesting
    @JvmStatic
    fun sliceData(
        data: ByteArray,
        sliceSize: Int = DEFAULT_PACKET_SIZE
    ): List<Pair<ByteArray, UShort>> {
        return with(arrayListOf<Pair<ByteArray, UShort>>()) {
            var remaining = data.size
            val count = ceil(data.size.toFloat() / sliceSize).toInt()
            for (i in 0 until count) {
                val start = sliceSize * i
                val size = sliceSize
                    .takeIf { sliceSize <= remaining }
                    ?: remaining
                remaining -= sliceSize
                // offset slice size by -1 for end-inclusive int range
                val end = start + size - 1
                val slice = data.slice(IntRange(start, end))
                val crc = CRC16Utils.crc16(
                    uBytes = slice.toByteArray().toUByteArray()
                )
                add(i, Pair(slice.toByteArray(), crc))
            }
            this
        }
    }

    /**
     * Strips the headers and returns the combined payloads of all given packets
     *
     * @param T instance type to be returned
     * @param packets
     */
    @JvmStatic
    inline fun <reified T: Any> combinePayloads(
        packets: List<IOPacket>,
        parse: (ByteArray) -> T?
    ): T? = try {
        val data = with(arrayListOf<Byte>()) {
            packets.forEach { addAll(it.payload.toList()) }
            this
        }
        parse.invoke(data.toByteArray())
    } catch (e: Exception) {
        System.err.println(e.localizedMessage)
        null
    }

    /**
     * Static method for quickly constructing [StandardizedProtocol]
     * instances from a provided incoming [UByteArray]
     *
     * @param T: [StandardizedProtocol]-compliant instance
     * @param bytes raw UBytes from which the object will be built
     * @return class instance with arbitrary payload size
     */
    @JvmStatic
    inline fun <reified T: StandardizedProtocol> parsePacket(
        bytes: ByteArray
    ): T = with(bytes) {
        val pid = slice(
            IntRange(
                PID_OFFSET,
                (PID_OFFSET + PID_SIZE) - 1
            )
        ).toByteArray()
            .toInt()
        val crc = slice(
            IntRange(
                CRC_OFFSET,
                (CRC_OFFSET + CRC_SIZE) - 1
            )
        ).toByteArray()
            .toInt()
        val type = slice(
            IntRange(
                TYPE_OFFSET,
                (TYPE_OFFSET + TYPE_SIZE) - 1
            )
        ).toByteArray()
            .toInt()
        val index = slice(
            IntRange(
                INDEX_OFFSET,
                (INDEX_OFFSET + INDEX_SIZE) - 1
            )
        ).toByteArray()
            .toInt()
        val parts = slice(
            IntRange(
                PARTS_OFFSET,
                (PARTS_OFFSET + PARTS_SIZE) - 1
            )
        ).toByteArray()
            .toInt()
        val payload = slice(
            IntRange(
                PAYLOAD_OFFSET,
                bytes.size - 1
            )
        ).toByteArray()

        object : IOPacket(
            processId = pid,
            crc = crc,
            type = type,
            index = index,
            parts = parts,
            payload = payload
        ) {} as T
    }
}
