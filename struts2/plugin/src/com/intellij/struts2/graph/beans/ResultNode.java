/*
 * Copyright 2008 The authors
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.struts2.graph.beans;

import com.intellij.openapi.fileTypes.FileTypes;
import com.intellij.openapi.paths.PathReference;
import com.intellij.struts2.dom.struts.action.Result;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Represents {@link Result} element.
 *
 * @author Yann C&eacute;bron
 */
public class ResultNode extends BasicStrutsNode<Result> {

  private static final Icon UNKNOWN_RESULT_ICON = FileTypes.UNKNOWN.getIcon();

  public ResultNode(@NotNull final Result identifyingElement, @Nullable final String name) {
    super(identifyingElement, name);
  }

  @NotNull
  public Icon getIcon() {
    final Result result = getIdentifyingElement();
    if (!result.isValid()) {
      return UNKNOWN_RESULT_ICON;
    }

    final PathReference pathReference = result.getValue();
    if (pathReference == null) {
      return UNKNOWN_RESULT_ICON;
    }

    if (pathReference.resolve() == null) {
      return UNKNOWN_RESULT_ICON;
    }

    final Icon pathReferenceIcon = pathReference.getIcon();
    return pathReferenceIcon != null ? pathReferenceIcon : UNKNOWN_RESULT_ICON;
  }

  @NotNull
  public String getTooltip() {
    final PathReference pathReference = getIdentifyingElement().getValue();
    final String displayPath = pathReference != null ? pathReference.getPath() : "???";

    final TooltipBuilder builder = new TooltipBuilder();
    builder.addLine("Path", displayPath)
        .addLine("Type", getIdentifyingElement().getType().getStringValue());

    return builder.getTooltipText();
  }

}