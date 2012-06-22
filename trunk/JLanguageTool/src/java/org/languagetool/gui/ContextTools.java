/* LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.gui;

import org.languagetool.tools.StringTools;

/**
 * Helper class to mark errors in text.
 */
public class ContextTools {

  private int contextSize = 40;
  private boolean escapeHtml = true;
  private String errorMarkerStart = "<b><font bgcolor=\"#ff8b8b\">";
  private String errorMarkerEnd = "</font></b>";

  public ContextTools() {
  }

  public String getContext(final int fromPos, final int toPos, String text) {
    text = text.replace('\n', ' ');
    // calculate context region:
    int startContent = fromPos - contextSize;
    String prefix = "...";
    String postfix = "...";
    String markerPrefix = "   ";
    if (startContent < 0) {
      prefix = "";
      markerPrefix = "";
      startContent = 0;
    }
    int endContent = toPos + contextSize;
    final int fileLen = text.length();
    if (endContent > fileLen) {
      postfix = "";
      endContent = fileLen;
    }
    // make "^" marker. inefficient but robust implementation:
    final StringBuilder marker = new StringBuilder();
    final int totalLen = fileLen + prefix.length();
    for (int i = 0; i < totalLen; i++) {
      if (i >= fromPos && i < toPos) {
        marker.append('^');
      } else {
        marker.append(' ');
      }
    }
    // now build context string plus marker:
    final StringBuilder sb = new StringBuilder();
    sb.append(prefix);
    sb.append(text.substring(startContent, endContent));
    final String markerStr = markerPrefix
        + marker.substring(startContent, endContent);
    sb.append(postfix);
    final int startMark = markerStr.indexOf('^');
    final int endMark = markerStr.lastIndexOf('^');
    String result = sb.toString();
    if (escapeHtml) {
      final String escapedErrorPart = StringTools.escapeHTML(result.substring(startMark, endMark + 1))
              .replace(" ", "&nbsp;");   // make sure whitespace errors are visible
      result = StringTools.escapeHTML(result.substring(0, startMark))
          + errorMarkerStart
          + escapedErrorPart
          + errorMarkerEnd + StringTools.escapeHTML(result.substring(endMark + 1));
    } else {
      result = result.substring(0, startMark) + errorMarkerStart
          + result.substring(startMark, endMark + 1) + errorMarkerEnd
          + result.substring(endMark + 1);
    }
    return result;
  }

  public void setErrorMarkerStart(String errorMarkerStart) {
    this.errorMarkerStart = errorMarkerStart;
  }

  public void setErrorMarkerEnd(String errorMarkerEnd) {
    this.errorMarkerEnd = errorMarkerEnd;
  }

  public void setContextSize(int contextSize) {
    this.contextSize = contextSize;
  }

  public void setEscapeHtml(boolean escapeHtml) {
    this.escapeHtml = escapeHtml;
  }
}
