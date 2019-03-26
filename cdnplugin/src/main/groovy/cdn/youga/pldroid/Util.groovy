package cdn.youga.pldroid

import java.util.jar.JarEntry
import java.util.jar.JarFile

class Util {

    static boolean isPldroidJar(File file) {
        JarFile jarFile = new JarFile(file)
        Enumeration<JarEntry> enumeration = jarFile.entries()

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = enumeration.nextElement()
            if (jarEntry.directory) {
                continue
            }
            String entryName = jarEntry.getName()

            if (entryName == "com/qiniu/qplayer/mediaEngine/MediaPlayer.class") {
                return true
            }
        }
        jarFile.close()
        return false
    }

    static boolean isMediaPlayerClass(String name) {
        //只处理需要的class文件
        return "com/qiniu/qplayer/mediaEngine/MediaPlayer.class" == name
    }
}