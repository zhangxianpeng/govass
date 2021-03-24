/**
 * Copyright (C), 2015-2021, XXX有限公司
 * FileName: CommonUtil
 * Author: zhang
 * Date: 2021/3/24 20:31
 * Description:
 * History:
 * <author> <time> <version> <desc>
 * 作者姓名 修改时间 版本号 描述
 */
package com.xianpeng.govass.util;

import android.content.Context;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import java.io.File;

public class CommonUtil {
    public static String getRootDirPath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File file = ContextCompat.getExternalFilesDirs(context.getApplicationContext(), null)[0];
            return file.getAbsolutePath();
        } else {
            return context.getApplicationContext().getFilesDir().getAbsolutePath();
        }
    }
}
