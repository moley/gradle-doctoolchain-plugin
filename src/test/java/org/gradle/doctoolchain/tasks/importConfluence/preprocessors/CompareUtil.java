package org.gradle.doctoolchain.tasks.importConfluence.preprocessors;

public class CompareUtil {

  public static String removeWhitespaces (String source) {
    return source.replaceAll("\\s+","");
  }
}
