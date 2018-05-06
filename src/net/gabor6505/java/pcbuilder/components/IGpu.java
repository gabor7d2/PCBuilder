package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.Brand;
import net.gabor6505.java.pcbuilder.xml.ComponentProperties;

class IGpu extends ComponentBase {

    public final static String[] NODE_NAMES = new String[]{"base_frequency_mhz", "max_frequency_mhz", "supported_displays"};

    private final int baseFrequencyMHz;
    private final int maxFrequencyMHz;
    private final short supportedDisplays;

    IGpu(Brand brand, String modelNumber, ComponentProperties properties) {
        super(brand, modelNumber);

        baseFrequencyMHz = properties.getInt(0);
        maxFrequencyMHz = properties.getInt(1);
        supportedDisplays = properties.getShort(2);
    }

    int getBaseFrequencyMHz() {
        return baseFrequencyMHz;
    }

    int getMaxFrequencyMHz() {
        return maxFrequencyMHz;
    }

    short getSupportedDisplays() {
        return supportedDisplays;
    }
}
