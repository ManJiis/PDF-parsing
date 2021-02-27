package com.example.pdfdemo.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.ReflectUtil;
import com.example.pdfdemo.entry.TemplateDataModel;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * @author TANG
 */
@Component
public class PdfTemplateUtils {
    private final static Logger log = LoggerFactory.getLogger(PdfTemplateUtils.class);

    private static ExecutorService PDF_EXECUTOR_SERVICE;

    private static final String SYS_OS_WINDOWS = "win";
    private static final String SYS_OS_NAME;

    static {
        SYS_OS_NAME = System.getProperty("os.name");
        log.info("SYS_OS_NAME : {}", SYS_OS_NAME);
//        PDF_EXECUTOR_SERVICE = new ThreadPoolExecutor(
//                20,
//                50,
//                10L, TimeUnit.SECONDS,
//                new LinkedBlockingQueue<Runnable>(3000),
//                new NamedThreadFactory("pdf-pool-exec-", false),
//                // 拒绝策略
//                new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 根据PDF模板填充数据并将文件上传阿里OSS
     *
     * @param modelList /
     * @return list  返回文件名集合
     */
    public static List<String> fileUpload(List<TemplateDataModel> modelList, ThreadPoolExecutor poolExecutor) {
        long beginTime = System.currentTimeMillis();
        List<String> returnList = new ArrayList<>();
        if (modelList != null && modelList.size() > 0) {
            PDF_EXECUTOR_SERVICE = poolExecutor;
            CountDownLatch downLatch = new CountDownLatch(modelList.size());
            for (TemplateDataModel dataModel : modelList) {
                log.info("TemplateDataModel: {}", dataModel);
                PDF_EXECUTOR_SERVICE.execute(() -> {
                    extracted(returnList, downLatch, dataModel);
                });
            }
            try {
                // 等待主线程
                downLatch.await();
            } catch (InterruptedException e) {
                log.error("解析PDF线程异常 : {}", e.getMessage());
            }
            long endTime = System.currentTimeMillis();
            log.info("本次执行时间 :" + (endTime - beginTime) / 1000 + "s");
            return returnList;
        }
        return returnList;
    }

    private static void extracted(List<String> returnList, CountDownLatch downLatch, TemplateDataModel dataModel) {
//        long currentTimeMillis = System.currentTimeMillis();
//        String newFileName = dataModel.getTemplateEnName() + "_" + currentTimeMillis + ".pdf";

        String newFileName = dataModel.getMerId() + dataModel.getTemplateEnName() + "_" + IdUtils.snowflakeId() + ".pdf";

        log.info("上传文件名: {}", newFileName);
        log.info("=========TemplateCode: [{}], TemplateBasePath: [{}]", dataModel.getTemplateCode(), dataModel.getTemplateBasePath());

        // eg: /D:/work-space/javaProject/demo/pdf-demo/target/classes/baosheng/pdf/entry/
        String templatePath = ClassLoader.getSystemResource(dataModel.getTemplateBasePath()).getPath();
        if (SYS_OS_NAME.toLowerCase().startsWith(SYS_OS_WINDOWS)) {
            // 读取的PDF模板全路径 eg: D:/work-space/javaProject/demo/pdf-demo/target/classes/baosheng/pdf/entry/12_个人信用信息查询与提供授权书.pdf
            templatePath = templatePath.replaceFirst("/", "") + dataModel.getTemplateName();
        } else {
            templatePath = templatePath + dataModel.getTemplateName();
        }
        log.info("===================模板全路径: {}", templatePath);

        ByteArrayOutputStream bos;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            //-----------------------------------模板内容填充 start -----------------------------------------------------//
            bos = templateProcessing(dataModel, templatePath);
            //-----------------------------------模板内容填充 start -----------------------------------------------------//
            byte[] content = bos.toByteArray();
            inputStream = new ByteArrayInputStream(content);
            if (SYS_OS_NAME.toLowerCase().startsWith(SYS_OS_WINDOWS)) {
                // ===============输出至本地 start ======================//
                File mkdir = FileUtil.mkdir("D:\\test\\");
                outputStream = new FileOutputStream(mkdir + "/" + newFileName);
                outputStream.write(content);
            } else {
                // ===============TODO 上传至阿里OSS ======================//
                // do something
                System.out.println("OSS上传成功");
            }
            String returnFileName = dataModel.getTemplateCode() + "_" + newFileName;
            log.info("返回文件名: {}", returnFileName);
            returnList.add(returnFileName);
        } catch (Exception e) {
            log.error("解析PDF异常: {}", e.getMessage());
        } finally {
            // 计数器减一
            downLatch.countDown();
            closeQuietly(inputStream);
            closeQuietly(outputStream);
        }
    }

