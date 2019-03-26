package cdn.youga.pldroid.asm;

import org.gradle.api.Project;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author: YougaKingWu@gmail.com
 * @created on: 2019/03/26 15:38
 * @description:
 */
public class MediaPlayerVisitor extends ClassVisitor {

    private Project mProject;

    public MediaPlayerVisitor(ClassVisitor visitor, Project project) {
        super(Opcodes.ASM5, visitor);
        mProject = project;
    }

    public void visit(int version, int access, String name, String signature,
                      String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        mProject.getLogger().error(name + "\t" + desc);
        if ("a".equals(name) && "(Ljava/lang/String;Ljava/util/Map;)V".equals(desc)) {
            return new SetDataSourceMethodVisitor(mv);
        }
        return mv;
    }
}
