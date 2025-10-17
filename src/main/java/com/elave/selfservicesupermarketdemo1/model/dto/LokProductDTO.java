package com.elave.selfservicesupermarketdemo1.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class LokProductDTO implements Serializable {
    private String productName;
    private String productBrand;
    private String productSn;
    private BigDecimal productPrice;
    private Integer productQuantity;
    private MultipartFile productPicFile;
}
