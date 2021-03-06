package com.cn.ric;

import com.cn.ric.entity.StructGenerateResult;
import com.cn.ric.util.BuilderUtil;
import com.cn.ric.util.StructUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @program: GoBuilderGeneratorPlugin
 * @description:
 * @author: richen
 * @create: 2020-08-14 15:23
 **/
public class GoBuilderGeneratorAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        final Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (null == editor) {
            return;
        }

        Document document = editor.getDocument();
        String extension = Objects.requireNonNull(FileDocumentManager.getInstance().getFile(document)).getExtension();
        if (!(extension != null && extension.toLowerCase().equals("go"))){
            return;
        }
        if (!document.isWritable()) {
            return;
        }

        String selectedText = editor.getSelectionModel().getSelectedText();

        StructGenerateResult structGenerateResult = StructUtil.generateStruct(selectedText);
        if (!StringUtils.isBlank(structGenerateResult.error)) {
            Messages.showErrorDialog(project, structGenerateResult.error, "Generate Failed");
            return;
        }

        final String builderPatternCode = BuilderUtil.generateBuilderPatternCode(structGenerateResult.structEntityList);

        final int textLength = editor.getDocument().getTextLength();


//        new WriteCommandAction(project) {
//            @Override
//            protected void run(@NotNull Result result) {
//                editor.getDocument().insertString(textLength, builderPatternCode);
//                editor.getCaretModel().moveToOffset(textLength + 2);
//                editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
//            }
//        }.execute();

        WriteCommandAction.runWriteCommandAction(project, () ->  {
            editor.getDocument().insertString(textLength, builderPatternCode);
            editor.getCaretModel().moveToOffset(textLength + 2);
            editor.getScrollingModel().scrollToCaret(ScrollType.CENTER_UP);
        });
    }
}
