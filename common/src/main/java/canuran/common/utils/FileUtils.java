package canuran.common.utils;

import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * 文件工具类。
 *
 * @author canuran
 * @since 2020年04月29日
 */
public final class FileUtils {
    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * 用于释放依赖的可执行文件到指定目录，如 gif2webp 依赖用于转换 gif 格式为 webp 格式。
     */
    public static String copyCommandFromResource(String linuxFile, String windowsFile, String targetPath) {
        String osName = System.getProperty("os.name");
        boolean isWindows = osName != null && osName.contains("Windows");
        String fileName = isWindows ? windowsFile : linuxFile;

        try (InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(fileName)) {

            File targetPathDirectory = new File(targetPath);
            if (!targetPathDirectory.exists() || !targetPathDirectory.mkdirs())
                throw new IllegalStateException("Make directories fail");

            String filePath = targetPath.concat(fileName);
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                FileCopyUtils.copy(Objects.requireNonNull(inputStream), outputStream);

                File commandFile = new File(filePath);
                if (commandFile.exists()) {
                    String absolutePath = commandFile.getAbsolutePath();
                    if (!isWindows)
                        Runtime.getRuntime().exec("chmod 777 -R " + absolutePath);
                    return absolutePath;

                } else {
                    throw new IllegalStateException("Copy command file fail");
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
