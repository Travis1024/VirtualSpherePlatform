package org.travis.agent.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.travis.agent.web.pojo.dto.FileMergeDTO;
import org.travis.shared.common.constants.ImageConstant;
import org.travis.shared.common.constants.LockConstant;
import org.travis.shared.common.constants.SystemConstant;
import org.travis.shared.common.enums.BizCodeEnum;
import org.travis.shared.common.exceptions.BadRequestException;
import org.travis.shared.common.exceptions.CommonException;
import org.travis.shared.common.exceptions.LockConflictException;
import org.travis.shared.common.exceptions.ServerErrorException;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName FileController
 * @Description FileController
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/18
 */
@RestController
@RequestMapping(SystemConstant.AGENT_REQUEST_PREFIX + SystemConstant.AGENT_FILE_CONTROLLER)
public class FileController {

    @Resource
    private RedissonClient redissonClient;

    @Operation(summary = "切片文件上传")
    @PostMapping(ImageConstant.SLICE_UPLOAD)
    public void imageSliceUpload(
            @Parameter(description = "切片文件", required = true) @RequestPart("sliceFile") MultipartFile multipartFile,
            @Parameter(description = "切片文件临时存储路径", required = true) @RequestParam("tempFilePath") String tempFilePath
        ) {
        RLock lock = redissonClient.getLock(LockConstant.LOCK_FILE_PREFIX + tempFilePath);
        try {
            // 1.根据存储临时路径加锁, 尝试拿锁 400ms 后停止重试 (自动续期)
            Assert.isTrue(lock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException("当前切片文件正在处理中，请稍后重试!"));
            // 2.校验文件存放临时文件的目录
            FileUtil.mkParentDirs(tempFilePath);
            // 3.将切片文件保存到本地临时文件夹
            multipartFile.transferTo(new File(tempFilePath));
        } catch (IOException | InterruptedException e) {
            throw new ServerErrorException("切片文件上传失败 -> " + e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Operation(summary = "切片文件合并")
    @PostMapping(ImageConstant.SLICE_MERGE)
    public void imageSliceMerge(@Validated @RequestBody FileMergeDTO fileMergeDTO) {
        RLock lock = redissonClient.getLock(LockConstant.LOCK_FILE_PREFIX + fileMergeDTO.getImageId());
        try {
            // 1.根据切片文件 ID 加锁, 尝试拿锁 400ms 后停止重试 (自动续期)
            Assert.isTrue(lock.tryLock(400, TimeUnit.MILLISECONDS), () -> new LockConflictException("当前切片文件正在处理中，请稍后重试!"));

            /*
             * 2.切片文件排序（切片文件序号都以下划线分割）
             * /opt/vsp/image/1712678912389115_0
             * /opt/vsp/image/1712678912389115_1
             */
            List<String> tempFilePathList = fileMergeDTO.getServerTempFilePathList();
            Assert.isFalse(tempFilePathList.isEmpty(), () -> new BadRequestException("切片文件列表不能为空!"));

            tempFilePathList.sort((o1, o2) -> {
                int o1Number = Integer.parseInt(o1.substring(o1.lastIndexOf(StrUtil.UNDERLINE) + 1));
                int o2Number = Integer.parseInt(o2.substring(o2.lastIndexOf(StrUtil.UNDERLINE) + 1));
                return o1Number - o2Number;
            });

            // 3.合并文件
            merge(tempFilePathList, fileMergeDTO.getServerFilePath());

            // 4.计算文件校验码并判断
            long crc32 = FileUtil.checksumCRC32(new File(fileMergeDTO.getServerFilePath()));
            Assert.isTrue(fileMergeDTO.getCrc32().equals(String.valueOf(crc32)), () -> {
                // 删除目标文件
                FileUtil.del(fileMergeDTO.getServerFilePath());
                return new BadRequestException("文件校验码校验失败!");
            });

        } catch (InterruptedException | IOException e) {
            throw new ServerErrorException("切片文件合并失败 -> " + e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public static void merge(List<String> sortTempFilePathList, String targetFilePath) throws IOException {
        RandomAccessFile finalFile = null;
        RandomAccessFile readerFile = null;
        try {
            // 1.初始化 最终文件
            FileUtil.touch(targetFilePath);
            finalFile = new RandomAccessFile(new File(targetFilePath), "rw");
            // 2.循环读取中间文件，并写入最终文件
            for (String tempFilePath : sortTempFilePathList) {
                // 2.1.读取中间文件
                readerFile = new RandomAccessFile(new File(tempFilePath), "r");
                byte[] bytes = new byte[1024];
                int length;
                while ((length = readerFile.read(bytes)) != -1) {
                    finalFile.write(bytes, 0, length);
                }
                // 2.2.关闭中间文件
                readerFile.close();
            }
        } finally {
            if (finalFile != null) {
                finalFile.close();
            }
            if (readerFile != null) {
                readerFile.close();
            }
        }
    }
}
