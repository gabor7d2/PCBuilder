package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.Brand;
import net.gabor6505.java.pcbuilder.utils.Format;

public class Gpu extends ComponentBase {

    private final int baseFrequencyMHz;
    private final int turboFrequencyMHz;

    public Gpu(Brand brand, String modelNumber, int baseFrequencyMHz, int turboFrequencyMHz) {
        super(brand, modelNumber);
        this.baseFrequencyMHz = baseFrequencyMHz;
        this.turboFrequencyMHz = turboFrequencyMHz;
    }

    public int getBaseFrequencyMHz() {
        return baseFrequencyMHz;
    }

    public String getBaseFrequency() {
        return Format.formatUnitValue(baseFrequencyMHz, Format.HERTZ);
    }

    public int getTurboFrequencyMHz() {
        return turboFrequencyMHz;
    }

    public String getTurboFrequency() {
        return Format.formatUnitValue(turboFrequencyMHz, Format.HERTZ);
    }
}
