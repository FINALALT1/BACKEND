package kr.co.moneybridge.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@ApiModel
@AllArgsConstructor
@Getter
@Setter
public class ValidDTO {
    @ApiModelProperty
    private String key;

    @ApiModelProperty
    private String value;
}
