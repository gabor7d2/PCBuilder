package net.gabor6505.java.pcbuilder.components;

import net.gabor6505.java.pcbuilder.types.Brand;

public class ComponentBase {

    private final Brand brand;
    private final String modelNumber;

    public ComponentBase(Brand brand, String modelNumber) {
        this.brand = brand;
        this.modelNumber = modelNumber;
    }

    public Brand getBrand() {
        return brand;
    }

    public String getBrandName() {
        return brand.getName();
    }

    public String getModelNumber() {
        return modelNumber;
    }
}
