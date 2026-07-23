package com.springbeans.cafemenumanagement.order.dto.request;

import java.util.List;


public record OrderCreateRequest(

        String email,

        String address,

        String postalCode,

        List<Item> items

) {


    public record Item(

            Long productId,

            int amount

    ){}
}