package com.example.demo.model;

/**
 * Record entry that stores the essential pandemic information of a country.
 * @param name Name of the country
 * @param region Region of the country
 * @param caseCmltTotal Case - Cumulative Total
 * @param caseCmltNorm Case - Cumulative total normalized to per 100,000
 * @param caseReported7D Case - Reported in last 7 days
 * @param caseReported7DNorm Case - Reported in last 7 days, normalized to per 100,000
 * @param caseReported24H Case - Reported in last 24 hours
 * @param deathCmltTotal Death - Cumulative Total
 * @param deathCmltNorm Death - Cumulative total normalized to per 100,000
 * @param deathReported7D Death - Reported in last 7 days
 * @param deathReported7DNorm Death - Reported in last 7 days, normalized to per 100,000
 * @param deathReported24H Death - Reported in last 24 hours
 */
public record CountryInfoEntry(String name,
                               String region,
                               Double caseCmltTotal,
                               Double caseCmltNorm,
                               Double caseReported7D,
                               Double caseReported7DNorm,
                               Double caseReported24H,
                               Double deathCmltTotal,
                               Double deathCmltNorm,
                               Double deathReported7D,
                               Double deathReported7DNorm,
                               Double deathReported24H) {
    @Override
    public String toString() {
        return "[CountryInfoEntry]={name=" + name + ", "
                + "region=" + region + ", "
                + "caseCmltTotal=" + caseCmltTotal + ", "
                + "caseCmltNorm=" + caseCmltNorm + ", "
                + "caseReported7D=" + caseReported7D + ", "
                + "caseReported7DNorm=" + caseReported7DNorm + ", "
                + "caseReported24H=" + caseReported24H + ", "
                + "deathCmltTotal=" + deathCmltTotal + ", "
                + "deathCmltNorm=" + deathCmltNorm + ", "
                + "deathReported7D=" + deathReported7D + ", "
                + "deathReported7DNorm=" + deathReported7DNorm + ", "
                + "deathReported24H" + deathReported24H;
    }

    // Don't change getters, in case they are being called by JavaFX CellFactory.

    public String getName() {
        return name;
    }

    public String getRegion() {
        return region;
    }

    public Double getCaseCmltTotal() {
        return caseCmltTotal;
    }

    public Double getCaseCmltNorm() {
        return caseCmltNorm;
    }

    public Double getCaseReported7D() {
        return caseReported7D;
    }

    public Double getCaseReported7DNorm() {
        return caseReported7DNorm;
    }

    public Double getCaseReported24H() {
        return caseReported24H;
    }

    public Double getDeathCmltTotal() {
        return deathCmltTotal;
    }

    public Double getDeathCmltNorm() {
        return deathCmltNorm;
    }

    public Double getDeathReported7D() {
        return deathReported7D;
    }

    public Double getDeathReported7DNorm() {
        return deathReported7DNorm;
    }

    public Double getDeathReported24H() {
        return deathReported24H;
    }
}
