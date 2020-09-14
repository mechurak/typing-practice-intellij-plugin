package com.shimnssso.intellij.plugin.typing

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate
import com.intellij.codeInsight.hint.HintManager
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile

class MyTypedHandler : TypedHandlerDelegate() {
    enum class Status {
        ENABLED,
        DISABLED
    }

    companion object {
        var status: Status = Status.DISABLED

        private const val SHOW_INTERVAL = 2000L
        private const val CHAR_PER_WORD = 5.0f
        private const val ONE_MIN_IN_MS = 60 * 1000
    }

    private var typeCount: Int = 0
    private var startTime: Long = 0
    private var prevTime: Long = 0

    override fun charTyped(c: Char, project: Project, editor: Editor, file: PsiFile): Result {
        if (status == Status.DISABLED) {
            return Result.DEFAULT
        }

        typeCount++
        val curTime = System.currentTimeMillis()
        if (startTime == 0L) {
            startTime = curTime
            Notifications.Bus.notify(
                Notification("TypingPractice", "TypingPractice", "Start!!!", NotificationType.INFORMATION)
            )
            return Result.STOP
        }

        if (curTime - prevTime < SHOW_INTERVAL) {
            return Result.DEFAULT
        }
        val wpm: Int = (typeCount / CHAR_PER_WORD * ONE_MIN_IN_MS / (curTime - startTime)).toInt()
        Notifications.Bus.notify(
            Notification(
                "TypingPractice",
                "TypingPractice",
                "wpm: $wpm, typeCount: $typeCount",
                NotificationType.INFORMATION
            )
        )
        prevTime = curTime
        HintManager.getInstance().showErrorHint(editor, "wpm: $wpm")

        return Result.STOP
    }
}
