package com.shimnssso.intellij.plugin.typing

import com.intellij.diff.DiffContentFactory
import com.intellij.diff.DiffDialogHints
import com.intellij.diff.DiffManager
import com.intellij.diff.DiffRequestFactory
import com.intellij.diff.actions.impl.MutableDiffRequestChain
import com.intellij.diff.contents.DiffContent
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.WindowWrapper
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.Consumer
import com.intellij.util.ui.UIUtil
import java.io.File
import java.io.IOException

class PracticeAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val vf: VirtualFile? = e.getData(PlatformDataKeys.VIRTUAL_FILE)
        if (vf == null) {
            System.err.println("Failed to get VirtualFile from AnActionEvent")
            return
        }

        val path = vf.path
        val tempPath = path + "_temp.txt"
        val file = File(tempPath)
        try {
            val success = file.createNewFile()
            println("tempPath: $tempPath, success: $success")
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }

        val vf2: VirtualFile? = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
        if (vf2 == null) {
            System.err.println("Failed to get VirtualFile from $file")
            return
        }
        Notifications.Bus.notify(
            Notification("TypingPractice", "TypingPractice", "ActionPerformed!!!", NotificationType.INFORMATION)
        )

        // Reference: https://github.com/JetBrains/intellij-community/blob/1febe662a8f9a86621279d30bc61ae6cacae5ef5/platform/diff-impl/src/com/intellij/diff/actions/BaseShowDiffAction.java#L69
        val project: Project? = e.project
        val contentFactory: DiffContentFactory = DiffContentFactory.getInstance()
        val requestFactory: DiffRequestFactory = DiffRequestFactory.getInstance()

        val contest1: DiffContent = contentFactory.create(project, vf)
        val contest2: DiffContent = contentFactory.create(project, vf2)

        val chain = MutableDiffRequestChain(contest1, contest2)
        chain.windowTitle = requestFactory.getTitle(vf, vf2)
        chain.title1 = requestFactory.getContentTitle(vf)
        chain.title2 = requestFactory.getContentTitle(vf2)

        val runnable = Runnable {
            MyTypedHandler.status = MyTypedHandler.Status.DISABLED
            val success = file.delete()
            println("closed!!, delete: $success")
        }
        val consumer = Consumer<WindowWrapper> {
            UIUtil.runWhenWindowClosed(it.window, runnable)
        }

        val hint = DiffDialogHints(null, null, consumer)
        DiffManager.getInstance().showDiff(project, chain, hint)
        MyTypedHandler.status = MyTypedHandler.Status.ENABLED
    }
}
