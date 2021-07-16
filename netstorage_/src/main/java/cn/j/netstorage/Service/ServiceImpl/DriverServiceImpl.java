package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.Driver.Driver;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Mapper.DriverMapper;
import cn.j.netstorage.Service.DriverService;
import cn.j.netstorage.Service.FileService2;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.PutObjectRequest;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.AnonymousCOSCredentials;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DriverServiceImpl implements DriverService {

    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private FileService2 fileService2;

    @Override
    public boolean putDriver(Driver driver) {
        if (driver == null) return false;
        URL url = null;
        switch (driver.getType()) {
            case Driver.Ali:
                url = getDriverObjectUrl(driver, "", "", driver.getUser());
                driver.setMapper(url.toString());
                driverMapper.save(driver);
                break;
            case Driver.Cos:
                url = getDriverObjectUrl(driver, "", "", driver.getUser());
                driver.setMapper(url.toString());
                driverMapper.save(driver);
                break;
            case Driver.OneDrive:
                break;
        }
        return false;
    }

    @Override
    public boolean upload(Driver driver, User user, long file_id) {
        List<Files> files = fileService2.files(file_id);
        if (files != null && files.size() >= 1)
            return upload(driver, user, files.get(0));
        return false;
    }

    @Override
    public boolean upload(Driver driver, User user, Files files) {
        switch (driver.getType()) {
            case Driver.Ali:
                return uploadOss(driver, files);
            case Driver.Cos:
                return uploadCos(driver, files);
            case Driver.OneDrive:
                break;
        }
        return false;
    }

    public boolean upload(Driver driver, User user, OriginFile originFile) {
        return false;
    }

    private boolean uploadCos(Driver driver, Files files) {
        OriginFile originFile = files.getOriginFile();
        if (originFile == null) return false;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    COSClient cosClient = getCosClient(driver);
                    File localFile = new File(originFile.getPath());
                    PutObjectResult putObjectResult = cosClient.putObject(driver.getBucketName(), files.getParentName() + files.getSelfName(), localFile);
                    cosClient.shutdown();
                } catch (CosClientException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        return true;
    }

    private boolean uploadOss(Driver driver, Files files) {
        try {
            OSS ossclient = getOssClient(driver);
            File file = new File(files.getOriginFile().getPath());
            if (!file.exists()) return false;
            PutObjectRequest putObjectRequest = new PutObjectRequest(driver.getBucketName(), files.getFullName(), file);
            ossclient.putObject(putObjectRequest);
            ossclient.shutdown();
            return true;
        } catch (OSSException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void uploadOneDrive() {

    }


    @Override
    public List<String> getDriver(User user) {
        List<String> list = new ArrayList<>();
        List<Driver> drivers = driverMapper.findAllByUser(user);
        for (Driver driver : drivers) {
            list.add(driver.getBucketName());
        }
        return list;
    }

    @Override
    public List<Driver> getAllDriver() {
        return driverMapper.findAll();
    }

    @Override
    public Driver getDriver(String bucketName, User user) {
        return driverMapper.getByBucketNameAndUser(bucketName, user);
    }

    @Override
    public boolean test(Driver driver) {
        return false;
    }

    @Override
    public boolean delete(String bucketName, String[] key, User user) {
        Driver driver = getDriver(bucketName, user);
        switch (driver.getType()) {
            case Driver.Ali:
                return false;
            case Driver.Cos:
                return deleteCos(driver, key);
            case Driver.OneDrive:
                break;
        }
        return false;
    }

    private boolean deleteOss(Driver driver, String key) {
        try {
            OSS ossClient = getOssClient(driver);
            // 删除文件。
            ossClient.deleteObject(driver.getBucketName(), key);
            ossClient.shutdown();
            return true;
        } catch (OSSException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean deleteCos(Driver driver, String[] key) {
        try {
            COSClient cosClient = getCosClient(driver);
            for (int i = 0; i < key.length; i++) {
                cosClient.deleteObject(driver.getBucketName(), key[i]);
            }
            Shutdown(cosClient);
            return true;
        } catch (CosClientException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public URL getDriverObjectUrl(Driver driver, String parentName, String selfName, User user) {
        switch (driver.getType()) {
            case Driver.Ali:
                getOssUrl(driver, parentName + selfName, user);
                break;
            case Driver.Cos:
                return getCosUrl(driver, parentName + selfName, user);
            case Driver.OneDrive:
                break;
        }
        return null;
    }

    private URL getOssUrl(Driver driver, String key, User user) {
        OSS ossClient = getOssClient(driver);
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
        return ossClient.generatePresignedUrl(driver.getBucketName(), key, expiration);
    }

    private URL getCosUrl(Driver driver, String key, User user) {
        COSCredentials cred = new AnonymousCOSCredentials();
        ClientConfig clientConfig = new ClientConfig(new Region(driver.getRegion()));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosclient = new COSClient(cred, clientConfig);
        URL url = cosclient.getObjectUrl(driver.getBucketName(), key);
        cosclient.shutdown();
        return url;
    }

    public List<FilesDTO> Driver(String bucketName, User user, String path) {
        Driver driver = driverMapper.getByBucketNameAndUser(bucketName, user);
        if (driver == null) return new ArrayList<>();
        switch (driver.getType()) {
            case Driver.Ali:
                return getAliFileList(driver, path);
            case Driver.Cos:
                return getCosFileList(driver, path);
            case Driver.OneDrive:
                return getOneDriveFileList(driver, path);
        }
        return new ArrayList<>();
    }

    @Override
    public boolean moveFiles(Driver driver, String key) {
        return false;
    }

    @Override
    public boolean reName(Driver driver, String key) {
        return false;
    }

    private List<FilesDTO> getAliFileList(Driver driver, String path) {

        OSS ossClient = getOssClient(driver);
        List<FilesDTO> fileList = new ArrayList<>();
        boolean result = ossClient.doesBucketExist(driver.getBucketName());
        if (!result) {
            return new ArrayList<>();
        }
        com.aliyun.oss.model.ObjectListing objectListing = ossClient.listObjects(driver.getBucketName());
        objectListing.setPrefix(path);
        objectListing.setDelimiter("/");

        List<String> commonPrefixs = objectListing.getCommonPrefixes();
        for (String commonPrefix : commonPrefixs) {
            File file = new File(commonPrefix);
            String parent = file.getParent();
            if (parent == null)
                parent = "/";
            fileList.add(new FilesDTO(commonPrefix, file.getName(), parent, (short) -1, 0L, Type.Folder.getType(), null));
        }
        for (OSSObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            String etag = objectSummary.getETag();
            long fileSize = objectSummary.getSize();
            String storageClasses = objectSummary.getStorageClass();
            Date lastModified = objectSummary.getLastModified();
            File file = new File(key);
            fileList.add(new FilesDTO(key, file.getName(), file.getParent(), (short) -1, fileSize, Type.getInstance(file.getName()).getType(), lastModified));
        }
        ossClient.shutdown();
        return fileList;
    }

    private List<FilesDTO> getCosFileList(Driver driver, String path) {
        List<FilesDTO> fileList = new ArrayList<>();
        COSClient cosClient = null;
        ListObjectsRequest listObjectsRequest = null;
        ObjectListing objectListing = null;
        try {
            cosClient = getCosClient(driver);
            boolean bucketExistFlag = cosClient.doesBucketExist(driver.getBucketName());
            if (!bucketExistFlag) return new ArrayList<>();

            listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(driver.getBucketName());
            listObjectsRequest.setPrefix(path);
            listObjectsRequest.setDelimiter("/");
            listObjectsRequest.setMaxKeys(1000);
            objectListing = null;
        } catch (Exception e) {
            e.printStackTrace();
            return fileList;
        }
        do {
            try {
                objectListing = cosClient.listObjects(listObjectsRequest);
            } catch (CosClientException e) {
                e.printStackTrace();
                return new ArrayList<>();
            } finally {
                Shutdown(cosClient);
            }

            List<String> commonPrefixs = objectListing.getCommonPrefixes();
            for (String commonPrefix : commonPrefixs) {
                File file = new File(commonPrefix);
                String parent = file.getParent();
                if (parent == null)
                    parent = "/";
                fileList.add(new FilesDTO(driver.getBucketName() + "\t" + commonPrefix, file.getName(), parent, (short) -1, 0L, Type.Folder.getType(), null));
            }

            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                String key = cosObjectSummary.getKey();
                String etag = cosObjectSummary.getETag();
                long fileSize = cosObjectSummary.getSize();
                String storageClasses = cosObjectSummary.getStorageClass();
                Date lastModified = cosObjectSummary.getLastModified();
                File file = new File(key);
                fileList.add(new FilesDTO(key, file.getName(), file.getParent(), (short) -1, fileSize, Type.getInstance(file.getName()).getType(), lastModified));
            }
            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());
        cosClient.shutdown();
        return fileList;
    }

    private List<FilesDTO> getOneDriveFileList(Driver driver, String path) {
        return null;
    }

    private COSClient getCosClient(Driver driver) {
        COSCredentials cred = new BasicCOSCredentials(driver.getSecretId(), driver.getSecretKey());
        Region region = new Region(driver.getRegion());
        ClientConfig clientConfig = new ClientConfig(region);
        clientConfig.setHttpProtocol(HttpProtocol.https);
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

    private OSS getOssClient(Driver driver) {
        OSS oss = new OSSClientBuilder().build(driver.getRegion(), driver.getSecretId(), driver.getSecretKey());
        return oss;
    }

    private void Shutdown(OSS oss) {

    }

    private void Shutdown(COSClient cosClient) {
        if (cosClient != null) cosClient.shutdown();
    }


    public boolean CheckBucket(Driver driver) {
        switch (driver.getType()) {
            case Driver.Ali:
                OSS ossClient = getOssClient(driver);
                boolean result = ossClient.doesBucketExist(driver.getBucketName());
                ossClient.shutdown();
                return result;
            case Driver.Cos:
                COSClient cosClient = getCosClient(driver);
                boolean bucketExistFlag = cosClient.doesBucketExist(driver.getBucketName());
                cosClient.shutdown();
                return bucketExistFlag;
        }
        return false;
    }
}
