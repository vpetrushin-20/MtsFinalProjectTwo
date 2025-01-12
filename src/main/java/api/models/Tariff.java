package api.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Tariff {

    private Integer id;
    public String type;
    public String interestRate;
}