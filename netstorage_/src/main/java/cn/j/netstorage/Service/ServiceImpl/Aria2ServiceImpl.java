package cn.j.netstorage.Service.ServiceImpl;

import cn.j.netstorage.Entity.File.Aria2File;
import cn.j.netstorage.Entity.User.User;
import cn.j.netstorage.Mapper.AriaMapper;
import cn.j.netstorage.Service.Aria2Service;
import cn.j.netstorage.Service.UploadService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class Aria2ServiceImpl implements Aria2Service {

    @Autowired
    private AriaMapper ariaMapper;
    @Autowired
    private UploadService uploadService;
    private final static String url = "http://localhost:6800/jsonrpc";

    private final static String METHOD_TELL_ACTIVE = "aria2.tellActive";
    private final static String METHOD_ADD_URI = "aria2.addUri";
    public final static String METHOD_GET_GLOBAL_STAT = "aria2.getGlobalStat";
    public final static String METHOD_FORCE_PAUSE = "aria2.forcePause";
    public final static String METHOD_Start = "aria2.unPause";
    private final static String METHOD_tellStopped = "aria2.tellStopped";
    private final static String METHOD_tellWaiting = "aria2.tellWaiting";
    public final static String METHOD_addTorrent = "aria2.addTorrent";

    private String[] rowName = new String[]{"gid",
            "totalLength",
            "completedLength",
            "uploadSpeed",
            "downloadSpeed",
            "connections",
            "numSeeders",
            "seeder",
            "status",
            "errorCode",
            "verifiedLength",
            "verifyIntegrityPending",
            "files",
            "bittorrent",
            "infoHash"};

    private JsonObject commonJson(String method, JsonArray array) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("jsonrpc", "2.0");
        jsonObject.addProperty("method", method);
        jsonObject.addProperty("id", "abc");
        jsonObject.add("params", array);
        return jsonObject;
    }

