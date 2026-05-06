package com.example.inventoryAuth.Entity;

public enum Supplier {

    SURASAVI_SUPPLIERS("Surasavi Suppliers"),
    GUNASEKARA_SUPPLIERS("Gunasekara Suppliers"),
    ATLAS_SUPPLIERS("Atlas Suppliers"),
    ALPHA_STATIONERIES("Alpha Stationeries"),
    INNOVATION_TECH("Innovation Tech");

    private final String displayName;

    Supplier(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
