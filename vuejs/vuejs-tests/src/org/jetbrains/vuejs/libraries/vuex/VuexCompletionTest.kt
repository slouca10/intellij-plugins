// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.vuejs.libraries.vuex

import com.intellij.openapi.application.PathManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.psi.PsiDocumentManager
import com.intellij.testFramework.UsefulTestCase
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import junit.framework.TestCase
import org.jetbrains.vuejs.lang.createPackageJsonWithVueDependency
import org.jetbrains.vuejs.lang.renderLookupItems
import java.io.File

class VuexCompletionTest : BasePlatformTestCase() {

  override fun getTestDataPath(): String = PathManager.getHomePath() + "/contrib/vuejs/vuejs-tests/testData/vuex/completion"

  fun testBasicGettersCompletion() {
    createPackageJsonWithVueDependency(myFixture, "\"vuex\": \"^3.0.1\"")
    myFixture.configureByFiles("basicGetter.vue", "basicGetter.js")
    myFixture.completeBasic()
    assertContainsElements(myFixture.lookupElementStrings!!, "getter1", "getter_2")
  }

  fun testBasicMutationsCompletion() {
    createPackageJsonWithVueDependency(myFixture, "\"vuex\": \"^3.0.1\"")
    myFixture.configureByFiles("basicMutations.vue", "basicMutations.js")
    myFixture.completeBasic()
    assertContainsElements(myFixture.lookupElementStrings!!, "mutation1")
  }

  fun testBasicMutations2Completion() {
    createPackageJsonWithVueDependency(myFixture, "\"vuex\": \"^3.0.1\"")
    myFixture.configureByFiles("basicMutations2.js")
    myFixture.completeBasic()
    assertContainsElements(myFixture.lookupElementStrings!!, "mutation1")
  }

  fun testBasicActionsCompletion() {
    createPackageJsonWithVueDependency(myFixture, "\"vuex\": \"^3.0.1\"")
    myFixture.configureByFiles("basicActions.vue", "basicActions.js")
    myFixture.completeBasic()
    assertContainsElements(myFixture.lookupElementStrings!!, "action1", "action_2")
  }

  fun testVuexActions2Completion() {
    createPackageJsonWithVueDependency(myFixture, "\"vuex\": \"^3.0.1\"")
    myFixture.configureByFiles("basicActions2.vue", "basicActions2.ts")
    myFixture.completeBasic()
    assertContainsElements(myFixture.lookupElementStrings!!, "action1", "action_2")
  }

