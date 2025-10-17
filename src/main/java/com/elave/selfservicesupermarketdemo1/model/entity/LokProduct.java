package com.elave.selfservicesupermarketdemo1.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 
 * @TableName lok_product
 */
@TableName(value ="lok_product")
@Data
public class LokProduct implements Serializable {
    /**
     * 
     */
    @TableId
    private Long id;

    /**
     * 商品图片
     */
    private String productPic;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品品牌
     */
    private String productBrand;

    /**
     * 商品条码
     */
    private String productSn;

    /**
     * 销售价格
     */
    private BigDecimal productPrice;

    /**
     * 剩余数量
     */
    private Integer productQuantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        LokProduct other = (LokProduct) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProductPic() == null ? other.getProductPic() == null : this.getProductPic().equals(other.getProductPic()))
            && (this.getProductName() == null ? other.getProductName() == null : this.getProductName().equals(other.getProductName()))
            && (this.getProductBrand() == null ? other.getProductBrand() == null : this.getProductBrand().equals(other.getProductBrand()))
            && (this.getProductSn() == null ? other.getProductSn() == null : this.getProductSn().equals(other.getProductSn()))
            && (this.getProductPrice() == null ? other.getProductPrice() == null : this.getProductPrice().equals(other.getProductPrice()))
            && (this.getProductQuantity() == null ? other.getProductQuantity() == null : this.getProductQuantity().equals(other.getProductQuantity()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProductPic() == null) ? 0 : getProductPic().hashCode());
        result = prime * result + ((getProductName() == null) ? 0 : getProductName().hashCode());
        result = prime * result + ((getProductBrand() == null) ? 0 : getProductBrand().hashCode());
        result = prime * result + ((getProductSn() == null) ? 0 : getProductSn().hashCode());
        result = prime * result + ((getProductPrice() == null) ? 0 : getProductPrice().hashCode());
        result = prime * result + ((getProductQuantity() == null) ? 0 : getProductQuantity().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", productPic=").append(productPic);
        sb.append(", productName=").append(productName);
        sb.append(", productBrand=").append(productBrand);
        sb.append(", productSn=").append(productSn);
        sb.append(", productPrice=").append(productPrice);
        sb.append(", productQuantity=").append(productQuantity);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}