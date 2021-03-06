// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package training.ui

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.showYesNoDialog
import com.intellij.ui.layout.*
import training.lang.LangManager
import training.learn.LearnBundle
import training.ui.welcomeScreen.recentProjects.actionGroups.GroupManager
import training.util.clearTrainingProgress
import training.util.resetPrimaryLanguage
import javax.swing.DefaultComboBoxModel

class FeaturesTrainerSettingsPanel : BoundConfigurable(LearnBundle.message("learn.options.panel.name"), null) {
  override fun createPanel(): DialogPanel = panel {
    row {
      checkBox(LearnBundle.message("learn.option.show.list.on.welcome.frame"),
               { GroupManager.instance.showTutorialsOnWelcomeFrame },
               { GroupManager.instance.showTutorialsOnWelcomeFrame = it })
    }
    val languagesExtensions = LangManager.getInstance().supportedLanguagesExtensions
    if (languagesExtensions.isNotEmpty()) {
      row {
        label(LearnBundle.message("learn.option.main.language"))
        val options = languagesExtensions.map { it.language }.toTypedArray()
        comboBox(DefaultComboBoxModel<String>(options), {
          LangManager.getInstance().state.languageName?.takeIf { options.contains(it) } ?: options[0]
        }, { language ->
          val chosen = LangManager.getInstance().supportedLanguagesExtensions.first { it.language == language }
          resetPrimaryLanguage(chosen.instance)
        })
      }
    }
    row {
      link(LearnBundle.message("learn.option.reset.progress")) {
        if (showYesNoDialog(LearnBundle.message("learn.option.reset.progress.dialog"), LearnBundle.message("learn.option.reset.progress.confirm"), null)) {
          clearTrainingProgress()
        }
      }
    }
  }
}
