package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.*;
import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class Cpu extends Component {

    public final static IXmlComponentBuilder DATA_HANDLER = (cpu, properties, list) -> {
        List<String> cacheNodeValues = cpu.getNode("caches").getNodesContent("cache_mb");
        double[] caches = new double[cacheNodeValues.size()];
        for (int i = 0; i < caches.length; i++) {
            caches[i] = Double.parseDouble(cacheNodeValues.get(i));
        }

        Node iGpuNode = cpu.getNode("IGpu");
        IGpu iGpu = new IGpu(iGpuNode.getComponentInfo(), iGpuNode.getNodesContent(IGpu.NODE_NAMES));

        list.add(new Cpu(cpu.getComponentInfo(), properties, caches, iGpu));
    };

    private final CpuPlatform platform;
    private final double[] cachesMB;
    private final IGpu iGpu;

    public Cpu(NodeList componentInfoNode, ComponentProperties properties, double[] cachesMB, IGpu iGpu) {
        super(componentInfoNode, properties, "cpus.xml");

        this.platform = CpuPlatform.getCpuPlatform(componentInfoNode.getNodeContent("brand"), properties.get("socket"));
        this.cachesMB = cachesMB;
        this.iGpu = iGpu;
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> list = new ArrayList<>();
        if (checkValidity("cores") && checkValidity("threads"))
            list.add(get("cores") + " Cores, " + get("threads") + " Threads");

        addExtraInfos(list, "base_frequency_mhz", "/", "turbo_frequency_mhz");

        if (cachesMB.length > 0)
            list.add("Cache: " + getCacheValue(0));
        return list;
    }

    public CpuPlatform getPlatform() {
        return platform;
    }

    public String getSocket() {
        return platform.getPrefix() + platform.getSocket();
    }

    public double[] getCacheValues() {
        return cachesMB;
    }

    public String getCacheValue(int index) {
        if (index >= cachesMB.length) return "";
        return Format.formatUnitValue(cachesMB[index], Format.BYTES);
    }

    public IGpu getIGpu() {
        return iGpu;
    }
}
