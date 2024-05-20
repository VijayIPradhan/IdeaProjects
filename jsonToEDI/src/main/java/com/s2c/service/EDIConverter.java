package com.s2c.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface EDIConverter {

    String convert(JsonNode jsonData, String agencyCode);
}
