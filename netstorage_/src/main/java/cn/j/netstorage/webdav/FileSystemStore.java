package cn.j.netstorage.webdav;

import cn.j.netstorage.Entity.DTO.FilesDTO;
import cn.j.netstorage.Entity.File.Files;
import cn.j.netstorage.Entity.File.OriginFile;
import cn.j.netstorage.Entity.Folder.Folder;
import cn.j.netstorage.Entity.Type;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Service.FileService2;
import cn.j.netstorage.Service.FilesService;
import cn.j.netstorage.Service.UploadService;
import cn.j.netstorage.Service.UserService;
import com.qcloud.cos.transfer.Upload;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.Transaction;
import net.sf.webdav.exceptions.WebdavException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileSystemStore implements IWebdavStore {


    private static int BUF_SIZE = 65536;

    private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory
            .getLogger(FileSystemStore.class);


    @Autowired
    private FilesService filesService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService2 fileService2;

    @Autowired
    private UploadService uploadService;

    private static int chunkSize = 10485760; // 10MB


    public void destroy() {
        ;
    }

    @Override
    public ITransaction begin(Principal principal, HttpServletRequest req, HttpServletResponse resp) {
        return new Transaction(principal, req, resp);
    }

    public ITransaction begin(Principal principal) throws WebdavException {
        return null;
    }

    public void checkAuthentication(ITransaction transaction)
            throws SecurityException {
        LOG.trace("LocalFileSystemStore.checkAuthentication()");
        // do nothing

    }

    public void commit(ITransaction transaction) throws WebdavException {
        // do nothing
        LOG.trace("LocalFileSystemStore.commit()");
    }

    public void rollback(ITransaction transaction) throws WebdavException {
        // do nothing
        LOG.trace("LocalFileSystemStore.rollback()");

    }

    public void createFolder(ITransaction transaction, String uri)
            throws WebdavException {

        User user = getUser(transaction);
        if (user == null)
            return;

        FileParam fileParam = getFileParm(uri);
        if (fileParam == null)
            return;

        if (fileParam.getPath().equals("/") && fileParam.getFileName() == null) {
            return;
        }

        Files files = Files.setFolder(fileParam.getPath(), fileParam.getFileName(), user);
        boolean res = uploadService.common_upload_Folder(files);
    }

    public void createResource(ITransaction transaction, String uri)
            throws WebdavException {
        System.out.println("createResource");
    }

    public long setResourceContent(ITransaction transaction, String uri,
                                   InputStream is, String contentType, String characterEncoding)
            throws WebdavException {
        User user = getUser(transaction);
        if (user == null)
            return 0L;

        FileParam fileParam = getFileParm(uri);
        System.out.println("setResourceContent"+uri+"\nFileParam : "+fileParam);
        if (fileParam == null)
            return 0L;

        uploadService.common_upload(is, fileParam.getPath(), fileParam.getFileName(), user);
        return 0L;
    }

    public String[] getChildrenNames(ITransaction transaction, String uri)
            throws WebdavException {
        System.out.println("getChildrenNames:" + uri);
        User user = getUser(transaction);
        if (user == null)
            return new String[]{};

        List<FilesDTO> list = filesService.UserFile(uri, user, true);
        String[] strings = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            strings[i] = list.get(i).getSelfName();
        }
        return strings;
    }

    public void removeObject(ITransaction transaction, String uri)
            throws WebdavException {
        User user = getUser(transaction);
        if (user == null)
            return;

        FileParam fileParam = getFileParm(uri);
        if (fileParam == null)
            return;

        Files files = getFiles(user, fileParam);

        if (files == null)
            return;
        fileService2.del(files);
    }

    @Override
    public boolean moveObject(ITransaction transaction, String destinationPath, String sourcePath) {
        System.out.println("destinationPath:"+destinationPath+"\nsourcePath: "+sourcePath);
        User user = getUser(transaction);
        if (user == null)
            return false;

        FileParam fileParam = getFileParm(sourcePath);
        FileParam destination=getFileParm(destinationPath);
        if (fileParam == null)
            return false;

        if (destination == null)
            return false;
        Files files = getFiles(user, fileParam);
        if (files == null) return false;
        return fileService2.move(files, destination.getPath());
    }

    public InputStream getResourceContent(ITransaction transaction, String uri)
            throws WebdavException {
        System.out.println("getResourceContent :" + uri);
        User user = getUser(transaction);
        if (user == null)
            return null;

        FileParam fileParam = getFileParm(uri);
        if (fileParam == null)
            return null;

        Files files = getFiles(user, fileParam);

        if (files == null)
            return null;

        OriginFile originFile = files.getOriginFile();
        if (originFile == null)
            return null;

        InputStream in;
        try {
            in = new BufferedInputStream(new FileInputStream(originFile.getPath()));
        } catch (IOException e) {
            throw new WebdavException(e);
        }
        return in;
    }

    public long getResourceLength(ITransaction transaction, String uri)
            throws WebdavException {
        System.out.println("getResourceLength:" + uri);
        User user = getUser(transaction);
        if (user == null)
            return 0L;

        FileParam fileParam = getFileParm(uri);
        if (fileParam == null)
            return 0L;

        if (fileParam.getPath().equals("/") && fileParam.getFileName() == null) {
            return 0L;
        }

        Files files = getFiles(user, fileParam);

        if (files == null) return 0L;

        return files.getOriginFile().getSize();
    }

    public StoredObject getStoredObject(ITransaction transaction, String uri) {
        StoredObject so = new StoredObject();

        User user = getUser(transaction);
        if (user == null)
            return null;

        FileParam fileParam = getFileParm(uri);
        if (fileParam == null) {
            so.setFolder(true);
            so.setLastModified(new Date());
            so.setCreationDate(new Date());
            so.setResourceLength(0L);
            return so;
        }

        if (fileParam.getPath().equals("/") && fileParam.getFileName() == null) {
            so.setFolder(true);
            so.setLastModified(new Date());
            so.setCreationDate(new Date());
            so.setResourceLength(0L);
            return so;
        }

        Files files = getFiles(user, fileParam);

        if (files == null) {
            return null;
        }

        if (Type.Folder.getType().equals(files.getType())) {
            so.setFolder(Type.Folder.getType().equals(files.getType()));
            so.setLastModified(files.getCreateDate());
            so.setCreationDate(files.getCreateDate());
            so.setResourceLength(0L);
        } else {
            OriginFile originFile = files.getOriginFile();
            File targetFile = new File(originFile.getPath());
            so.setFolder(false);
            so.setLastModified(new Date(targetFile.lastModified()));
            so.setCreationDate(files.getCreateDate());
            so.setResourceLength(originFile.getSize());
        }
        return so;
    }

    private User getUser(ITransaction transaction) {
        String userName = transaction.getPrincipal().getName();
        User user = null;
        if (StringUtils.hasText(userName)) {
            user = userService.getUser(userName);
        }
        return user;
    }

    private FileParam getFileParm(String uri) {
        if (uri.length() == 0) return null;
        if (uri.length() == 1) return new FileParam("/", null);
        if (uri.endsWith("/")) uri = uri.substring(0, uri.length() - 1);
        int index = uri.lastIndexOf("/");
        String parent = uri.substring(0, index + 1);
        String self = uri.substring(index + 1, uri.length());
        return new FileParam(parent, self);
    }

    public Files getFiles(User user, FileParam fileParam) {
        return fileService2.getFiles(fileParam.getPath(), fileParam.getFileName(), user);
    }

    @Setter
    @Getter
    @ToString
    public class FileParam {
        private String Path;
        private String fileName;

        public FileParam(String path, String fileName) {
            Path = path;
            this.fileName = fileName;
        }
    }
}
