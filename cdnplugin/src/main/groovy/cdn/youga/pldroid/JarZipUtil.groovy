package cdn.youga.pldroid

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


class JarZipUtil {

    private static String mDestJarName;

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

    static void zipJar(File jarFile, File sourceDir) {
        JarOutputStream jarStream = null
        FileOutputStream outputStream = null
        try {
            mDestJarName = jarFile.getCanonicalPath()
            outputStream = new FileOutputStream(jarFile)
            jarStream = new JarOutputStream(outputStream)
            zipJar(sourceDir, jarStream, null)
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            try {
                if (jarStream != null) jarStream.close()
                if (outputStream != null) outputStream.close()
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
    }

    static void zipJar(File sourceDir, JarOutputStream jarStream, String path) throws IOException {
        if (sourceDir.isDirectory()) {
            String[] dirList = sourceDir.list()
            String subPath = (path == null) ? "" : (path + sourceDir.getName() + '/')
            if (path != null) {
                JarEntry je = new JarEntry(subPath)
                je.setTime(sourceDir.lastModified())
                jarStream.putNextEntry(je)
                jarStream.flush()
                jarStream.closeEntry()
            }
            for (int i = 0; i < dirList.length; i++) {
                File f = new File(sourceDir, dirList[i])
                zipJar(f, jarStream, subPath)
            }
        } else {
            if (sourceDir.getCanonicalPath().equals(mDestJarName)) {
                return
            }
            FileInputStream fis = new FileInputStream(sourceDir)
            JarEntry entry = new JarEntry(path + sourceDir.getName())
            entry.setTime(sourceDir.lastModified())
            jarStream.putNextEntry(entry)
            int count
            byte[] buffer = new byte[2156]
            while ((count = fis.read(buffer)) != -1) {
                jarStream.write(buffer, 0, count)
            }
            jarStream.flush()
            jarStream.closeEntry()
        }
    }
}