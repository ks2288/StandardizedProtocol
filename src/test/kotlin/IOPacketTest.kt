import dev.specter.auxi.ext.toByte
import dev.specter.sp.IOPacket
import dev.specter.sp.IOProcessor
import dev.specter.sp.PacketType
import org.junit.After
import org.junit.Before
import org.junit.Test
import util.TestConstants
import util.TestReflector
import kotlin.test.assertEquals

class IOPacketTest {
    private lateinit var packet: IOPacket
    @Before
    fun setup() {
        packet = IOProcessor.parsePacket<IOPacket>(
            TestConstants.TEST_RAW_PACKET_DATA
        )
    }

    @After
    fun teardown() {}

    @Test
    fun testSliceData() {
        val expected = IOProcessor.sliceData(data = TEST_PAYLOAD)
        assert(expected.size == 2 && expected.first().first.size == 64)
    }

    @Test
    fun testSliceSizeVariance() {
        val expected = IOProcessor.sliceData(
            data = TEST_PAYLOAD,
            sliceSize = TEST_SLICE_SIZE
        )
        assert(expected.size == 16)
    }

    @Test
    fun testBuildMultipart() {
        val packets = IOProcessor.preparePackets(
            processId = 0,
            data = TEST_PAYLOAD
        )
        assert(packets.size == EXPECTED_MP_MESSAGE_SIZE)
        packets.forEachIndexed { i, p ->
            assert(p.index == i + 1 && p.parts == packets.size)
        }
    }

    @Test
    fun testBuildSinglePart() {
        val packets = IOProcessor.preparePackets(
            processId = 1,
            data = TestConstants.TEST_PACKET_DATA
        )
        assert(packets.size == 1)
        packets.forEachIndexed { i, p ->
            assert(p.index == i + 1 && p.parts == packets.size)
        }
    }

    @Test
    fun testBuildPacket() {
        val i =
            assertEquals(
                listOf(
                    packet.processId,
                    packet.crc,
                    packet.type,
                    packet.index,
                    packet.parts,
                    packet.payload.size
                ),
                listOf(
                    EXPECTED_PID,
                    EXPECTED_CRC,
                    EXPECTED_PACKET_TYPE,
                    EXPECTED_MULTIPART_VALUE,
                    EXPECTED_MULTIPART_VALUE,
                    EXPECTED_PAYLOAD_SIZE
                )
            )
    }

    @Test
    fun testReflectUBytesToClass() {
        val reflector:TestReflector? =
            IOProcessor.combinePayloads(listOf(packet)) {
                TestReflector.fromUBytes(it)
            }

        assertEquals(
            listOf(
                reflector?.p1,
                reflector?.p2,
                reflector?.p3?.toByte(),
                reflector?.p4,
                reflector?.p5
            ),
            listOf(
                EXPECTED_P1,
                EXPECTED_P2,
                EXPECTED_P3.toByte(),
                EXPECTED_P4,
                EXPECTED_P5
            )
        )
    }

    @Test
    fun testReflectBytesToProperty() {
        val intBytes = TestReflector.getP1Bytes(packet.payload)
        val stringBytes = TestReflector.getP2Bytes(packet.payload)
        val boolArrayBytes = TestReflector.getP3Bytes(packet.payload)
        val longBytes = TestReflector.getP4Bytes(packet.payload)
        val floatBytes = TestReflector.getP5Bytes(packet.payload)
        val i = IOProcessor.reflectProperty<Int>(intBytes)
        val s = IOProcessor.reflectProperty<String>(stringBytes)
        val b = IOProcessor.reflectProperty<BooleanArray>(boolArrayBytes)
        val l = IOProcessor.reflectProperty<Long>(longBytes)
        val f = IOProcessor.reflectProperty<Float>(floatBytes)
        assertEquals(
            listOf(i, s, l, f),
            listOf(
                EXPECTED_P1,
                EXPECTED_P2,
                EXPECTED_P4,
                EXPECTED_P5
            )
        )
        b?.forEachIndexed { index, bool, ->
            assert(bool == EXPECTED_P3[index])
        }
    }

    companion object {
        private const val TEST_SLICE_SIZE = 8
        private const val EXPECTED_MP_MESSAGE_SIZE = 2
        // 128 ubyte payload data
        private val TEST_PAYLOAD: ByteArray
            get() {
                val reversedData = TestConstants.TEST_PACKET_DATA
                    .reversedArray()
                return with(arrayListOf<Byte>()) {
                    // add one from the reversed copy, and one from the original
                    // back-to-back for arbitrary variance
                    reversedData.forEachIndexed { i, b ->
                        add(b)
                        add(TestConstants.TEST_PACKET_DATA[i])
                    }
                    this.toByteArray()
                }
            }
        private const val EXPECTED_P1 = 61538
        private const val EXPECTED_P2 = "asdf"
        private val EXPECTED_P3 = booleanArrayOf(
            true,
            true,
            false,
            true,
            false,
            true,
            true,
            false
        )
        private const val EXPECTED_P4 = 9223372036854775801
        private const val EXPECTED_P5 = 651.77905f
        private const val EXPECTED_PID = 16
        private val EXPECTED_PACKET_TYPE = PacketType.DATA.ordinal
        private const val EXPECTED_MULTIPART_VALUE = 1
        private const val EXPECTED_PAYLOAD_SIZE = 19
        private const val EXPECTED_CRC = 0
    }
}
