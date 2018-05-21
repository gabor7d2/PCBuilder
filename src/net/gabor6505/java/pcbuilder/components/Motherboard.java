package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.frames.MainFrame;
import net.gabor6505.java.pcbuilder.types.*;
import net.gabor6505.java.pcbuilder.xml.*;

import java.util.ArrayList;
import java.util.List;

public class Motherboard extends Component {

    // TODO put all classes' node names into xml file
    public final static String[] NODE_NAMES = new String[]{"form_factor", "chipset", "ram_slots", "ram_max_mb", "max_pcie_version"};

    public final static IXmlComponentBuilder DATA_HANDLER = (mobo, properties, list) -> {
        CpuPlatform cpuPlatform = CpuPlatform.getCpuPlatform(mobo.getNode("CpuType"));

        List<RamPlatform> ramPlatforms = new ArrayList<>(0);
        for (Node ramType : mobo.getNode("RamTypes").getNodes("RamType")) {
            ramPlatforms.add(RamPlatform.getRamPlatform(ramType));
        }

        List<Connectivity> connectivities = new ArrayList<>(0);
        for (Node connectivity : mobo.getNode("Connectivities").getNodes("Connectivity")) {
            connectivities.add(new Connectivity(connectivity));
        }

        list.add(new Motherboard(mobo.getComponentInfo(), properties, cpuPlatform, ramPlatforms, connectivities));
    };

    private final String formFactor;
    private final CpuPlatform cpuPlatform;
    private final List<RamPlatform> ramPlatforms;
    private final List<Connectivity> connectivities;

    private Motherboard(NodeList componentInfoNode, ComponentProperties properties, CpuPlatform cpuPlatform, List<RamPlatform> ramPlatforms, List<Connectivity> connectivities) {
        super(componentInfoNode, properties, "motherboards.xml");

        this.cpuPlatform = cpuPlatform;
        this.ramPlatforms = ramPlatforms;
        this.connectivities = connectivities;

        formFactor = MainFrame.getFormFactor(properties.get("form_factor"));
    }

    public String getFormFactor() {
        return formFactor;
    }

    public CpuPlatform getCpuPlatform() {
        return cpuPlatform;
    }

    public String getCpuSocket() {
        return cpuPlatform.getPrefix() + cpuPlatform.getSocket();
    }

    public List<RamPlatform> getRamPlatforms() {
        return ramPlatforms;
    }

    public List<Connectivity> getConnectivities() {
        return connectivities;
    }
}
