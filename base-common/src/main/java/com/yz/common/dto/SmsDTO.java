package com.yz.common.dto;

import java.util.List;


/**
 * @ClassName BusinessException
 * @Description 前端给后端传递的短信发送数据
 * @Author yunze
 * @Date 2022/11/13 23:23
 * @Version 1.0
 */
public class SmsDTO {

    /**
     * 短信接收电话号码
     */
    private String phone;

    /**
     * 短信模版ID
     */
    private String templateId;

    /**
     * 模版的参数集，参数顺序与模版参数对应
     */
    private List<String> templateParamSet;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List<String> getTemplateParamSet() {
        return templateParamSet;
    }

    public void setTemplateParamSet(List<String> templateParamSet) {
        this.templateParamSet = templateParamSet;
    }

    @Override
    public String toString() {
        return "SmsDTO{" +
                "phone='" + phone + '\'' +
                ", templateId='" + templateId + '\'' +
                ", templateParamSet=" + templateParamSet +
                '}';
    }
}
