package org.travis.center.common.utils;

import org.travis.shared.common.constants.MonitorConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.redis.serializer.SerializationException;
import redis.clients.jedis.Jedis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName GzipUtil
 * @Description Gzip解压缩依赖类
 * @Author travis-wei
 * @Version v1.0
 * @Data 2023/8/29
 */
@Slf4j
public class GzipUtil {

    /**
     * Gzip 序列化
     */
    public static byte[] serialize(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return new byte[0];
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);
        gzipOutputStream.write(data);
        gzipOutputStream.close();
        return outputStream.toByteArray();
    }

    /**
     * @MethodName deserialize
     * @Description 解压 and 反序列化监控数据
     * @Author travis-wei
     **/
    public static String deserialize(Jedis jedis, String key) throws SerializationException {
        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        GZIPInputStream gzip = null;
        try {
            // 从 redis 阻塞队列中获取数据
            byte[] bytes = jedis.lpop(key.getBytes());
            if (bytes == null || bytes.length == 0) {
                log.warn("[No Data] -> " + key);
                return null;
            }

            bos = new ByteArrayOutputStream();
            bis = new ByteArrayInputStream(bytes);
            gzip = new GZIPInputStream(bis);
            byte[] buff = new byte[MonitorConstant.BUFFER_SIZE];
            int n;
            // 先解压
            while ((n = gzip.read(buff, 0, MonitorConstant.BUFFER_SIZE)) > 0) {
                bos.write(buff, 0, n);
            }
            return bos.toString();
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(gzip);
        }
    }

    /**
     * @MethodName deserialize
     * @Description Gzip 解压缩
     * @Author travis-wei
     * @Data 2023/8/29
     **/
    public static String deserialize(byte[] bytes) throws SerializationException {
        ByteArrayOutputStream bos = null;
        ByteArrayInputStream bis = null;
        GZIPInputStream gzip = null;
        try {
            if (bytes == null || bytes.length == 0) {
                return null;
            }

            bos = new ByteArrayOutputStream();
            bis = new ByteArrayInputStream(bytes);
            gzip = new GZIPInputStream(bis);
            byte[] buff = new byte[MonitorConstant.BUFFER_SIZE];
            int n;
            // 先解压
            while ((n = gzip.read(buff, 0, MonitorConstant.BUFFER_SIZE)) > 0) {
                bos.write(buff, 0, n);
            }
            return bos.toString();
        } catch (Exception e) {
            log.error(e.toString());
            return null;
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(bis);
            IOUtils.closeQuietly(gzip);
        }
    }
}