    private static ByteArrayOutputStream templateProcessing(TemplateDataModel dataModel, String templatePath) throws DocumentException, IOException {
        PdfReader reader;
        ByteArrayOutputStream bos;
        PdfStamper stamper;
        // 给表单添加中文字体 这里采用系统字体。不设置的话，中文可能无法显示
        // BaseFont.IDENTITY_H  带有垂直书写的Unicode编码
        // BaseFont.EMBEDDED    是个boolean值，如果字体要嵌入到PDF中，则为true
        BaseFont bf;
        if (SYS_OS_NAME.toLowerCase().startsWith(SYS_OS_WINDOWS)) {
            // Windows字体目录 C:\Windows\Fonts
            bf = BaseFont.createFont("C:\\Windows\\Fonts\\simfang.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } else {
            bf = BaseFont.createFont("/usr/share/fonts/simfang.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
        // 读取pdf模板
        reader = new PdfReader(templatePath);
        bos = new ByteArrayOutputStream();
        stamper = new PdfStamper(reader, bos);
        AcroFields acroFields = stamper.getAcroFields();
        //遍历数据
        Field[] fields = ReflectUtil.getFields(TemplateDataModel.class);
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String fieldValue = (String) ReflectUtil.getFieldValue(dataModel, field);
            if (field.getName().toLowerCase().contains("url")) {
                List<AcroFields.FieldPosition> fieldPositions = acroFields.getFieldPositions(fieldName);
                for (AcroFields.FieldPosition fieldPosition : fieldPositions) {
                    int pageNo = fieldPosition.page;
                    Rectangle position = fieldPosition.position;
                    float x = position.getLeft();
                    float y = position.getBottom();
                    //根据路径读取图片
                    Image image = Image.getInstance(fieldValue);
                    //获取图片页面
                    PdfContentByte under = stamper.getOverContent(pageNo);
                    //图片大小自适应
                    image.scaleToFit(position.getWidth(), position.getHeight());
                    //添加图片
                    image.setAbsolutePosition(x, y);
                    under.addImage(image);
                }
            } else {
                acroFields.addSubstitutionFont(bf);
                acroFields.setField(fieldName, fieldValue);
            }
        }
        // 设置为false时，如果生成的PDF文件可以编辑，尝试将其设置为true
        stamper.setFormFlattening(false);
        stamper.close();
        return bos;
    }

    public static void closeQuietly(InputStream input) {
        closeQuietly((Closeable) input);
    }

    public static void closeQuietly(OutputStream output) {
        closeQuietly((Closeable) output);
    }

    /**
     * 关闭流
     *
     * @param closeable /
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            log.info("PdfTemplateUtils stream close error: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor threadP = new ThreadPoolExecutor(
                20,
                50,
                10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(3000),
                new NamedThreadFactory("pdf-pool-exec-", false),
                // 拒绝策略
                new ThreadPoolExecutor.AbortPolicy());

        List<TemplateDataModel> list = new ArrayList<>();

        TemplateDataModel model1 = new TemplateDataModel();
        model1.setAuthorizer("李xx");
        model1.setBorrower("张xx");
        model1.setRelationship("父子");
        model1.setUrlAuthorizerSign("http://dev-bfp-image.oss-cn-beijing.aliyuncs.com/be43ea3360414bcab01654c0b4992d52_autograph1612507642793.png");
        model1.setUrlUsedNameSign("http://dev-bfp-image.oss-cn-beijing.aliyuncs.com/be43ea3360414bcab01654c0b4992d52_autograph1612507642793.png");
        model1.setIdCardNumber("450365599844512235");
        model1.setOtherPapers("");
        model1.setTemplateBasePath("baosheng/pdf/active/");
        model1.setTemplateName("03_二类户开户协议.pdf");
        model1.setTemplateEnName("03_type2AccountOpeningAgreement");

        TemplateDataModel model2 = TemplateDataModel.builder()
                .authorizer("刘xx")
                .borrower("刘x")
                .relationship("母子")
                .urlAuthorizerSign("http://dev-bfp-image.oss-cn-beijing.aliyuncs.com/be43ea3360414bcab01654c0b4992d52_autograph1612507642793.png")
                .urlUsedNameSign("http://dev-bfp-image.oss-cn-beijing.aliyuncs.com/be43ea3360414bcab01654c0b4992d52_autograph1612507642793.png")
                .idCardNumber("450365599844512235")
                .otherPapers("港澳通行证")
                .templateBasePath("baosheng/pdf/active/")
                .templateName("22_移动CA中心个人数字证书申请表.pdf")
                .templateEnName("22_personalInfo").build();

        list.add(model1);
        list.add(model2);

        List<String> fileNameList = fileUpload(list, threadP);

        System.out.println("fileNameList = " + fileNameList);

        PDF_EXECUTOR_SERVICE.shutdown();
    }

}

