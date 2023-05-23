package com.dayone.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Company {
    private String name;
    private String ticker;
}
