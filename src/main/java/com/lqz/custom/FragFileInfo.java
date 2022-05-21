package com.lqz.custom;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.File;

/**
 * @author LQZ
 * @date 2022-05-20 23:49
 */

@Data
@Accessors(chain = true)
public class FragFileInfo {
    /**
     * 分片的文件
     */
    private File tmpFile;
    /**
     * 分片的序号
     */
    private int partIndex;
    /**
     * 分片文件的 md5
     */
    private String md5;
}
