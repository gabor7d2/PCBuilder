package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.types.RamPlatform;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class Ram extends ComponentBase {

    private final static List<Ram> ramList = new ArrayList<>(0);

    public final static String[] NODE_NAMES = new String[]{"capacity_per_piece_mb", "count", "latency"};

    private static IXmlComponentBuilder DATA_HANDLER = (ram, properties) -> {
        RamPlatform ramPlatform = RamPlatform.getRamPlatform(ram.getNode("RamType"));

        ramList.add(new Ram(ram.getComponentInfo(), properties, ramPlatform));
    };

    public final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.COMPONENTS, "rams.xml", "Ram", NODE_NAMES, DATA_HANDLER);

    static {
        XmlParser.parseXmlComponents(CONTRACT);
    }

    private final RamPlatform platform;
    private final int capacityPerPieceMB;
    private final short count;
    private final short latency;

    public Ram(NodeList componentInfoNode, ComponentProperties properties, RamPlatform platform) {
        super(componentInfoNode, CONTRACT);

        this.platform = platform;
        capacityPerPieceMB = properties.getInt(0);
        count = properties.getShort(1);
        latency = properties.getShort(2);
    }

    public String getFrequency() {
        return Format.formatUnitValue(platform.getFrequencyMHz(), Format.HERTZ);
    }

    public int getCapacityPerPieceMB() {
        return capacityPerPieceMB;
    }

    public String getCapacityPerPiece() {
        return Format.formatUnitValue(capacityPerPieceMB, Format.BYTES);
    }

    public int getCapacityKB() {
        return capacityPerPieceMB * count;
    }

    public String getCapacity() {
        return Format.formatUnitValue(capacityPerPieceMB * count, Format.BYTES);
    }

    public short getCount() {
        return count;
    }

    public short getLatency() {
        return latency;
    }
}
