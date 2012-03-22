/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
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
package org.sonar.api.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.sonar.api.utils.DateUtils;

import javax.annotation.Nullable;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * SonarSource license. This class aims to extract metadata but not to validate or - of course -
 * to generate license
 *
 * @since 2.15
 */
public final class License {
  private String product;
  private String organization;
  private String expirationDate;
  private String type;
  private String server;

  private License(Map<String, String> properties) {
    product = StringUtils.defaultString(properties.get("Product"), properties.get("Plugin"));
    organization = StringUtils.defaultString(properties.get("Organisation"), properties.get("Name"));
    expirationDate = StringUtils.defaultString(properties.get("Expiration"), properties.get("Expires"));
    type = properties.get("Type");
    server = properties.get("Server");
  }

  @Nullable
  public String getProduct() {
    return product;
  }

  @Nullable
  public String getOrganization() {
    return organization;
  }

  @Nullable
  public String getExpirationDateAsString() {
    return expirationDate;
  }

  @Nullable
  public Date getExpirationDate() {
    return DateUtils.parseDateQuietly(expirationDate);
  }

  public boolean isExpired() {
    return isExpired(new Date());
  }

  @VisibleForTesting
  boolean isExpired(Date now) {
    Date date = getExpirationDate();
    return date != null && !date.after(org.apache.commons.lang.time.DateUtils.truncate(now, Calendar.DATE));
  }

  @Nullable
  public String getType() {
    return type;
  }

  @Nullable
  public String getServer() {
    return server;
  }

  public static License readBase64(String base64) {
    return readPlainText(new String(Base64.decodeBase64(base64.getBytes())));
  }

  @VisibleForTesting
  static License readPlainText(String data) {
    Map<String, String> props = Maps.newHashMap();
    StringReader reader = new StringReader(data);
    try {
      List<String> lines = IOUtils.readLines(reader);
      for (String line : lines) {
        if (StringUtils.isNotBlank(line) && line.indexOf(':') > 0) {
          String key = StringUtils.substringBefore(line, ":");
          String value = StringUtils.substringAfter(line, ":");
          props.put(StringUtils.trimToEmpty(key), StringUtils.trimToEmpty(value));
        }
      }

    } catch (Exception e) {
      // silently ignore

    } finally {
      IOUtils.closeQuietly(reader);
    }
    return new License(props);
  }
}
