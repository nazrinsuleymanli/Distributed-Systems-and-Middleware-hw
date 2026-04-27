package com.distributed.systems.wrapper;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "result")
public class ResultWrapper {
    private Double value;
    public ResultWrapper(Double value) { this.value = value; }
    @XmlValue // This puts the number inside the <result> tags
    public Double getValue() { return value; }
}
