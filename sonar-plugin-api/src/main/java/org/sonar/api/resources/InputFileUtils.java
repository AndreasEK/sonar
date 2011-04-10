/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.resources;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.List;

/**
 * @since 2.8
 */
public final class InputFileUtils {

  private InputFileUtils() {
    // only static methods
  }

  /**
   * @param inputFiles not nullable
   * @return not null collection
   */
  public static Collection<java.io.File> toFiles(Collection<InputFile> inputFiles) {
    Collection<java.io.File> files = Lists.newArrayList();
    for (InputFile inputFile : inputFiles) {
      files.add(inputFile.getFile());
    }
    return files;
  }

  /**
   * For internal and for testing purposes. Please use the FileSystem component to access files.
   */
  public static InputFile create(java.io.File basedir, java.io.File file) {
    String relativePath = getRelativePath(basedir, file);
    if (relativePath != null) {
      return create(basedir, relativePath);
    }
    return null;
  }

  /**
   * For internal and for testing purposes. Please use the FileSystem component to access files.
   */
  public static InputFile create(java.io.File basedir, String relativePath) {
    return new DefaultInputFile(basedir, relativePath);
  }

  static String getRelativePath(java.io.File basedir, java.io.File file) {
    List<String> stack = Lists.newArrayList(file.getName());
    java.io.File cursor = file.getParentFile();
    while (cursor != null) {
      if (basedir.equals(cursor)) {
        return StringUtils.join(stack, "/");
      }
      stack.add(0, cursor.getName());
      cursor = cursor.getParentFile();
    }
    return null;
  }

  static final class DefaultInputFile implements InputFile {
    private java.io.File basedir;
    private String relativePath;

    DefaultInputFile(java.io.File basedir, String relativePath) {
      this.basedir = basedir;
      this.relativePath = relativePath;
    }

    public java.io.File getFileBaseDir() {
      return basedir;
    }

    public java.io.File getFile() {
      return new java.io.File(basedir, relativePath);
    }

    public String getRelativePath() {
      return relativePath;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      DefaultInputFile that = (DefaultInputFile) o;
      return basedir.equals(that.basedir) && relativePath.equals(that.relativePath);

    }

    @Override
    public int hashCode() {
      int result = basedir.hashCode();
      result = 31 * result + relativePath.hashCode();
      return result;
    }

    @Override
    public String toString() {
      return String.format("%s -> %s", basedir.getAbsolutePath(), relativePath);
    }
  }
}