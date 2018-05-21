package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.types.RamPlatform;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class Ram extends Component {

    public final static String[] NODE_NAMES = new String[]{"capacity_per_piece_mb", "count", "latency"};

    public final static IXmlComponentBuilder DATA_HANDLER = (ram, properties, list) -> {
        RamPlatform ramPlatform = RamPlatform.getRamPlatform(ram.getNode("RamType"));

        list.add(new Ram(ram.getComponentInfo(), properties, ramPlatform));
    };

    private final RamPlatform platform;

    public Ram(NodeList componentInfoNode, ComponentProperties properties, RamPlatform platform) {
        super(componentInfoNode, properties, "rams.xml");
        this.platform = platform;
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> extraInfo = new ArrayList<>();
        extraInfo.add(getTypeName() + " " + getFrequency());
        extraInfo.add(get("count") + " x " + get("capacity_per_piece_mb") + " (" + getCapacity() + ")");
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
        return Format.formatUnitValueDefault(platform.getFrequencyMHz(), Format.HERTZ);
    }

    public String getCapacity() {
        return Format.formatUnitValue(getInt("capacity_per_piece_mb") * getInt("count"), Format.BYTES);
    }
}
