package org.example.inminute_demo.chat.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GPTRequest {
    private String model;
    private List<GPTMessage> messages;
    private int n;

    public GPTRequest(String model, String prompt) {
        this.model = model;
        this.messages = new ArrayList<GPTMessage>();
        this.messages.add(new GPTMessage("user", prompt));
        this.n = 1;
    }
}
