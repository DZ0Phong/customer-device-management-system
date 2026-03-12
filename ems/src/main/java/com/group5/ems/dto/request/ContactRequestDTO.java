package com.group5.ems.dto.request;

import lombok.Data;

@Data
public class ContactRequestDTO {

    private String senderName;
    private String senderEmail;
    private String senderPhone;
    private String topic;
    private String message;

}