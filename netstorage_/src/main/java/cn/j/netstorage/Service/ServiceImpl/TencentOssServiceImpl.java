package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Entity.oss.Oss;
import cn.j.netstorage.Entity.oss.Oss;
import cn.j.netstorage.Entity.oss.OssFiles;
import cn.j.netstorage.Mapper.OssMapper;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.OssService;
import cn.j.netstorage.tool.EncryUtil;
import cn.j.netstorage.tool.FilesUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TencentOssServiceImpl implements OssService {

    @Autowired
    private OssMapper mapper;

    @Autowired
    private FilesService filesService;

    private COSClient getCosClient(Oss oss) {
        if (oss==null)return null;
        System.out.println("oss:"+oss);

        String secretId = oss.getSecretId();
        String secretKey = oss.getSecretKey();
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        Region region = new Region(oss.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);

        return new COSClient(cred, clientConfig);
    }


    @Override
    public List<OssFiles> get(Oss oss, String path, String prefix) {
        // Bucket的命名格式为 BucketName-APPID ，此处填写的存储桶名称必须为此格式

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        // 设置bucket名称
        listObjectsRequest.setBucketName(path);
        // prefix表示列出的object的key以prefix开始
        listObjectsRequest.setPrefix(prefix);
        // deliter表示分隔符, 设置为/表示列出当前目录下的object, 设置为空表示列出所有的object
        listObjectsRequest.setDelimiter("/");

        listObjectsRequest.setMaxKeys(1000);

        ObjectListing objectListing = null;
        if (oss==null) return null;

        COSClient cosClient = getCosClient(oss.decrypt());

        List<OssFiles> files = new ArrayList<>();
        do {
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosClientException e) {
                e.printStackTrace();
            } finally {
                if (cosClient!=null)
                    shutdown(cosClient);
            }
            // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
            List<String> commonPrefixs = objectListing.getCommonPrefixes();
            commonPrefixs.forEach(value -> {
                files.add(new OssFiles(value));
            });
            // object summary表示所有列出的object列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                Date lastModified = cosObjectSummary.getLastModified();
                // 文件的路径key
                String key = cosObjectSummary.getKey();
                // 文件的etag
                String etag = cosObjectSummary.getETag();
                // 文件的长度
                long fileSize = cosObjectSummary.getSize();
                // 文件的存储类型
                String storageClasses = cosObjectSummary.getStorageClass();
                files.add(new OssFiles(key, fileSize, lastModified, storageClasses));
            }
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());
        return files;
    }

    @Override
    public List<OssFiles> get(User user, String name, String prefix) {
        Oss oss=get(user);
        if (oss==null)return null;
        return get(oss,name,prefix);
    }

    @Override
    public boolean upload(User user, String path, Files files) {
        File localFile=null;

        if (files.getOriginFile().iterator().hasNext())
            localFile = new File(files.getOriginFile().iterator().next().getPath());
        else return false;
        COSClient cosClient = null;
        Oss oss = get(user).decrypt();
        if (oss==null)return false;
        cosClient = getCosClient(oss);
        if (cosClient==null)return false;
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(path, FilesUtil.append(files.getParentName(),files.getSelfName()), localFile);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        } catch (CosClientException e) {
            e.printStackTrace();
            return false;
        } finally {
            shutdown(cosClient);
        }
        return true;
    }

    @Override
    public boolean upload(User user, String path, Long id) {
        Files files=filesService.findByFid(id);
        if (files==null)return false;
        if (files.getUser()!=user)return false;
        return upload(user,path,files);
    }

    @Override
    public boolean download(User user, String path, OriginFile originFile) {
        Oss oss = get(user).decrypt();
        if (oss==null)return false;
        COSClient cosClient = getCosClient(oss);
        if (cosClient==null)return false;
        String outputFilePath = originFile.getPath();
        File downFile = new File(outputFilePath);
        GetObjectRequest getObjectRequest = new GetObjectRequest(path, originFile.getOssKey());
        ObjectMetadata downObjectMeta = cosClient.getObject(getObjectRequest, downFile);
        shutdown(cosClient);
        return false;
    }

    @Override
    public boolean delete(User user, String path, OriginFile originFile) {
        return false;
    }

    private void shutdown(COSClient cosClient) {
        cosClient.shutdown();
    }

    public boolean add(Oss oss, User user) {
        oss.setUser(user);
        return mapper.save(oss.encrypt()).getId() != 0;
    }

    public List<OssFiles> getAllBucket(User user){
        Oss oss = get(user).decrypt();
        if (oss==null)return null;
        List<Bucket> buckets = this.getCosClient(oss).listBuckets();
        List<OssFiles> files=new ArrayList<>();
        for (Bucket bucketElement : buckets) {
            String bucketName = bucketElement.getName();
            String bucketLocation = bucketElement.getLocation();
            files.add(new OssFiles(bucketName));
        }
        return files;
    }

    @Override
    public boolean backup(User user, String bucketName, Files files) {
        return false;
    }

    @Override
    public boolean createBucket(User user, String bucketName) {
        return false;
    }

    @Override
    public Oss get(User user) {
        return mapper.findByUser(user);
    }
}
