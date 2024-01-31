package com.chensoul.restaurant.domain.model;

import com.chensoul.auth.domain.model.SimpleUser;
import com.chensoul.commons.model.entity.Review;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewVO extends Review {
    private static final long serialVersionUID = -6854973884896957287L;
    private SimpleUser userInfo;


    private LocalDateTime createDate;

}
