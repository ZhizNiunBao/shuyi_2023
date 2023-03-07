package cn.bywin.business.hetu;

import com.google.gson.Gson;
import io.prestosql.jdbc.$internal.airlift.json.JsonCodec;
import io.prestosql.jdbc.$internal.okhttp3.MediaType;
import io.prestosql.jdbc.$internal.okhttp3.MultipartBody;
import io.prestosql.jdbc.$internal.okhttp3.OkHttpClient;
import io.prestosql.jdbc.$internal.okhttp3.Request;
import io.prestosql.jdbc.$internal.okhttp3.RequestBody;
import io.prestosql.jdbc.$internal.okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 动态添加目录工具类
 * @author firepation
 */
public class HetuDynamicCatalogUtil extends HetuBaseUtil {

    private static final JsonCodec<List<String>> STRING_LIST_CODE = JsonCodec.listJsonCodec(String.class);

    private static final Logger logger = LoggerFactory.getLogger(HetuDynamicCatalogUtil.class);

    private static final int SUCCESS = 200;

    private static final int SYSTEM_ERROR = 500;

    private static final String DEFAULT_ERROR_MESSAGE = "请求失败";

    /**
     * 判断某个 catalog 是否存在
     * @param hetuInfo hetu 配置信息
     * @param catalogName 目录名称
     * @return 目录是否存在
     */
    public static boolean checkCatalogExist(HetuInfo hetuInfo, String catalogName) throws IOException {
        OkHttpClient httpClient = createHttpClient(hetuInfo);
        Request request = new Request.Builder()
                .url(hetuInfo.getConnectionUrl() + "/v1/catalog")
                .get()
                .build();
        try(Response response = httpClient.newCall(request).execute()) {
            if (response != null && response.isSuccessful()) {
                String resultString = response.body().string();
                List<String> catalogs = STRING_LIST_CODE.fromJson(resultString);
                return catalogs.contains(catalogName);
            }
            return false;
        }
    }

    /**
     * 动态删除目录
     * @param hetuInfo hetu 配置信息
     * @param catalogName 目录名称
     * @return 是否删除成功
     */
    public static DynamicCatalogResult deleteCatalog(HetuInfo hetuInfo, String catalogName) throws IOException {
        boolean exist = checkCatalogExist(hetuInfo, catalogName);
        if (!exist) {
            return new DynamicCatalogResult(SUCCESS, "");
        }

        OkHttpClient httpClient = createHttpClient(hetuInfo);
        Request request = new Request.Builder()
                .url(hetuInfo.getConnectionUrl() + "/v1/catalog/" + catalogName)
                .delete()
                .build();
        try(Response response = httpClient.newCall(request).execute()) {
            if (response != null) {
                int code = response.code();
                if (response.isSuccessful()) {
                    logger.info("delete catalog {} success", catalogName);
                } else {
                    logger.error("delete catalog {} failed, error message: {}", catalogName, response.message());
                }
                return new DynamicCatalogResult(code, response.message());
            }
        }
        return new DynamicCatalogResult(SYSTEM_ERROR, DEFAULT_ERROR_MESSAGE);
    }

    /**
     * 动态添加目录
     * @param hetuInfo   hetu 配置信息
     * @param catalogInfo      动态目录的配置信息
     */
    public static DynamicCatalogResult addCatalog(HetuInfo hetuInfo, CatalogInfo catalogInfo) throws IOException {
        OkHttpClient client = createHttpClient(hetuInfo);

        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        Gson gson = new Gson();
        requestBodyBuilder.addFormDataPart("catalogInformation", gson.toJson(catalogInfo));

        List<String> configFilePaths = catalogInfo.getConfigFilePaths();
        configFilePaths.forEach(configFilePath -> {
            File catalogFile = new File(configFilePath);
            RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), catalogFile);
            requestBodyBuilder.addFormDataPart("catalogConfigurationFiles", catalogFile.getName(), fileBody);
        });

        Request.Builder requestBuilder = new Request.Builder()
                .url(hetuInfo.getConnectionUrl() + "/v1/catalog");
        if (checkCatalogExist(hetuInfo, catalogInfo.getCatalogName())) {
            requestBuilder = requestBuilder.put(requestBodyBuilder.build());
        } else {
            requestBuilder = requestBuilder.post(requestBodyBuilder.build());
        }
        Request request = requestBuilder.build();

        try (Response response = client.newCall(request).execute()) {
            if (response != null) {
                int code = response.code();
                if (response.isSuccessful()) {
                    logger.info("add catalog success");
                } else {
                    logger.info("request failed, error message: {}", response.message());
                }
                return new DynamicCatalogResult(code, response.message());
            }
        }
        return new DynamicCatalogResult(SYSTEM_ERROR, DEFAULT_ERROR_MESSAGE);
    }
}
