package org.gradle.doctoolchain.tasks.importConfluence;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.json.JSONException;

public class ImportFromConfluenceTask extends DefaultTask {

  private String pageId;

  private String confluenceUrl;

  private String username;

  private String password;

  private File htmlImportDir = getProject().file("build/doctoolchain/html");
  private File htmlImportDirPreprocessed = getProject().file("build/doctoolchain/htmlPreprocessed");
  private File adocImportDir = getProject().file("src");



  @TaskAction
  public void importFromConfluence () throws IOException, JSONException {

    if (pageId == null)
      throw new IllegalStateException("PageID not set, please use parameter --pageid to set a valid id");

    if (confluenceUrl == null)
      throw new IllegalStateException("Import URL not set, please use parameter --url to set a valid confluence url");

    FileUtils.deleteDirectory(htmlImportDir);
    FileUtils.deleteDirectory(htmlImportDirPreprocessed);
    FileUtils.deleteDirectory(adocImportDir);

    final ConfluenceRestApi restClient = new ConfluenceRestApi(confluenceUrl, username, password);

    Long pageIdAsLong = Long.parseLong(pageId);

    String contentLinked = "";
    String contentIncluded = "";

    List<Page> pages = restClient.getChildren(pageIdAsLong);
    for (Page next : pages) {
      getLogger().lifecycle("Exporting " + next.getId() + "-" + next.getTitle());
      next.save(htmlImportDir, htmlImportDirPreprocessed, adocImportDir);
      contentLinked += "link:" + next.getFileNameOutFileSuffix() + "[" + next.getTitle() + "]\n\n";
      contentIncluded += "include::" + next.getFileName() + "[]\n";
    }

    FileUtils.writeStringToFile(new File (adocImportDir, "index.adoc"), contentLinked, Charset.defaultCharset());
    FileUtils.writeStringToFile(new File (adocImportDir, "index_all.adoc"), contentIncluded, Charset.defaultCharset());

  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getConfluenceUrl() {
    return confluenceUrl;
  }

  @Option(option = "url", description = "Url to import documentation from")
  public void setConfluenceUrl(String confluenceUrl) {
    this.confluenceUrl = confluenceUrl;
  }

  @Option(option = "pageid", description = "The page id of confluence of the root page. This page and all direct children are imported")
  public void setPageId(String pageId) {
    this.pageId = pageId;
  }
}
