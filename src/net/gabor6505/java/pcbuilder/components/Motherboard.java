package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.utils.Format;
import net.gabor6505.java.pcbuilder.types.*;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class Motherboard extends ComponentBase {

    private final static List<Motherboard> moboList = new ArrayList<>(0);

    public final static String[] NODE_NAMES = new String[]{"form_factor", "chipset", "ram_slots", "ram_max_mb", "max_pcie_version"};

    private static IXmlComponentBuilder DATA_HANDLER = (mobo, properties) -> {
        CpuPlatform cpuPlatform = CpuPlatform.getCpuPlatform(mobo.getNode("CpuType"));

        List<RamPlatform> ramPlatforms = new ArrayList<>(0);
        for (Node ramType : mobo.getNode("RamTypes").getNodes("RamType")) {
            ramPlatforms.add(RamPlatform.getRamPlatform(ramType));
        }

        List<Connectivity> connectivities = new ArrayList<>(0);
        for (Node connectivity : mobo.getNode("Connectivities").getNodes("Connectivity")) {
            connectivities.add(new Connectivity(connectivity));
        }

        moboList.add(new Motherboard(mobo.getComponentInfo(), properties, cpuPlatform, ramPlatforms, connectivities));
    };

    public final static XmlContract CONTRACT = new XmlContract(XmlContract.Folder.COMPONENTS, "motherboards.xml", "Motherboard", NODE_NAMES, DATA_HANDLER);

    static {
        XmlParser.parseXmlComponents(CONTRACT);
    }

    private final String formFactor;
    private final CpuPlatform cpuPlatform;
    private final String cpuChipset;
    private final List<RamPlatform> ramPlatforms;
    private final short ramSlots;
    private final int ramMaxMB;
    private final short maxPcieVersion;
    private final List<Connectivity> connectivities;

    private Motherboard(NodeList componentInfoNode, ComponentProperties properties, CpuPlatform cpuPlatform, List<RamPlatform> ramPlatforms, List<Connectivity> connectivities) {
        super(componentInfoNode, CONTRACT);

        this.cpuPlatform = cpuPlatform;
        this.ramPlatforms = ramPlatforms;
        this.connectivities = connectivities;

        formFactor = properties.getString(0);
        cpuChipset = properties.getString(1);
        ramSlots = properties.getShort(2);
        ramMaxMB = properties.getInt(3);
        maxPcieVersion = properties.getShort(4);
    }

    public String getFormFactor() {
        return formFactor;
    }

    public CpuPlatform getCpuPlatform() {
        return cpuPlatform;
    }

    public Brand getCpuBrand() {
        return cpuPlatform.getBrand();
    }

    public String getCpuBrandName() {
        return cpuPlatform.getBrandName();
    }

    public String getCpuSocket() {
        return cpuPlatform.getSocket();
    }

    public String getCpuChipset() {
        return cpuChipset;
    }

    public List<RamPlatform> getRamPlatforms() {
        return ramPlatforms;
    }

    public String getRamSlots() {
        return String.valueOf(ramSlots);
    }

    public int getRamMaxMB() {
        return ramMaxMB;
    }

    public String getRamMax() {
        return Format.formatUnitValue(ramMaxMB, Format.BYTES);
    }

    public short getMaxPcieVersion() {
        return maxPcieVersion;
    }

    public List<Connectivity> getConnectivities() {
        return connectivities;
    }

    /*public List<Connectivity> getConnectivitiesByType(String type) {
        List<Connectivity> returnList = new ArrayList<>();
        for(Connectivity conn : connectivities) {
            if (conn.getType().equals(type)) returnList.add(conn);
        }
        return returnList;
    }

    public List<Connectivity> getConnectivitiesByLocation(String location) {
        List<Connectivity> returnList = new ArrayList<>();
        for(Connectivity conn : connectivities) {
            if (conn.getLocation().equals(location)) returnList.add(conn);
        }
        return returnList;
    }*/
}
