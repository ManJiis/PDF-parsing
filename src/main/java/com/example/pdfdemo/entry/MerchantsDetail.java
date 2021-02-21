package com.example.pdfdemo.entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class MerchantsDetail {

    /** 商户档案号 */
    private String merId;
    /** 授权人名字 */
    private String authorizer;
    /** 紧急联系人名字 */
    private String borrower;
    /** 关系 */
    private String relationship;
    /** 授权人签字图片 */
    private String urlAuthorizerSign;
    /** 曾用名签字图片 */
    private String urlUsedNameSign;
    /** 证件号码 */
    private String idCardNumber;
    /** 其他证件类型 */
    private String otherPapers;

    /** 模板路径  eg: baosheng/pdf/ */
    private String templateBasePath;
    /** 模板名称 **/
    private String templateName;
    /** 模板英文名 用于存储的名称 **/
    private String templateEnName;



}
