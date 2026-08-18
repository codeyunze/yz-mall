package com.yz.mall.sys.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 第三方客户端公钥上传数据模型类
 *
 * @author yunze
 */
@Data
public class SysOpenClientKeyUploadDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户端标识
     */
    @NotBlank(message = "clientId不能为空")
    private String clientId;

    /**
     * 客户端SM2公钥Base64/PEM
     */
    @NotBlank(message = "客户端公钥不能为空")
    @Length(max = 4096, message = "公钥内容过长")
    private String clientPublicKey;

    /**
     * 备注
     */
    @Length(max = 255, message = "备注不能超过255个字符")
    private String remark;
}
