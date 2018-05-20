package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.*;
import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class Cpu extends Component {

    private final static List<Cpu> cpuList = new ArrayList<>(0);

    public final static String[] NODE_NAMES = new String[]{"generation", "unlocked", "cores", "threads", "base_frequency_mhz", "turbo_frequency_mhz", "ram_max_mb", "tdp_w", "socket"};

    private static IXmlComponentBuilder DATA_HANDLER = (cpu, properties) -> {
        List<String> cacheNodeValues = cpu.getNode("caches").getNodesContent("cache_mb");
        double[] caches = new double[cacheNodeValues.size()];
        for (int i = 0; i < caches.length; i++) {
            caches[i] = Double.parseDouble(cacheNodeValues.get(i));
        }

        Node iGpuNode = cpu.getNode("IGpu");
        IGpu iGpu = new IGpu(iGpuNode.getComponentInfo(), iGpuNode.getNodesContent(IGpu.NODE_NAMES));

        cpuList.add(new Cpu(cpu.getComponentInfo(), properties, caches, iGpu));
    };

    public final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.COMPONENTS, "cpus.xml", "Cpu", NODE_NAMES, DATA_HANDLER);

    static {
        XmlParser.parseXmlComponents(CONTRACT);
    }

    private final CpuPlatform platform;
    private final short generation;
    private final boolean unlocked;
    private final short cores;
    private final short threads;
    private final int baseFrequencyMHz;
    private final int turboFrequencyMHz;
    private final double[] cachesMB;
    private final int ramMaxMB;
    private final short tdpW;
    private final IGpu iGpu;

    public Cpu(NodeList componentInfoNode, ComponentProperties properties, double[] cachesMB, IGpu iGpu) {
        super(componentInfoNode, properties, CONTRACT);

        this.platform = CpuPlatform.getCpuPlatform(componentInfoNode.getNodeContent("brand"), properties.getString(8));
        this.cachesMB = cachesMB;
        this.iGpu = iGpu;
        generation = properties.getShort(0);
        unlocked = properties.getBoolean(1);
        cores = properties.getShort(2);
        threads = properties.getShort(3);
        baseFrequencyMHz = properties.getInt(4);
        turboFrequencyMHz = properties.getInt(5);
        ramMaxMB = properties.getInt(6);
        tdpW = properties.getShort(7);
    }

    @Override
    public List<String> getExtraInfo() {
        List<String> extraInfo = new ArrayList<>();
        extraInfo.add(get("cores") + " Cores, " + get("threads") + " Threads");
        extraInfo.add(get("base_frequency_mhz") + " / " + get("turbo_frequency_mhz"));
        return extraInfo;
    }

    public CpuPlatform getPlatform() {
        return platform;
    }

    public String getSocket() {
        return platform.getPrefix() + platform.getSocket();
    }

    public String getGeneration() {
        return Format.formatGeneration(getBrandName(), generation);
    }

    public String isUnlocked() {
        return Format.formatBoolean(unlocked);
    }

    public short getCores() {
        return cores;
    }

    public short getThreads() {
        return threads;
    }

    public int getBaseFrequencyMHz() {
        return baseFrequencyMHz;
    }

    public int getTurboFrequencyMHz() {
        return turboFrequencyMHz;
    }

    public String getBaseFrequency() {
        return Format.formatUnitValue(baseFrequencyMHz, Format.HERTZ);
    }

    public String getTurboFrequency() {
        return Format.formatUnitValue(turboFrequencyMHz, Format.HERTZ);
    }

    public double[] getCacheValues() {
        return cachesMB;
    }

    public String getCache(int index) {
        if (index >= cachesMB.length) return "";
        return Format.formatUnitValue(cachesMB[index], Format.BYTES);
    }

    public int getRamMaxMB() {
        return ramMaxMB;
    }

    public String getRamMax() {
        return Format.formatUnitValue(ramMaxMB, Format.BYTES);
    }

    public short getTdpW() {
        return tdpW;
    }

    public String getTdp() {
        return Format.formatUnitValue(tdpW, Format.WATTS);
    }

    public IGpu getIGpu() {
        return iGpu;
    }

    public Brand getIGpuBrand() {
        return iGpu.getBrand();
    }

    public String getIGpuBrandName() {
        return iGpu.getBrandName();
    }

    public String getIGpuModelNumber() {
        return iGpu.getModelNumber();
    }

    public int getIGpuBaseFrequencyKHz() {
        return iGpu.getBaseFrequencyMHz();
    }

    public int getIGpuMaxFrequencyKHz() {
        return iGpu.getMaxFrequencyMHz();
    }

    public String getIGpuBaseFrequency() {
        return Format.formatUnitValue(iGpu.getBaseFrequencyMHz(), Format.HERTZ);
    }

    public String getIGpuMaxFrequency() {
        return Format.formatUnitValue(iGpu.getMaxFrequencyMHz(), Format.HERTZ);
    }

    public short getIGpuSupportedDisplays() {
        return iGpu.getSupportedDisplays();
    }

    public static List<Cpu> getCpuList() {
        return cpuList;
    }
}
