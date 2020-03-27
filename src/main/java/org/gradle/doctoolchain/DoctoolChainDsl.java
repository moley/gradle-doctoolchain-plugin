package org.gradle.doctoolchain;

import java.util.ArrayList;
import java.util.List;

/**
 * extension for configuring doctoolchain plugin
 */
public class DoctoolChainDsl {


  private List<DoctoolchainImport> importList = new ArrayList<DoctoolchainImport>();

  /**
   * add an item to be imported from confluence
   *
   * @param pageId    page ID
   * @param url       url of confluence
   * @param folder    subfolder to save the importes pages to
   */
  public void importItem (String pageId, String url, final String folder) {
    DoctoolchainImport newImportItem = new DoctoolchainImport();
    newImportItem.pageId = pageId;
    newImportItem.url = url;
    newImportItem.folder = folder;
    importList.add(newImportItem);
  }

  public List<DoctoolchainImport> getImportList() {
    return importList;
  }



}
