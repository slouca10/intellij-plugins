// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.jetbrains.vuejs.lang.expr.psi.impl

import com.intellij.lang.Language
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSSuppressionHolder
import com.intellij.lang.javascript.psi.impl.JSElementImpl
import com.intellij.lang.javascript.psi.impl.JSEmbeddedContentImpl
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.tree.IElementType
import org.jetbrains.vuejs.lang.expr.VueJSLanguage
import org.jetbrains.vuejs.lang.expr.psi.VueJSEmbeddedExpression

class VueJSEmbeddedExpressionImpl(elementType: IElementType) : JSElementImpl(elementType), JSSuppressionHolder, VueJSEmbeddedExpression {

  override fun getLanguage(): Language {
    return VueJSLanguage.INSTANCE
  }

  override fun accept(visitor: PsiElementVisitor) {
    if (visitor is JSElementVisitor) {
      visitor.visitJSEmbeddedContent(this)
    }
    else {
      super.accept(visitor)
    }
  }

  override fun allowTopLevelThis(): Boolean {
    return true
  }

  override fun getQuoteChar(): Char? {
    return JSEmbeddedContentImpl.getQuoteChar(this)
  }
}