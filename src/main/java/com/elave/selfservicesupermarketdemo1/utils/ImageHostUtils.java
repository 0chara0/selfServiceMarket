package com.elave.selfservicesupermarketdemo1.utils;

import cn.hutool.http.HttpUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "imagehost.cloudflare")
@Slf4j
public class ImageHostUtils {
    //S3访问密钥
    private String accessKeyId;
    //S3加密访问密钥
    private String secretAccessKey;
    //服务端点
    private String endPoint;
    //桶名
    private String bucketName;
    //地址前缀
    public static String PICURL = "https://pub-b8515d9381dc4bc4b9c229d937cccc69.r2.dev/";

    public AmazonS3 createS3Client(String accessKeyId, String secretAccessKey, String endPoint){
        //创建s3 client
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        //添加客户端最大连接数
        clientConfiguration.setMaxConnections(1000);
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKeyId, secretAccessKey);
        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, Regions.CN_NORTH_1.getName()))
                .withClientConfiguration(clientConfiguration)
                .withPathStyleAccessEnabled(true).build();
        return client;
    }

    /**
     * 上传文件到对象存储桶
     * @param file
     * @param fileKey
     * @return Boolean
     */
    public boolean uploadFile(File file, String fileKey){
        try{
            //获取客户端连接
            AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
            //上传文件
            PutObjectResult putObjectResult = s3Client.putObject(bucketName, fileKey, file);
        }catch (Exception e){
            log.error(String.valueOf(e));
            return false;
        }
        return true;
    }

    /**
     * 上传文件到对象存储桶-文件流方式
     * @param inputStream
     * @param fileKey
     * @return boolean
     */
    public boolean uploadFileByStream(InputStream inputStream, String fileKey){
        try{
            //获取客户端连接
            AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
            //上传文件
            s3Client.putObject(bucketName,fileKey,inputStream,new ObjectMetadata());
        }catch (Exception e){
            log.error(String.valueOf(e));
            return false;
        }
        return true;
    }

    /**
     * 下载文件
     * @param fileKey
     * @return
     */
    public InputStream downloadFile(String fileKey){
        try{
            //获取客户端连接
            AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
            //上传文件
            S3Object object = s3Client.getObject(bucketName, fileKey);
            return object.getObjectContent();
        }catch (Exception e){
            log.error(String.valueOf(e));
            return null;
        }
    }


    /**
     * 移动文件（删除原文件）
     * @param oldFileKey 原全路径
     * @param newFileKey 新全路径
     * @return boolean
     */
    public boolean mvFile(String oldFileKey,String newFileKey){
        try{
            //获取客户端连接
            AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
            //复制文件
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucketName, oldFileKey, bucketName, newFileKey);
            s3Client.copyObject(copyObjectRequest);
            //删除原文件
            s3Client.deleteObject(new DeleteObjectRequest(bucketName,oldFileKey));
        }catch (Exception e){
            log.error(String.valueOf(e));
            return false;
        }
        return true;
    }

    /**
     * 删除文件
     * @param fileKey 删除文件全路径
     * @return boolean
     */
    public boolean deleteFile(String fileKey){
        try{
            //获取客户端连接
            AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
            //删除文件
            s3Client.deleteObject(bucketName,fileKey);
        }catch (Exception e){
            log.error(String.valueOf(e));
            return false;
        }
        return true;
    }

    /**
     * 删除文件列表
     * @param fileList
     * @return
     */
    public boolean deleteFiles(String[] fileList){
        try{
            //获取客户端连接
            AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
            //删除多个文件
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(fileList);
            s3Client.deleteObjects(deleteObjectsRequest);
        }catch (Exception e){
            log.error(String.valueOf(e));
            return false;
        }
        return true;
    }

    /**
     * 查找桶内文件是否存在
     * @param fileKey
     * @return
     */
    public boolean exist(String fileKey){
        //获取客户端连接
        AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
        ListObjectsV2Result listObjectsV2Result = s3Client.listObjectsV2(bucketName, fileKey);
        return listObjectsV2Result.getKeyCount()>0;
    }

    /**
     * 根据prefix获取文件列表
     * 注意：若不进行do-while，只进行一次s3Client.listObjectsV2(req)，则最多展示1000条
     * @param prefix
     * @return
     */
    public List<S3ObjectSummary> searchPathFileByPrefix(String prefix){
        ArrayList<S3ObjectSummary> returnList = new ArrayList<>();
        try{
            //获取客户端连接
            AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
            //按prefix获取文件列表
            ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucketName);
            ListObjectsV2Result result;
            if(prefix!=null &&!prefix.isEmpty()){
                req = req.withPrefix(prefix);
            }
            do{
                result = s3Client.listObjectsV2(req);
                returnList.addAll(result.getObjectSummaries());
                String token = result.getNextContinuationToken();
                req.setContinuationToken(token);
            }while(result.isTruncated());
            return returnList;
        }catch (Exception e){
            log.error(String.valueOf(e));
            return null;
        }
    }


    /**
     * 生成文件的预签名URL
     * 注意：
     * 1.有url，无需通过密钥连接对象存储桶，只用http请求就能下载文件
     * 2.url最多7天有效，官方限制。所以必须考虑url刷新
     * @param fileKey
     * @return
     */
    public String generalUrl(String fileKey){
        try{
            //获取客户端连接
            AmazonS3 s3Client = this.createS3Client(accessKeyId,secretAccessKey,endPoint);
            //设置预签名失效时间-最多七天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_MONTH,7);
            Date date = calendar.getTime();
            //生成预签名链接
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileKey)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(date);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            return url.toString();
        }catch (Exception e){
            log.error(String.valueOf(e));
            return null;
        }
    }

    /**
     * 根据url下载文件到本地
     * @param url
     * @param filePath
     * @return
     * @throws IOException
     */
//    public File downloadFromUrl(String url,String filePath) throws IOException {
//
//
//        //获取文件名
//        String tmpUrl = StringUtils.substring(url,url.indexOf("//")+2);
//        int startPos = StringUtils.indexOf(tmpUrl,"/",tmpUrl.indexOf("/")+1)+1;
//        int endPos = StringUtils.indexOf(tmpUrl,"?");
//        tmpUrl = StringUtils.substring(tmpUrl,startPos,endPos);
//        String fileName = Paths.get(StringUtils.replace(tmpUrl,"%2F","/")).getFileName().toString();
//
//        //http请求
//        HttpClient client = HttpClients.createDefault();
//        HttpGet httpGet = new HttpGet(url);
//        HttpResponse response = client.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//
//        //输出文件
//        File file = new File(filePath + "/" + fileName);
//        if(!file.getParentFile().exists()&&!file.getParentFile().isDirectory()){
//            file.getParentFile().mkdir();
//        }
//        try(InputStream is = entity.getContent();
//            FileOutputStream fileout = new FileOutputStream(file)){
//            byte[] buffer = new byte[10 * 1024];
//            int ch = 0;
//            while((ch = is.read(buffer))!=-1){
//                fileout.write(buffer,0,ch);
//            }
//            fileout.flush();
//            return file;
//        }catch (Exception e){
//            return null;
//        }
//    }

}
