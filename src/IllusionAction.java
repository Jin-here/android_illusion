import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import java.util.*;
import java.lang.*;
import java.io.*;

public class IllusionAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        /*String text = Messages.showInputDialog(project,
                "What is your name?",
                "Input your name",
                Messages.getQuestionIcon());
        Messages.showMessageDialog(project,
                "Hello, " + text + "!\n I am glad to see you.",
                "Information",
                Messages.getInformationIcon());*/

        String layoutFileName = "main_activity";
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(getLayoutFilePath(project, layoutFileName));
        PsiFile layoutPsiFile = PsiManager.getInstance(project).findFile(virtualFile);
        XmlFile xmlFile = (XmlFile) layoutPsiFile;
        XmlTag child = xmlFile.getDocument().getRootTag();

        List<ViewBean> list = new ArrayList<ViewBean>();

        proChild(list, child);

        Messages.showMessageDialog(project,
                list.toString(),
                "Information",
                Messages.getInformationIcon());
    }

    private void proChild(List<ViewBean> list, XmlTag root) {
        String rawViewName = root.getName();
        String rawId = root.getAttribute("android:id").getValue();
        String viewId = getId(rawId);
        String viewName = getViewName(rawViewName);

        list.add(new ViewBean(viewName, viewId));

        XmlTag[] children = root.getSubTags();
        if (children != null && children.length > 0) {
            for (XmlTag child : children) {
                proChild(list, child);
            }
        }
    }

    private String getViewName(String rawViewName) {
        String[] split = rawViewName.split("\\.");
        if (split.length == 0) {
            return rawViewName;
        }
        return split[split.length - 1];
    }

    private String getId(String rawId) {
        return rawId.split("/")[1];
    }

    private PsiFile getManifestFile(Project project) {
        String path = project.getBasePath() + File.separator +
                "app" + File.separator +
                "src" + File.separator +
                "main" + File.separator +
                "AndroidManifest.xml";
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByPath(path);
        if (virtualFile == null) {
            return null;
        }
        return PsiManager.getInstance(project).findFile(virtualFile);
    }

    public String getAppPackageName(Project project) {
        PsiFile manifestFile = getManifestFile(project);
        XmlDocument xml = (XmlDocument) manifestFile.getFirstChild();
        return xml.getRootTag().getAttribute("package").getValue();
    }

    public VirtualFile getAppSrcDir(Project project) {
        String path = project.getBasePath() + File.separator +
                "app" + File.separator +
                "src" + File.separator +
                "main" + File.separator +
                "java" + File.separator +
                getAppPackageName(project).replace(".", File.separator);
        return LocalFileSystem.getInstance().findFileByPath(path);
    }

    public VirtualFile getAppResDir(Project project) {
        String path = project.getBasePath() + File.separator +
                "app" + File.separator +
                "src" + File.separator +
                "main" + File.separator +
                "res".replace(".", File.separator);
        return LocalFileSystem.getInstance().findFileByPath(path);
    }

    public String getLayoutFilePath(Project project, String fileName) {
        return getAppResDir(project).getPath() + File.separator +
                "layout" + File.separator +
                fileName + ".xml";
    }

    private void createDir(Project project, String dirName) {
        /*VirtualFile baseDir = getAppPackageBaseDir(project);

        // 判断根目录下是否有db文件夹
        VirtualFile dbDir = baseDir.findChild(dirName);
        if(dbDir == null) {
            try {
                dbDir = baseDir.createChildDirectory(null, dirName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }
}