//    private String getUrl(String url) {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        final Request request = new Request.Builder()
//                .url(url)
//                .build();
//        Call call = okHttpClient.newCall(request);
//        try {
//            Response response = call.execute();
//            if (response.isRedirect()){
//                String redirect=response.header("Location");
//                response.close();
//                return getUrl(redirect);
//            }
//            return url;
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//        return null;
//    }

    @Override
    public Boolean download(String url, String path, User user) {
        JsonObject jsonObject = commonJson(METHOD_ADD_URI, tellParams(tellParams(url)));
        try {
            Response response = post(jsonObject);
            String res = parse(response);
            if (!StringUtils.hasText(res))
                return false;
            Aria2File file = new Aria2File();
            file.setPath(path);
            file.setUser(user);
            file.setGid(res);
            file.setType("URI");
            file.setName(url.substring(url.lastIndexOf("/") + 1));
            return ariaMapper.save(file).getId() != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Map<String, List<String>> getTorrentDetail(String gid) {
//        aria2.tellStatus
        System.out.println(gid);
        Map<String, List<String>> map = new LinkedHashMap<>();
        Response response = post(commonJson("aria2.tellStatus", tellParams(gid)));
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            JsonObject jsonObject = gson.fromJson(response.body().string(), JsonObject.class).getAsJsonObject("result");
            if (jsonObject.has("bittorrent")) {
                JsonObject bittorrent = jsonObject.getAsJsonObject("bittorrent");
                String info = bittorrent.getAsJsonObject("info").getAsJsonObject("name").getAsString();
                List<String> names = map.getOrDefault(info, new ArrayList<>());
                JsonArray jsonArray = jsonObject.getAsJsonArray("files");
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject object = jsonArray.get(i).getAsJsonObject();
                    names.add(object.get("path").getAsString());
                }
                map.put(info, names);
                return map;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String getSimpleDetail(String gid) {
        Response response = post(commonJson("aria2.tellStatus", tellParams(gid)));

        JsonObject jsonObject = null;
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            jsonObject = gson.fromJson(response.body().string(), JsonObject.class).getAsJsonObject("result");
            JsonArray jsonArray = jsonObject.getAsJsonArray("files");
            JsonObject object = jsonArray.get(0).getAsJsonObject();
            String res = object.get("path").getAsString();
            return res;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Boolean download(Long fid, String path, User user) {
        //todo aria2.addTorrent
        return null;
    }


    @Override
    public JsonArray getActive(User user) {
        JsonObject jsonObject = commonJson(METHOD_TELL_ACTIVE, tellParams(tellParams(rowName)));
        return compare(post(jsonObject), user);
    }

    @Override
    public JsonArray getStopped(User user) {
//        {"id":"abc","jsonrpc":"2.0","result":[{"bitfield":"e0","completedLength":"2462637","connections":"0","dir":"Download","downloadSpeed":"0","errorCode":"0","errorMessage":"","files":[{"completedLength":"2462637","index":"1","length":"2462637","path":"Download\/obj%2Fwo3DlMOGwrbDjj7DisKw%2F4959745806%2F482a%2F1a84%2Fca27%2F02c0f32c8c1b78a97988cebbee2ede1b.mp3","selected":"true","uris":[{"status":"used","uri":"http:\/\/m701.music.126.net\/20210303195832\/7170bf045ec864d1609355f2a85a2a00\/jdymusic\/obj\/wo3DlMOGwrbDjj7DisKw\/4959745806\/482a\/1a84\/ca27\/02c0f32c8c1b78a97988cebbee2ede1b.mp3"},{"status":"waiting","uri":"http:\/\/m701.music.126.net\/20210303195832\/7170bf045ec864d1609355f2a85a2a00\/jdymusic\/obj\/wo3DlMOGwrbDjj7DisKw\/4959745806\/482a\/1a84\/ca27\/02c0f32c8c1b78a97988cebbee2ede1b.mp3"},{"status":"waiting","uri":"http:\/\/m701.music.126.net\/20210303195832\/7170bf045ec864d1609355f2a85a2a00\/jdymusic\/obj\/wo3DlMOGwrbDjj7DisKw\/4959745806\/482a\/1a84\/ca27\/02c0f32c8c1b78a97988cebbee2ede1b.mp3"},{"status":"waiting","uri":"http:\/\/m701.music.126.net\/20210303195832\/7170bf045ec864d1609355f2a85a2a00\/jdymusic\/obj\/wo3DlMOGwrbDjj7DisKw\/4959745806\/482a\/1a84\/ca27\/02c0f32c8c1b78a97988cebbee2ede1b.mp3"},{"status":"waiting","uri":"http:\/\/m701.music.126.net\/20210303195832\/7170bf045ec864d1609355f2a85a2a00\/jdymusic\/obj\/wo3DlMOGwrbDjj7DisKw\/4959745806\/482a\/1a84\/ca27\/02c0f32c8c1b78a97988cebbee2ede1b.mp3"},{"status":"waiting","uri":"http:\/\/m701.music.126.net\/20210303195832\/7170bf045ec864d1609355f2a85a2a00\/jdymusic\/obj\/wo3DlMOGwrbDjj7DisKw\/4959745806\/482a\/1a84\/ca27\/02c0f32c8c1b78a97988cebbee2ede1b.mp3"}]}],"gid":"b289679ce0d38ae4","numPieces":"3","pieceLength":"1048576","status":"complete","totalLength":"2462637","uploadLength":"0","uploadSpeed":"0"}]}
        JsonObject jsonObject = commonJson(METHOD_tellStopped, tellParams(0, 1000));
        return compare(post((jsonObject)), user);
    }

    @Override
    public JsonArray getWaiting(User user) {
        JsonObject jsonObject = commonJson(METHOD_tellWaiting, tellParams(0, 1000));
        return compare(post(jsonObject), user);
    }


    @Override
    public Boolean stop(String id, User user) {
        //todo
//        Response response = post(Collections.singletonList(SetParams(METHOD_FORCE_PAUSE, id)));
        return true;
    }

    @Override
    public Boolean start(String id, User user) {
//        Response response = post(Collections.singletonList(SetParams(METHOD_Start, id)));
        return true;
    }

    @Value("${aria2}")
    String aria2;

    @Override
    public Boolean finish(String gid) {
        System.out.println("finish:" + gid);
        if (!StringUtils.hasText(gid))
            return false;
        File file = null;
        Aria2File task = ariaMapper.findByGid(gid);
        if (task == null) return false;

        User user = task.getUser();
        String path = task.getPath();

        if ("TORRENT".equals(task.getType())) {
            Map<String, List<String>> map = getTorrentDetail(gid);
            for (String s : map.keySet()) {
                List<String> paths = map.get(s);
                for (String p : paths) {
                    File f = new File(aria2 + "/" + p);
                    if (f.exists()) {
                        return uploadService.multi_exist_upload(s, path, f.getName(), f.getAbsolutePath() + p, user);
                    }
                }
            }
        } else {
            file = new File(aria2 + "/" + getSimpleDetail(gid));
            System.out.println(file.exists());
            if (!file.exists())
                return false;
            return uploadService.exist_upload(file.getAbsolutePath(), file.getName(), path, user);
        }
        return false;
    }

    @Override
    public Boolean cancel(String gid, User user) {
        return null;
    }

    private List<Aria2File> gets(User user) {
        return ariaMapper.findAllByUserIs(user);
    }

    public Response post(List<LinkedHashMap<String, Object>> maps) {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody formBody = RequestBody.create(JSON, gson.toJson(maps));
        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            return call.execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Response post(JsonObject jsonObject) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        RequestBody formBody = RequestBody.create(JSON, jsonObject.toString());
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            return call.execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private JsonArray compare(Response response, User user) {
        if (response == null) return null;
        List<Aria2File> files = gets(user);
        List<String> strings = new ArrayList<>();
        JsonArray jsonArray = new JsonArray();
        files.forEach(v -> strings.add(v.getGid()));


        try {
            JsonObject res = new Gson().fromJson(response.body().string(), JsonObject.class);

            JsonArray array = res.getAsJsonArray("result");
            if (array == null || res.size() == 0)
                return null;
            for (int i = 0; i < array.size(); i++) {
                JsonObject object = array.get(i).getAsJsonObject();
                if (!object.has("gid"))
                    continue;
                if (strings.contains(object.get("gid").getAsString())) {
                    object.addProperty("name", files.get(strings.lastIndexOf(object.get("gid").getAsString())).getName());
                    jsonArray.add(object);
                }
//                System.out.println(object);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    private static JsonArray tellParams(int... params) {
        JsonArray jsonArray = new JsonArray();
        for (int param : params) {
            jsonArray.add(param);
        }
        return jsonArray;
    }

    private static JsonArray tellParams(String... params) {
        JsonArray jsonArray = new JsonArray();
        for (String param : params) {
            jsonArray.add(param);
        }
        return jsonArray;
    }

    private static JsonArray tellParams(JsonArray params) {
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(params);
        return jsonArray;
    }

    public String parse(Response response) throws IOException {
        if (!response.isSuccessful())
            return null;
        String content = response.body().string();
        if (!StringUtils.hasText(content))
            return null;

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        JsonObject jsonObject = gson.fromJson(content, JsonObject.class);
        if (jsonObject == null)
            return null;
        if (jsonObject.has("result"))
            return jsonObject.get("result").getAsString();
        return null;
    }

//
//    @Override
//    public Boolean download(String id, String url) {
//        return false;
//    }
//
//    @Override
//    public List<String> downloadList(String id, User user) {
//        return activeList(id,StoppedList(id,user).stream().map(Aria2File::getGid).collect(Collectors.toList()));
//    }
//
//    private List<String> activeList(String id, List<String> list) {
//        Aria2Entity aria2Entity = createEntity(id, "aria2.tellActive", Arrays.asList(rowName));
//        String jsonString = new Gson().toJson(aria2Entity);
//        String resultString = Aria2Http.post(uri, jsonString);
//        List<String> resultList=new ArrayList<>();
//        JsonArray result = new Gson().fromJson(resultString, JsonObject.class).get("result").getAsJsonArray();
//        result.forEach((value)->{
//            String gid=value.getAsJsonObject().get("gid").getAsString();
//            if (list.contains(gid)){
//                resultList.add(new Gson().toJson(value));
//            }
//        });
//        return resultList;
//    }
//
//
//    /**
//     * 获得当前用户在数据库中的所有任务
//     * @param id
//     * @param user
//     * @return
//     */
//    @Override
//    public List<Aria2File> StoppedList(String id, User user) {
//        return ariaMapper.findAllByUserIs(user);
//    }
//
//
//    @Override
//    public Boolean pause(String id, String gid) {
////        aria2.forcePause
//        Aria2Entity aria2Entity=createEntity(id,"aria2.forcePause",Collections.singleton(gid));
//        String result= Aria2Http.post(uri,new Gson().toJson(aria2Entity));
//        if (StringUtils.hasText(result)){
////            result: "492d9e87968f4e45"
//            JsonObject jsonObject=new Gson().fromJson(result,JsonObject.class);
//            String Gid=jsonObject.get("result").getAsString();
//            return gid.equals(Gid);
//        }
//        return null;
//    }
//
//    @Override
//    public Boolean unpause(String id, String gid) {
//        String method="aria2.unpause";
//        Aria2Entity aria2Entity=createEntity(id,method,Collections.singletonList(gid));
//        String result=Aria2Http.post(uri,new Gson().toJson(aria2Entity));
//        if (StringUtils.hasText(result)){
//            JsonObject jsonObject=new Gson().fromJson(result,JsonObject.class);
//            String Gid=jsonObject.get("result").getAsString();
//            return gid.equals(Gid);
//        }
//        return false;
//    }
//
//
//    @Override
//    public String Detail(String id, String gid) {
////        {"id":"detail-1","jsonrpc":"2.0","result":{"bittorrent":{"announceList":[["http:\/\/open.acgtracker.com:1096\/announce"]]},"completedLength":"0","connections":"0","dir":"D:\/\/test\/\/","downloadSpeed":"0","files":[{"completedLength":"0","index":"1","length":"0","path":"[METADATA]64923d9b6f7fd291470f68edb2c4dbaa1346a389","selected":"true","uris":[]}],"gid":"c102abe5feda0996","infoHash":"64923d9b6f7fd291470f68edb2c4dbaa1346a389","numPieces":"0","numSeeders":"0","pieceLength":"16384","seeder":"false","status":"active","totalLength":"0","uploadLength":"0","uploadSpeed":"0"}}
////        tellStatus
//        Aria2Entity aria2Entity = createEntity(id, "aria2.tellStatus", gid);
//        String result = Aria2Http.post(uri, new Gson().toJson(aria2Entity));
//        return result;
//    }
//
//    private Aria2Entity createEntity(String id, String method, Object... params) {
//        Aria2Entity aria2Entity = new Aria2Entity();
//        aria2Entity.setId(id);
//        aria2Entity.setJsonrpc("2.0");
//        aria2Entity.setMethod(method);
//        aria2Entity.setParams(params);
//        return aria2Entity;
//    }
//
//
//    @Override
//    public Boolean downloadTorrent(String id, Long fid, User user, OriginFile originFile) {
//        try {
//            /* base64转码后加入下载列表 */
//            File file = new File(originFile.getPath());
//            FileInputStream inputFile = new FileInputStream(file);
//            byte[] buffer = new byte[(int) file.length()];
//            inputFile.read(buffer);
//            inputFile.close();
//
//            String base64code = new BASE64Encoder().encode(buffer);
//            //随机存储位置
//            String fileName=originFile.getFileName();
//            Type type=Type.getInstance(fileName.substring(fileName.lastIndexOf("."),fileName.length()-1));
//
//            HardDiskDevice hardDiskDevice = hardDeviceService.get(type);
////            HardDiskDevice hardDiskDevice = hardDiskDevices.get(new Random().nextInt(hardDiskDevices.size()));
//            //传参
//            HashMap<String, String> hashMap = new HashMap<>();
//            hashMap.put("dir", hardDiskDevice.getFolderName());
//            Aria2Entity aria2Entity = createEntity("QXJpYU5nXzE2MDQzMzM5MTlfMC4zMjU5NDUwODU3NzUzNDA1NA==", "aria2.addTorrent", base64code, Collections.EMPTY_LIST, hashMap);
//            String jsonString = new Gson().toJson(aria2Entity);
//            System.out.println(jsonString);
//            //获得结果
//            String result = Aria2Http.post(uri, jsonString);
//            System.out.println(result);
//            /* 写数据库*/
//            if (StringUtils.hasText(result)) {
//                //获得gid
//                String gid = new Gson().fromJson(result, JsonObject.class).get("result").getAsString();
//                String detail = Detail(id, gid);
//                JsonObject jsonObject = new Gson().fromJson(detail, JsonObject.class);
//                JsonObject detailResult = jsonObject.get("result").getAsJsonObject();
//                JsonObject bittorrent = detailResult.getAsJsonObject("bittorrent");
//                JsonArray files = detailResult.getAsJsonArray("files");
//                //根据file创建文件
//                JsonObject info = bittorrent.getAsJsonObject("info");
//                String [] arr= FilesUtil.getFileNameAndExt(info.get("name").getAsString());
//                String name =arr[0].equals("true")?arr[1]:"任务组";
//
//                Aria2File aria2File = new Aria2File();
//                aria2File.setName(name);
//                aria2File.setGid(gid);
//                aria2File.setUser(Collections.singletonList(user));
//                ariaMapper.save(aria2File);
//
//                FilesDTO torrentFile = filesService.getFilesById(fid);//获得原始种子文件
//
//                Files folder = FilesUtil.createFolder(name, torrentFile.getParentName(), Collections.singletonList(user));
//                folder.setOriginFile(Collections.singleton(filesService.insertFolder()));
//                fileMapper.save(folder);
//                files.forEach((value) -> {
//                    String selfName = new File(value.getAsJsonObject().get("path").getAsString()).getName();
//
//                    Files f = FilesUtil.createFiles(selfName, folder.getParentName() + folder.getSelfName()+"/", FilesUtil.setUserList(4L));
//
//                    OriginFile mapperFile = new OriginFile();
//                    mapperFile.setHardDiskDevice(FilesUtil.convert(hardDiskDevice));
//                    mapperFile.setSize(Long.valueOf(value.getAsJsonObject().get("length").getAsString()));
//                    mapperFile.setMd5(UUID.randomUUID().toString().replaceAll("-", ""));
//                    mapperFile.setFileName(selfName);
//                    f.setOriginFile(FilesUtil.convert(mapperFile));
//                    originFileMapper.save(mapperFile);
//                    fileMapper.save(f);
//                });
//                return true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
}
