package br.com.rodrigo.trabalho_dm111.model;

import java.io.Serializable;

public class DesiredProducts implements Serializable {

    private Long id;
    private String cpf;
    private Long saleId;
    private Long productId;
    private Double desiredPrice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Long getSaleId() {
        return saleId;
    }

    public void setSaleId(Long saleId) {
        this.saleId = saleId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Double getDesiredPrice() {
        return desiredPrice;
    }

    public void setDesiredPrice(Double desiredPrice) {
        this.desiredPrice = desiredPrice;
    }
}
