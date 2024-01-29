package com.imooc.commons.model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherOrder extends BaseEntity {

    private static final long serialVersionUID = 6131988058612260259L;
    private String orderNo;
    private Long voucherId;
    private Long userId;
    private String qrcode;
    private int payment;
    private int status;
    private int orderType;
    private int seckillId;

}
