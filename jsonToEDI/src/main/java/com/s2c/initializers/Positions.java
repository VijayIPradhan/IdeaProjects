package com.s2c.initializers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Positions {
    // Map<String,<Map<List<String>,Map<String,String>>>
    public Map<List<String>, Map<String, String>> getFDA() {
        List<String> segmentOrder = Arrays.asList("PE10","PE15","PG01", "PG02", "PG04", "PG06", "PG07", "PG10", "PG13", "PG14", "PG19", "PG20", "PG21", "PG23", "PG24", "PG25", "PG26", "PG27", "PG28", "PG30", "PG60", "PG00","PE90");
        Map<String, String> POSITION = new HashMap<>();
        POSITION.put("actionCode","PE10/1/5/5/L/N");
        POSITION.put("filingType","PE10/1/6/6/L/N");
        POSITION.put("referenceQualifierCode","PE10/3/7/9/L/N");
        POSITION.put("issuerCodeForReferenceIdentifier","PE10/4/10/13/L/N");
        POSITION.put("referenceIdentifierNo","PE10/36/14/49/L/N");
        POSITION.put("filerDefinedReferenceNo","PE10/9/50/58/L/N");
        POSITION.put("billTypeIndicator","PE10/1/60/60/L/N");
        POSITION.put("carrier","PE10/4/61/64/L/N");
        POSITION.put("entryType","PE10/2/65/66/L/N");
        POSITION.put("modeOfTransportation","PE10/2/67/68/L/N");
        POSITION.put("envelopeNumber","PE10/12/69/80/L/N");
        POSITION.put("billTypeIndicatorPE15","PE15/1/5/5/L/I");
        POSITION.put("issuerCodeOfBillOfLadingNumber","PE15/4/6/9/L/I");
        POSITION.put("billOfLadingNumber","PE15/50/10/59/L/I");
        POSITION.put("priorNoticeConfirmationNumber","PE15/21/60/80/L/I");
        POSITION.put("narrativeMessageTypeCode","PE90/2/5/6/R/I");

        POSITION.put("narrativeMessageIdentifier","PE90/3/7/9/L/I");
        POSITION.put("narrativeMessage","PE90/40/10/49/L/I");
        POSITION.put("outputEnvelopNumber","PE90/20/50/69/L/I");
        POSITION.put("pgaLineNumber", "PG01/3/5/7/L/N");
        POSITION.put("governmentAgencyCode", "PG01/3/8/10/L/N");
        POSITION.put("governmentAgencyProgramCode", "PG01/3/11/13/L/N");
        POSITION.put("governmentAgencyProcessingCode", "PG01/3/14/16/L/N");
        POSITION.put("intendedUseCode", "PG01/16/42/57/L/N");
        POSITION.put("intendedUseDescription", "PG01/21/58/78/L/N");
        POSITION.put("correctionIndicator", "PG01/1/79/79/L/N");
        POSITION.put("disclaimer", "PG01/1/80/80/L/N");
        POSITION.put("itemType", "PG02/1/5/5/L/N");
        POSITION.put("productCodeQualifier", "PG02/4/6/9/L/N");
        POSITION.put("sourceCodeType", "PG06/3/5/7/L/I");
        POSITION.put("productCodeNumber", "PG02/19/10/28/L/N");
        POSITION.put("constituentActiveIngredientQualifier", "PG04/1/5/5/L/I");
        POSITION.put("constituentElementName", "PG04/51/6/56/L/I");
        POSITION.put("constituentElementQuantity", "PG04/12/57/68/R/I");
        POSITION.put("constituentElementUnitOfMeasure", "PG04/5/69/73/L/I");
        POSITION.put("percentOfConstituentElement", "PG04/7/74/80/R/I");
        POSITION.put("sourceTypeCode", "PG06/3/5/7/L/I");
        POSITION.put("countryCode", "PG06/2/8/9/L/I");
        POSITION.put("tradeOrBrandName", "PG07/35/5/39/L/N");
        POSITION.put("commodityDesc", "PG10/57/24/80/L/N");
        POSITION.put("issuerOfLPCO", "PG13/35/5/39/L/N");
        POSITION.put("governmentGeographicCodeQualifier", "PG13/3/40/42/L/N");
        POSITION.put("locationOfIssuerOfTheLPCO", "PG13/3/43/45/L/N");
        POSITION.put("issuingAgencyLocation", "PG13/25/46/70/L/N");
        POSITION.put("transactionType", "PG14/1/5/5/L/N");
        POSITION.put("lpcoOrCodeType", "PG14/3/6/8/L/N");
        POSITION.put("lpcoOrPncNumber", "PG14/33/9/41/L/N");
        POSITION.put("partyType", "PG19/3/5/7/L/G");
        POSITION.put("partyIdentifierType", "PG19/3/8/10/L/G");
        POSITION.put("partyIdentifierNumber", "PG19/15/11/25/L/G");
        POSITION.put("partyName", "PG19/32/26/57/L/G");
        POSITION.put("address1", "PG19/23/58/80/L/G");
        POSITION.put("address2", "PG20/32/5/36/L/G");
        POSITION.put("apartmentOrSuiteNo", "PG20/5/37/41/L/G");
        POSITION.put("city", "PG20/21/42/62/L/G");
        POSITION.put("stateOrProvince", "PG20/3/63/65/L/G");
        POSITION.put("country", "PG20/2/66/67/L/G");
        POSITION.put("postalCode", "PG20/9/68/76/L/G");
        POSITION.put("individualQualifier", "PG21/3/5/7/L/G");
        POSITION.put("contactPerson", "PG21/23/8/30/L/G");
        POSITION.put("telephoneNumber", "PG21/15/31/45/L/G");
        POSITION.put("email", "PG21/35/46/80/L/G");
        POSITION.put("affirmationComplianceCode", "PG23/5/5/9/L/I");
        POSITION.put("affirmationComplianceQualifier", "PG23/30/10/39/L/I");
        POSITION.put("remarksTypeCode", "PG24/3/5/7/L/N");
        POSITION.put("remarksText", "PG24/68/13/80/L/N");
        POSITION.put("temperatureQualifier", "PG25/1/5/5/L/I");
        POSITION.put("degreeType","PG25/1/6/6/L/I");
        POSITION.put("negativeNumber","PG25/1/7/7/L/I");
        POSITION.put("actualTemperature","PG25/6/8/13/R/I");
        POSITION.put("locationOfTemperatureRecording","PG25/1/14/14/L/I");
        POSITION.put("lotNumberQualifier", "PG25/1/15/15/L/I");
        POSITION.put("lotNumber", "PG25/25/16/40/L/I");
        POSITION.put("productionStartDate","PG25/8/41/48/L/I");
        POSITION.put("productionEndDate","PG25/8/49/56/L/I");
        POSITION.put("pgaLineValue", "PG25/12/57/68/R/I");
        POSITION.put("packagingQualifier", "PG26/1/5/5/R/I");
        POSITION.put("quantity", "PG26/12/6/17/R/I");
        POSITION.put("uom", "PG26/5/18/22/L/I");
        POSITION.put("containerNumberOne", "PG27/20/5/24/L/I");
        POSITION.put("containerNumberTwo", "PG27/20/28/47/L/I");
        POSITION.put("containerNumberThree", "PG27/20/51/70/L/I");
        POSITION.put("containerDimensionsOne", "PG28/4/5/8/L/N");
        POSITION.put("containerDimensionsTwo", "PG28/4/9/12/L/N");
        POSITION.put("containerDimensionsThree", "PG28/4/13/16/L/N");
        POSITION.put("packageTrackingNumberCode", "PG28/4/17/20/L/N");
        POSITION.put("packageTrackingNumber", "PG28/50/21/70/L/N");
        POSITION.put("anticipatedArrivalInformation", "PG30/1/5/5/L/N");
        POSITION.put("arrivalDate", "PG30/8/6/13/L/N");
        POSITION.put("arrivalTime", "PG30/4/14/17/L/N");
        POSITION.put("anticipatedArrivalLocationCode", "PG30/4/18/21/L/N");
        POSITION.put("arrivalLocation", "PG30/50/22/71/L/N"); //refactor to anticipatedArrivalLocation
        POSITION.put("additionalInformationQualifierCode", "PG60/3/5/7/L/I");
        POSITION.put("additionalInformation", "PG60/73/8/80/L/I");
        POSITION.put("substitutionIndicator", "PG00/1/5/5/L/N");
        POSITION.put("substitutionNumber", "PG00/4/6/9/L/N");


        Map<List<String>, Map<String, String>> fdaMap = new HashMap<>();
        fdaMap.put(segmentOrder, POSITION);
        return fdaMap;
    }
}