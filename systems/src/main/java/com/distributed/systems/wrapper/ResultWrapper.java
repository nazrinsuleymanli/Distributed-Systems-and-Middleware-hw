package com.distributed.systems.wrapper;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JacksonXmlRootElement(localName = "result")
public class ResultWrapper {
    private Double value;

    public ResultWrapper() {} // ← required by JAXB
    public ResultWrapper(Double value) { this.value = value; }

    @JacksonXmlText
    public Double getValue() { return value; }

    public void setValue(Double value) { this.value = value; } // ← also good practice
}