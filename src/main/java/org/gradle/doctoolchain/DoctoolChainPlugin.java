package org.gradle.doctoolchain;

import groovy.lang.Closure;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.tasks.Exec;
import org.gradle.doctoolchain.tasks.importConfluence.ImportFromConfluenceTask;

/**
 * Wrapper around doctoolchain
 */
public class DoctoolChainPlugin implements Plugin<Project> {

  public final static String GROUP = "doctoolchain";

  @java.lang.Override
  public void apply(Project project) {

    final String user = System.getProperty("confluence.user");
    final String password = System.getProperty("confluence.plainPassword");

    if (user == null)
      throw new IllegalStateException("Confluence user is not set, use system property confluence.user");

    if (password == null)
      throw new IllegalStateException("Confluence password is not set, use system property confluence.plainPassword");


    createForwardTask(project, "generatePDF", "generate documentation to PDF", user, password);
    createForwardTask(project, "generateHTML", "generate documentation to HTML", user, password);
    createForwardTask(project, "publishToConfluence", "publishes docu to confluence", user, password);

    Task importFromConfluenceTask = project.getTasks().create("importFromConfluence");
    importFromConfluenceTask.setGroup(GROUP);
    DoctoolChainDsl doctoolChainDsl = project.getExtensions().create("doctoolchain", DoctoolChainDsl.class);
    project.afterEvaluate(project1 -> {
      for (DoctoolchainImport nextImport: doctoolChainDsl.getImportList()) {
        ImportFromConfluenceTask importFromConfluenceScenarioTask = project1.getTasks().register("importFromConfluence" + nextImport.folder, ImportFromConfluenceTask.class).get();
        importFromConfluenceScenarioTask.setDescription("Initially import an confluence page and its subpages (" + nextImport.folder + ")");
        importFromConfluenceScenarioTask.setPageId(nextImport.pageId);
        importFromConfluenceScenarioTask.setConfluenceUrl(nextImport.url);
        importFromConfluenceScenarioTask.setFolder(nextImport.folder);
        importFromConfluenceScenarioTask.setUsername(user);
        importFromConfluenceScenarioTask.setPassword(password);
        importFromConfluenceScenarioTask.setGroup(GROUP);
        importFromConfluenceTask.dependsOn(importFromConfluenceScenarioTask);
      }

    });


  }

  private void createForwardTask (final Project project, final String taskname, String description, final String user, final String password) {
    Exec execTask = project.getTasks().create(taskname, Exec.class);
    execTask.setDescription(description);
    execTask.setGroup(GROUP);
    execTask.commandLine("docToolchain/bin/doctoolchain", ".", "-s", taskname, "-Dconfluence.user=" + user, "-Dconfluence.plainPassword=" + password);
    execTask.doFirst(task -> {

      File doctoolChainPathDownloaded = project.file("build/docToolchain.zip");
      File extractedPath = project.file("docToolchain-master");
      File targetPath = project.file("docToolchain");
      if (! doctoolChainPathDownloaded.exists()) {
        try {
          FileUtils.copyInputStreamToFile(new URL("https://github.com/docToolchain/docToolchain/archive/master.zip").openStream(), doctoolChainPathDownloaded );
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }

      if (! extractedPath.exists()) {
        project.copy(spec -> {
          spec.from(project.zipTree(doctoolChainPathDownloaded));
          spec.into(project.getProjectDir());
        });
        extractedPath.renameTo(targetPath);
      }
    });
  }
}
