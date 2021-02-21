package com.example.pdfdemo.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 模板数据
 *
 * @author TANG
 * @date 2021-02-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateDataModel {

    /**
     * 商户档案号
     */
    private String merId;
    /**
     * 授权人名字
     */
    private String authorizer;
    /**
     * 紧急联系人名字
     */
    private String borrower;
    /**
     * 关系
     */
    private String relationship;
    /**
     * 授权人签字图片
     */
    private String urlAuthorizerSign;
    /**
     * 曾用名签字图片
     */
    private String urlUsedNameSign;
    /**
     * 证件号码
     */
    private String idCardNumber;
    /**
     * 其他证件类型
     */
    private String otherPapers;

    private static String year = "" + LocalDate.now().getYear();
    private static String month = "" + LocalDateTime.now().getMonthValue();
    private static String day = "" + LocalDate.now().getDayOfMonth();

    /**
     * 模板路径  eg: baosheng/pdf/active/
     */
    private String templateBasePath;
    /**
     * 模板名称
     **/
    private String templateName;
    /**
     * 模板英文名 用于存储的名称
     **/
    private String templateEnName;

    public void setRelationship(String relationship) {
        this.relationship = "1".equals(relationship) ? "父母" : "2".equals(relationship) ? "兄弟姐妹" : "朋友";
    }
}
