package com.imooc.commons.model.pojo;

import com.imooc.commons.model.base.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VoucherOrders extends BaseModel {

	private static final long serialVersionUID = 6131988058612260259L;
	private String orderNo;
	private Integer fkVoucherId;
	private Integer fkDinerId;
	private String qrcode;
	private int payment;
	private int status;
	private int orderType;
	private int fkSeckillId;

}
