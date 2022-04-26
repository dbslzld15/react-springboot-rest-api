package com.example.gccoffee.model;

public enum OrderStatus {
    ACCEPTED, // 주문이 들어왔을때
    PAYMENT_CONFIRMED, //결제 완료
    READY_FOR_DELIVERY, //배송 가능
    SHIPPED, //배송 중
    SETTLED, //고객이 받으면
    CANCELED
}
