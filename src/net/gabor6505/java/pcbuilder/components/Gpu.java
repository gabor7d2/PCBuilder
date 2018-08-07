package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.GpuPlatform;
import net.gabor6505.java.pcbuilder.xml.ComponentProperties;
import net.gabor6505.java.pcbuilder.xml.IXmlComponentBuilder;
import net.gabor6505.java.pcbuilder.xml.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Gpu extends Component {

    public final static IXmlComponentBuilder DATA_HANDLER = (gpu, properties, list) -> {
        GpuPlatform platform = GpuPlatform.getGpuPlatform(gpu.getNode("GpuType"));

        list.add(new Gpu(gpu.getComponentInfo(), properties, platform));
    };

    private final GpuPlatform platform;

    public Gpu(NodeList componentInfoNode, ComponentProperties properties, GpuPlatform platform) {
        super(componentInfoNode, properties, "gpus.xml");

        this.platform = platform;
    }

    public GpuPlatform getPlatform() {
        return platform;
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> list = new ArrayList<>();
        if (checkValidity("base_frequency_mhz") && checkValidity("boost_frequency_mhz"))
            list.add(getDef("base_frequency_mhz") + " / " + getDef("boost_frequency_mhz"));

        if (checkValidity("vram_amount_mb") && checkValidity("vram_frequency_mhz"))
            list.add(get("vram_amount_mb") + " - " + getDef("vram_frequency_mhz"));

        addExtraInfo(list, "CUDA Cores:", "cuda_core_amount", "");
        addExtraInfo(list, "Memory Bus:", "memory_bus_width", " bit");
        addExtraInfo(list, "Output ports:", "output_port_amount", "");
        return list;
    }
}