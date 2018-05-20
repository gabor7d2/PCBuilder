package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.types.RamPlatform;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class Ram extends Component {

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
        super(componentInfoNode, properties, CONTRACT);

        this.platform = platform;
        capacityPerPieceMB = properties.getInt(0);
        count = properties.getShort(1);
        latency = properties.getShort(2);
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> extraInfo = new ArrayList<>();
        extraInfo.add(getTypeName() + " " + getFrequencyDefaultFormat());
        extraInfo.add(get("count") + " x " + get("capacity_per_piece_mb"));
        return extraInfo;
    }

    public RamPlatform getPlatform() {
        return platform;
    }

    public String getTypeName() {
        return platform.getTypeName();
    }

    public int getFrequencyMHz() {
        return platform.getFrequencyMHz();
    }

    public String getFrequency() {
        return Format.formatUnitValue(platform.getFrequencyMHz(), Format.HERTZ);
    }

    public String getFrequencyDefaultFormat() {
        return Format.formatUnitValueDefault(platform.getFrequencyMHz(), Format.HERTZ);
    }

    public int getCapacityPerPieceMB() {
        return capacityPerPieceMB;
    }

    public String getCapacityPerPiece() {
        return Format.formatUnitValue(capacityPerPieceMB, Format.BYTES);
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

    public static List<Ram> getRamList() {
        return ramList;
    }
}
