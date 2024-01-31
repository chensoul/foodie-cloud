package com.chensoul.commons.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * 实体对象公共属性
 */
@Getter
@Setter
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 6595513467381653081L;

	private Long id;

	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
}
