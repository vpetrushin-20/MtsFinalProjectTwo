package api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeleteOrderRequest {
    private Integer userId;
    private String orderId;
}