  fun testStorefrontDirectGetter() {
    myFixture.configureStorefront()
    doItemsTest(0, "foo() { return this.\$store.getters['<caret>']", section = "computed")
    doItemsTest(0, "foo() { return this.store.getters['<caret>']", section = "computed")
    doItemsTest(1, "foo() { return this.\$store.getters['cart/<caret>getCoupon']", section = "computed")
    doItemsTest(2, "foo() { return this.\$store.getters.<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(3, "foo() { return this.\$store.getters[<caret>]", section = "computed", strict = false, renderPriority = true)

    doTypingTest("foo() { return this.\$store.getters['<caret>foo/bar']",
                 "cartCoupon\t", "this.\$store.getters['cart/getCoupon']", section = "computed")
  }

  fun testStorefrontDirectState() {
    myFixture.configureStorefront()
    doItemsTest(0, "foo() { return this.\$store.state.<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(1, "foo() { return this.\$store.state.category.<caret>", section = "computed", strict = false, renderPriority = true)
  }

  fun testStorefrontDirectDispatch() {
    myFixture.configureStorefront()
    doItemsTest(0, "foo() { this.\$store.dispatch('<caret>', code) }", section = "methods")
    doItemsTest(0, "foo() { this.store.dispatch('<caret>', code) }", section = "methods")
    doItemsTest(0, "foo() { this.\$store.dispatch('<caret>cart/applyCoupon', code) }", section = "methods")
    doItemsTest(0, "foo() { this.store.dispatch('<caret>cart/applyCoupon', code) }", section = "methods")
    doItemsTest(1, "foo() { this.\$store.dispatch('cart/a<caret>', code) }", section = "methods")
    doItemsTest(1, "foo() { this.store.dispatch('cart/a<caret>', code) }", section = "methods")
    doItemsTest(2, "foo() { this.\$store.dispatch(<caret>, code) }", section = "methods", strict = false, renderPriority = true)
    doItemsTest(2, "foo() { this.store.dispatch(<caret>, code) }", section = "methods", strict = false, renderPriority = true)

    doTypingTest("foo() { return this.\$store.dispatch('<caret>foo/bar', code)",
                 "cartCoupon\t", "('cart/applyCoupon', code)", section = "methods")
  }

  fun testStorefrontDirectCommit() {
    myFixture.configureStorefront()
    doItemsTest(0, "foo() { this.\$store.commit('<caret>', code) }", section = "methods")
    doItemsTest(0, "foo() { this.store.commit('<caret>', code) }", section = "methods")
    doItemsTest(0, "foo() { this.\$store.commit('<caret>cart/breadcrumbs/set', code) }", section = "methods")
    doItemsTest(0, "foo() { this.store.commit('<caret>cart/breadcrumbs/set', code) }", section = "methods")
    doItemsTest(1, "foo() { this.\$store.commit('cart/<caret>', code) }", section = "methods")
    doItemsTest(1, "foo() { this.store.commit('cart/<caret>', code) }", section = "methods")
    doItemsTest(2, "foo() { this.\$store.commit(<caret>, code) }", section = "methods", strict = false, renderPriority = true)
    doItemsTest(2, "foo() { this.store.commit(<caret>, code) }", section = "methods", strict = false, renderPriority = true)

    doTypingTest("foo() { return this.\$store.commit('<caret>foo/bar', code)",
                 "cartset\n", "('cart/breadcrumbs/setfoo/bar', code)", section = "methods")
    doTypingTest("foo() { return this.\$store.commit('<caret>foo/bar', code)",
                 "cartset\t", "('cart/breadcrumbs/set', code)", section = "methods")
  }

  fun testStorefrontMapGetters() {
    myFixture.configureStorefront()
    doItemsTest(0, "...mapGetters(<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(1, "...mapGetters('<caret>'", section = "computed")

    doItemsTest(2, "...mapGetters([<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(3, "...mapGetters(['<caret>'", section = "computed")
    doItemsTest(4, "...mapGetters('cart',[<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(5, "...mapGetters('cart',['<caret>'", section = "computed")

    doItemsTest(2, "...mapGetters({foo: <caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(3, "...mapGetters({foo: '<caret>'", section = "computed")
    doItemsTest(4, "...mapGetters('cart',{foo: <caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(5, "...mapGetters('cart',{foo: '<caret>'", section = "computed")

    doItemsTest(4, "...mapGetters([<caret>",
                additionalContent = namespacedHandlersCode, section = "computed", strict = false, renderPriority = true)
    doItemsTest(5, "...mapGetters(['<caret>'",
                additionalContent = namespacedHandlersCode, section = "computed")

    doItemsTest(4, "...mapGetters({foo: <caret>", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(5, "...mapGetters({foo: '<caret>'", section = "computed",
                additionalContent = namespacedHandlersCode)

    doItemsTest(6, "...categoryModule.mapGetters([<caret>", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(7, "...categoryModule.mapGetters(['<caret>'", section = "computed",
                additionalContent = namespacedHandlersCode)

    doItemsTest(6, "...categoryModule.mapGetters({foo: <caret>", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(7, "...categoryModule.mapGetters({foo: '<caret>'", section = "computed",
                additionalContent = namespacedHandlersCode)

    doItemsTest(2, "@Getter(<caret>)", strict = false, renderPriority = true)
    doItemsTest(3, "@Getter('<caret>')")
    doItemsTest(5, "@Getter('cart/<caret>')")

    doItemsTest(4, "@cartModule.Getter(<caret>)",
                additionalContent = namespacedDecoratorsCode, strict = false, renderPriority = true)
    doItemsTest(5, "@cartModule.Getter('<caret>')",
                additionalContent = namespacedDecoratorsCode)

    doItemsTest(8,"...mapState({foo(state, getters) { return getters.<caret> }", section = "computed", strict = false, renderPriority = true)
  }

  fun testStorefrontMapState() {
    myFixture.configureStorefront()
    doItemsTest(0, "...mapState(<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(1, "...mapState('<caret>'", section = "computed")

    doItemsTest(2, "...mapState([<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(3, "...mapState(['<caret>'", section = "computed")
    doItemsTest(4, "...mapState('cart',[<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(5, "...mapState('cart',['<caret>'", section = "computed")

    doItemsTest(2, "...mapState({foo: <caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(3, "...mapState({foo: '<caret>'", section = "computed")
    doItemsTest(4, "...mapState('cart',{foo: <caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(5, "...mapState('cart',{foo: '<caret>'", section = "computed")

    doItemsTest(6,"...mapState({foo: state => state.<caret>", section = "computed", strict = false, renderPriority = true)
    doItemsTest(7,"...mapState({foo(state) { return state.cart.<caret> }", section = "computed", strict = false, renderPriority = true)

    doItemsTest(7,"...mapState('cart',{foo: state => state.<caret>",
                section = "computed", strict = false, renderPriority = true)
    doItemsTest(8,"...mapState('cart',{foo(state) { return state.breadcrumbs.<caret> }",
                section = "computed", strict = false, renderPriority = true)

    doItemsTest(4, "...mapState([<caret>",
                additionalContent = namespacedHandlersCode, section = "computed", strict = false, renderPriority = true)
    doItemsTest(5, "...mapState(['<caret>'",
                additionalContent = namespacedHandlersCode, section = "computed")

    doItemsTest(4, "...mapState({foo: <caret>", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(5, "...mapState({foo: '<caret>'", section = "computed",
                additionalContent = namespacedHandlersCode)

    doItemsTest(7,"...mapState({foo: state => state.<caret>", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(8,"...mapState({foo(state) { return state.breadcrumbs.<caret> }", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)

    doItemsTest(9, "...categoryModule.mapState([<caret>", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(10, "...categoryModule.mapState(['<caret>'", section = "computed",
                additionalContent = namespacedHandlersCode)

    doItemsTest(9, "...categoryModule.mapState({foo: <caret>", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(10, "...categoryModule.mapState({foo: '<caret>'", section = "computed",
                additionalContent = namespacedHandlersCode)

    doItemsTest(11,"...categoryModule.mapState({foo: state => state.<caret>", section = "computed",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)

    doItemsTest(2, "@State(<caret>)", strict = false, renderPriority = true)
    doItemsTest(3, "@State('<caret>')")
    doItemsTest(5, "@State('cart/<caret>')")
    doItemsTest(6, "@State(state => state.<caret>", strict = false, renderPriority = true)

    doItemsTest(4, "@cartModule.State(<caret>)",
                additionalContent = namespacedDecoratorsCode, strict = false, renderPriority = true)
    doItemsTest(5, "@cartModule.State('<caret>')",
                additionalContent = namespacedDecoratorsCode)
    doItemsTest(7, "@cartModule.State(state => state.<caret>",
                additionalContent = namespacedDecoratorsCode, strict = false, renderPriority = true)

  }

  fun testStorefrontMapActions() {
    myFixture.configureStorefront()
    doItemsTest(0, "...mapActions(<caret>", section = "methods", strict = false, renderPriority = true)
    doItemsTest(1, "...mapActions('<caret>'", section = "methods")

    doItemsTest(2, "...mapActions([<caret>", section = "methods", strict = false, renderPriority = true)
    doItemsTest(3, "...mapActions(['<caret>'", section = "methods")
    doItemsTest(4, "...mapActions('cart',[<caret>", section = "methods", strict = false, renderPriority = true)
    doItemsTest(5, "...mapActions('cart',['<caret>'", section = "methods")

    doItemsTest(2,"...mapActions({foo(dispatch) { dispatch(<caret>) }", section = "methods", strict = false, renderPriority = true)
    doItemsTest(3,"...mapActions({foo: dispatch => dispatch('<caret>'", section = "methods")
    doItemsTest(4,"...mapActions('cart',{foo(dispatch) { dispatch(<caret> } }",
                section = "methods", strict = false, renderPriority = true)
    doItemsTest(5,"...mapActions('cart',{foo: dispatch => dispatch('<caret>'",
                section = "methods")

    doItemsTest(4, "...mapActions([<caret>",
                additionalContent = namespacedHandlersCode, section = "methods", strict = false, renderPriority = true)
    doItemsTest(5, "...mapActions(['<caret>'",
                additionalContent = namespacedHandlersCode, section = "methods")

    doItemsTest(4,"...mapActions({foo(dispatch) { dispatch(<caret> }", section = "methods",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(5,"...mapActions({foo: dispatch => dispatch('<caret>'", section = "methods",
                additionalContent = namespacedHandlersCode)

    doItemsTest(6, "...categoryModule.mapActions([<caret>", section = "methods",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(7, "...categoryModule.mapActions(['<caret>'", section = "methods",
                additionalContent = namespacedHandlersCode)

    doItemsTest(6, "...categoryModule.mapActions({foo(dispatch) { dispatch(<caret> }", section = "methods",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(7, "...categoryModule.mapActions({foo: dispatch => dispatch('<caret>", section = "methods",
                additionalContent = namespacedHandlersCode)

    doItemsTest(2, "@Action(<caret>)", strict = false, renderPriority = true)
    doItemsTest(3, "@Action('<caret>')")
    doItemsTest(5, "@Action('cart/<caret>')")

    doItemsTest(4, "@cartModule.Action(<caret>)",
                additionalContent = namespacedDecoratorsCode, strict = false, renderPriority = true)
    doItemsTest(5, "@cartModule.Action('<caret>')",
                additionalContent = namespacedDecoratorsCode)
  }
  
  fun testStorefrontMapMutations() {
    myFixture.configureStorefront()
    doItemsTest(0, "...mapMutations(<caret>", section = "methods", strict = false, renderPriority = true)
    doItemsTest(1, "...mapMutations('<caret>'", section = "methods")

    doItemsTest(2, "...mapMutations([<caret>", section = "methods", strict = false, renderPriority = true)
    doItemsTest(3, "...mapMutations(['<caret>'", section = "methods")
    doItemsTest(4, "...mapMutations('cart',[<caret>", section = "methods", strict = false, renderPriority = true)
    doItemsTest(5, "...mapMutations('cart',['<caret>'", section = "methods")

    doItemsTest(2,"...mapMutations({foo(commit) { commit(<caret>) }", section = "methods", strict = false, renderPriority = true)
    doItemsTest(3,"...mapMutations({foo: commit => commit('<caret>'", section = "methods")
    doItemsTest(4,"...mapMutations('cart',{foo(commit) { commit(<caret> } }",
                section = "methods", strict = false, renderPriority = true)
    doItemsTest(5,"...mapMutations('cart',{foo: commit => commit('<caret>'",
                section = "methods")

    doItemsTest(4, "...mapMutations([<caret>",
                additionalContent = namespacedHandlersCode, section = "methods", strict = false, renderPriority = true)
    doItemsTest(5, "...mapMutations(['<caret>'",
                additionalContent = namespacedHandlersCode, section = "methods")

    doItemsTest(4,"...mapMutations({foo(commit) { commit(<caret> }", section = "methods",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(5,"...mapMutations({foo: commit => commit('<caret>'", section = "methods",
                additionalContent = namespacedHandlersCode)

    doItemsTest(6, "...categoryModule.mapMutations([<caret>", section = "methods",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(7, "...categoryModule.mapMutations(['<caret>'", section = "methods",
                additionalContent = namespacedHandlersCode)

    doItemsTest(6, "...categoryModule.mapMutations({foo(commit) { commit(<caret> }", section = "methods",
                additionalContent = namespacedHandlersCode, strict = false, renderPriority = true)
    doItemsTest(7, "...categoryModule.mapMutations({foo: commit => commit('<caret>", section = "methods",
                additionalContent = namespacedHandlersCode)

    doItemsTest(2, "@Mutation(<caret>)", strict = false, renderPriority = true)
    doItemsTest(3, "@Mutation('<caret>')")
    doItemsTest(5, "@Mutation('cart/<caret>')")

    doItemsTest(4, "@cartModule.Mutation(<caret>)",
                additionalContent = namespacedDecoratorsCode, strict = false, renderPriority = true)
    doItemsTest(5, "@cartModule.Mutation('<caret>')",
                additionalContent = namespacedDecoratorsCode)
  }

  private val namespacedHandlersCode = """
    const {mapState, mapActions, mapGetters, mapMutations} = createNamespacedHelpers('cart')
    const categoryModule = createNamespacedHelpers('category')
  """

  private val namespacedDecoratorsCode = "const cartModule = namespace('cart')"

  private fun doTypingTest(content: String, toType: String, expectedContents: String, section: String? = null, checkJS: Boolean = true,
                           additionalContent: String = "") {
    createFile(content, false, section, additionalContent)
    checkTyping(toType, expectedContents)
    if (checkJS) {
      createFile(content, true, section, additionalContent)
      checkTyping(toType, expectedContents)
    }
  }

  private fun checkTyping(toType: String, expectedContents: String) {
    myFixture.completeBasic()
    myFixture.type(toType)
    PsiDocumentManager.getInstance(project).commitAllDocuments()
    TestCase.assertEquals(expectedContents, myFixture.file.findElementAt(myFixture.caretOffset)?.context?.context?.text)
  }

  private fun doItemsTest(id: Int, content: String, section: String? = null, strict: Boolean = true, renderType: Boolean = true,
                          renderPriority: Boolean = false, checkJS: Boolean = true, additionalContent: String = "") {
    createFile(content, false, section, additionalContent)
    checkItems(id, strict, renderType, renderPriority)
    if (checkJS) {
      createFile(content, true, section, additionalContent)
      checkItems(id, strict, renderType, renderPriority)
    }
  }

  private fun createFile(content: String, js: Boolean, section: String?, additionalContent: String) {
    val fileContents = """
      import {mapActions, mapGetters, mapMutations, mapState} from 'vuex'
      import {rootStore} from "aaa"
      
      ${additionalContent}
      
      export default {
        ${if (section != null) "$section: {" else ""}
           ${content}
        ${if (section != null) "}" else ""}
      }""".trimIndent()
    myFixture.configureByText("${getTestName(true)}.${if (js) "js" else "ts"}", fileContents)
  }

  private fun checkItems(id: Int, strict: Boolean, renderType: Boolean, renderPriority: Boolean) {
    myFixture.completeBasic()
    var checkFileName: String
    checkFileName = "gold/${myFixture.file.name}.${id}.txt"
    if (!File("$testDataPath/$checkFileName").exists()) {
      checkFileName = "gold/${FileUtil.getNameWithoutExtension(myFixture.file.name)}.${id}.txt"
      FileUtil.createIfDoesntExist(File("$testDataPath/$checkFileName"))
    }
    myFixture.renderLookupItems(renderPriority, renderType)
      .let { list ->
        if (strict) {
          myFixture.configureByText("out.txt", list.sorted().joinToString("\n") + "\n")
          myFixture.checkResultByFile(checkFileName, true)
        }
        else {
          val items = myFixture.configureByFile(checkFileName).text.split('\n').filter { !it.isEmpty() }
          UsefulTestCase.assertContainsElements(list, items)
        }
      }
  }
}
