package com.elave.selfservicesupermarketdemo1.model.dto;

import com.elave.selfservicesupermarketdemo1.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class AdminOrderDTO extends PageRequest implements Serializable {

}
