package training.lang

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.project.Project
import com.intellij.openapi.projectRoots.ProjectJdkTable
import com.intellij.openapi.projectRoots.Sdk
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.roots.ex.ProjectRootManagerEx
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel
import com.jetbrains.python.psi.LanguageLevel
import com.jetbrains.python.sdk.PyDetectedSdk
import com.jetbrains.python.sdk.PythonSdkType
import com.jetbrains.python.sdk.flavors.PythonSdkFlavor
import com.jetbrains.python.sdk.flavors.VirtualEnvSdkFlavor
import training.learn.exceptons.InvalidSdkException
import training.learn.exceptons.NoSdkException
import java.util.*

/**
 * @author Sergey Karashevich
 */
class PythonLangSupport : AbstractLangSupport() {
  
  override fun createProject(projectName: String, projectToClose: Project?): Project? {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun importLearnProject(): Project? {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  private val acceptableLanguages = setOf("python", "html")
  override fun acceptLang(ext: String) = acceptableLanguages.contains(ext.toLowerCase())

  override val FILE_EXTENSION: String
    get() = "py"


  override fun applyProjectSdk(project: Project): Unit {
    val pySdk: Sdk = findOrCreatePySdk()

    CommandProcessor.getInstance().executeCommand(project, {
      ApplicationManager.getApplication().runWriteAction {

        val rootManager = ProjectRootManagerEx.getInstanceEx(project)
        rootManager.projectSdk = pySdk
      }
    }, null, null)
  }

  private fun findOrCreatePySdk(): Sdk {
    //find registered python SDKs
    var pySdk: Sdk? = ProjectJdkTable.getInstance().allJdks.find { sdk -> sdk.sdkType is PythonSdkType && isNoOlderThan27(sdk)}

    //register first detected SDK
    if (pySdk == null) {
      val sdkList: List<Sdk> = detectPySdks()
      pySdk = sdkList.firstOrNull() ?: throw NoSdkException("Python")
      ApplicationManager.getApplication().runWriteAction { ProjectJdkTable.getInstance().addJdk(pySdk!!) }
    }
    return pySdk
  }

  override fun applyToProjectAfterConfigure(): (Project) -> Unit = {}

  override fun checkSdk(sdk: Sdk?) {
    checkSdkPresence(sdk)
    if (sdk!!.sdkType is PythonSdkType) {
      if (!isNoOlderThan27(sdk)) throw InvalidSdkException("Please use at least JDK 1.6 or IDEA SDK with corresponding JDK")
    }
    else throw NoSdkException()
  }

  fun addToSdkList() {
  }

  //detect sdk with version 2.7 and higher
  fun detectPySdks(): List<Sdk> {
    val model = ProjectSdksModel()
    model.reset(null)
    val sdkHomes = ArrayList<String>()
    sdkHomes.addAll(VirtualEnvSdkFlavor.INSTANCE.suggestHomePaths())
    PythonSdkFlavor.getApplicableFlavors()
        .filter { it !is VirtualEnvSdkFlavor }
        .forEach { sdkHomes.addAll(it.suggestHomePaths()) }
    Collections.sort(sdkHomes)
    return SdkConfigurationUtil.filterExistingPaths(PythonSdkType.getInstance(), sdkHomes, model.sdks).mapTo(ArrayList(), ::PyDetectedSdk).filter { sdk -> isNoOlderThan27(sdk) }
  }

  fun isNoOlderThan27(sdk: Sdk) = PythonSdkFlavor.getFlavor(sdk)!!.getLanguageLevel(sdk).isAtLeast(LanguageLevel.PYTHON27)

}
