package cdn.youga.pldroid

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class JarZipUtil {

    private static String mDestJarName

    /**
     * 将该jar包解压到指定目录
     * @param jarPath jar包的绝对路径
     * @param destDirPath jar包解压后的保存路径
     * @return 返回该jar包中包含的所有class的完整类名类名集合，其中一条数据如：com.aitski.hotpatch.Xxxx.class
     */
    static void unzipJar(String jarPath, String destDirPath) {
        if (!jarPath.endsWith('.jar')) return
        JarFile jarFile = new JarFile(jarPath)
        Enumeration<JarEntry> jarEntrys = jarFile.entries()
        while (jarEntrys.hasMoreElements()) {
            JarEntry jarEntry = jarEntrys.nextElement()
            if (jarEntry.directory) {
                continue
            }
            String entryName = jarEntry.getName()
            String outFileName = destDirPath + "/" + entryName
            File outFile = new File(outFileName)
            outFile.getParentFile().mkdirs()
            InputStream inputStream = jarFile.getInputStream(jarEntry)
            FileOutputStream fileOutputStream = new FileOutputStream(outFile)
            fileOutputStream << inputStream
            fileOutputStream.close()
            inputStream.close()
        }
        jarFile.close()
    }

    static void zipJar(File jarFile, String jarZipDir, String destPath) {
        mDestJarName = jarFile.getCanonicalPath()
        JarOutputStream outputStream = new JarOutputStream(new FileOutputStream(destPath))
        zipJar(jarZipDir, outputStream, null)
    }

    /**
     * 重新打包jar
     * @param packagePath 将这个目录下的所有文件打包成jar
     * @param destPath 打包好的jar包的绝对路径
     */
    static void zipJar(String jarZipDir, JarOutputStream outputStream, String path) {

        File sourceDir = new File(jarZipDir)

        if (sourceDir.isDirectory()) {
            String[] dirList = sourceDir.list();
            String subPath = (path == null) ? "" : (path + sourceDir.getName() + '/');
            if (path != null) {
                JarEntry je = new JarEntry(subPath);
                je.setTime(sourceDir.lastModified());
                outputStream.putNextEntry(je);
                outputStream.flush();
                outputStream.closeEntry();
            }
            for (int i = 0; i < dirList.length; i++) {
                File f = new File(sourceDir, dirList[i]);
                zipJar(f.getAbsolutePath(), outputStream, subPath);
            }
        } else {
            if (sourceDir.getCanonicalPath().equals(mDestJarName)) {
                return;
            }
            System.out.println("JarEntry:" + sourceDir.getPath());
            FileInputStream fis = new FileInputStream(sourceDir);
            JarEntry entry = new JarEntry(path + sourceDir.getName());
            entry.setTime(sourceDir.lastModified());
            outputStream.putNextEntry(entry);
            int count;
            byte[] buffer = new byte[2156];
            while ((count = fis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            outputStream.flush();
            outputStream.closeEntry();
        }
    }

}