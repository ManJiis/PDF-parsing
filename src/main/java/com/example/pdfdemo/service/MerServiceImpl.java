package com.example.pdfdemo.service;

import cn.hutool.extra.spring.SpringUtil;
import com.example.pdfdemo.config.TaskThreadPool;
import com.example.pdfdemo.entry.MerchantsDetail;
import com.example.pdfdemo.entry.TemplateDataModel;
import com.example.pdfdemo.util.MyBeanUtils;
import com.example.pdfdemo.util.PdfTemplateDataProperties;
import com.example.pdfdemo.util.PdfTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

import static com.example.pdfdemo.util.PdfTemplateUtils.fileUpload;

/**
 * @author TANG
 * @date 2021-02-20
 */
@Service
public class MerServiceImpl implements IMerService {

    @Autowired
    TaskThreadPool taskThreadPool;

    public MerchantsDetail findByMerId(String id) {
        MerchantsDetail list = new MerchantsDetail();
        if (id != null) {
            return MerchantsDetail.builder().
                    authorizer("李四").
                    borrower("李斯斯").
                    // 1 父母 2 兄弟姐妹 3 朋友
                            relationship("2").
                            urlAuthorizerSign("http://dev-bfp-image.oss-cn-beijing.aliyuncs.com/be43ea3360414bcab01654c0b4992d52_autograph1612507642793.png").
                            urlUsedNameSign("http://dev-bfp-image.oss-cn-beijing.aliyuncs.com/be43ea3360414bcab01654c0b4992d52_autograph1612507642793.png").
                            idCardNumber("450365598888888888").
                            otherPapers("港澳通行证").build();
        }
        return list;
    }

    @Override
    public List<String> activePdfUpload(String merId) {
        List<TemplateDataModel> list = new ArrayList<>();
        PdfTemplateDataProperties pdfTemplateDataProperties = SpringUtil.getBean(PdfTemplateDataProperties.class);
        // 模拟数据查询
        MerchantsDetail detail = this.findByMerId(merId);
        // 获取模板初始list
        List<TemplateDataModel> activeList = pdfTemplateDataProperties.getBsActiveList();
        for (TemplateDataModel model : activeList) {
            MyBeanUtils.copyPropertiesIgnoreNull(detail, model);
            model.setMerId(merId);
            list.add(model);
        }
        return PdfTemplateUtils.fileUpload(list, taskThreadPool.taskThreadPoolExecutor());
    }

    @Override
    public List<String> entryPdfUpload(String merId) {
        Set<String> set = new HashSet<>();
        List<TemplateDataModel> list = new ArrayList<>();

        PdfTemplateDataProperties pdfTemplateDataProperties = SpringUtil.getBean(PdfTemplateDataProperties.class);
        set.add(System.identityHashCode(pdfTemplateDataProperties) + "");

        // 模拟数据查询
        MerchantsDetail detail = this.findByMerId(merId);
        // 获取模板初始list
        List<TemplateDataModel> bsEntryList = pdfTemplateDataProperties.getBsEntryList();
        for (TemplateDataModel model : bsEntryList) {
            MyBeanUtils.copyPropertiesIgnoreNull(detail, model);
            model.setMerId(merId);
            list.add(model);
        }
        return PdfTemplateUtils.fileUpload(list, taskThreadPool.taskThreadPoolExecutor());
    }


    @Override
    public List<String> entryPdfUpload2(String merId, PdfTemplateDataProperties pdfTemplateDataProperties) {
        List<TemplateDataModel> list = new ArrayList<>();
        // 模拟数据查询
        MerchantsDetail detail = this.findByMerId(merId);
        // 获取模板初始list
        List<TemplateDataModel> bsEntryList = pdfTemplateDataProperties.getBsEntryList();
        for (TemplateDataModel model : bsEntryList) {
            MyBeanUtils.copyPropertiesIgnoreNull(detail, model);
            list.add(model);
        }
        return PdfTemplateUtils.fileUpload(list, taskThreadPool.taskThreadPoolExecutor());
    }

    @Override
    public void threadPoolTest() {
        ThreadPoolExecutor threadPoolExecutor = taskThreadPool.taskThreadPoolExecutor();
        System.out.println(Thread.currentThread().getName() + "- threadPoolExecutor = " + threadPoolExecutor);
    }
}
