package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.types.RamPlatform;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class Ram extends Component {

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
        List<String> list = new ArrayList<>();
        if (platform != null)
            list.add(getTypeName() + " - " + getFrequency());

        if (checkValidity("count") && checkValidity("capacity_per_piece_mb"))
            list.add(get("count") + " x " + get("capacity_per_piece_mb") + " (" + getCapacity() + ")");

        addExtraInfo(list, "Latency: CL", "latency", "");
        return list;
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
