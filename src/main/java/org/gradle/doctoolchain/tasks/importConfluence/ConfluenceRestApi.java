package org.gradle.doctoolchain.tasks.importConfluence;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * wrapper around the confluence rest api
 */
public class ConfluenceRestApi implements Serializable {

  /**
   * Encoding.
   */
  private static final String ENCODING = "utf-8";

  private String baseUrl;
  private String username;
  private String password;

  public ConfluenceRestApi (final String baseUrl, final String username, String password) {
    this.baseUrl = baseUrl;
    this.username = username;
    this.password = password;
  }

  /**
   * get the URL for te rest call
   * @param contentId  ID of page
   * @param expansions fields to get
   * @return created url
   * @throws UnsupportedEncodingException Encoding problem
   */
  private String getContentRestUrl(final Long contentId, final String[] expansions) throws UnsupportedEncodingException {
    final String expand = URLEncoder.encode(StringUtils.join(expansions, ","), ENCODING);
    return String.format("%s/rest/api/content/%s?expand=%s&os_authType=basic&os_username=%s&os_password=%s",
        baseUrl,
        contentId,
        expand,
        URLEncoder.encode(username, ENCODING),
        URLEncoder.encode(password, ENCODING));
  }

  public List<Page> getChildren (final Long contentID) throws IOException, JSONException {
    List<Page> children = new ArrayList<Page>();
    final HttpClient client = new DefaultHttpClient();
    String url = String.format("%s/rest/api/content/search?cql=parent=%s&os_authType=basic&os_username=%s&os_password=%s",
        baseUrl,
        contentID,
        URLEncoder.encode(username, ENCODING),
        URLEncoder.encode(password, ENCODING));
    final HttpGet getPageRequest = new HttpGet(url);
    final HttpResponse getPageResponse = client.execute(getPageRequest);
    HttpEntity pageEntity = getPageResponse.getEntity();
    JSONObject jsonObject = new JSONObject(IOUtils.toString(pageEntity.getContent()));
    JSONArray jsonArray = (JSONArray) jsonObject.get("results");
    for (int i = 0; i < jsonArray.length(); i++) {
      JSONObject nextObject = (JSONObject) jsonArray.get(i);
      Page page = new Page();
      page.setId(nextObject.getLong("id"));
      page.setTitle(nextObject.getString("title"));
      String content = getPageContent(page.getId());
      Document document = Jsoup.parse(content);
      page.setContent(document.outerHtml());
      children.add(page);
    }

    return children;
  }

  /**
   * Holt das JSON-Objekt für eine Page-ID
   * @param pageId die Page-ID
   * @return das erzeugte JSONObjekt
   * @throws IOException   Fehler beim Pagezugriff
   * @throws JSONException Fehler beim Zugriff auf das JSON-Objekt
   */
  private JSONObject getJSONObject(final long pageId) throws IOException, JSONException {
    final HttpClient client = new DefaultHttpClient();

    // Get current page version
    String pageObj = null;
    HttpEntity pageEntity = null;
    try {
      String url = getContentRestUrl(pageId, new String[]{"body.storage", "version"});
      System.out.println (url);
      final HttpGet getPageRequest = new HttpGet(url);
      final HttpResponse getPageResponse = client.execute(getPageRequest);
      pageEntity = getPageResponse.getEntity();

      pageObj = IOUtils.toString(pageEntity.getContent());
    } finally {
      if (pageEntity != null) {
        EntityUtils.consume(pageEntity);
      }
    }

    // Parse response into JSON
    return new JSONObject(pageObj);
  }

  /**
   * Liefert den Content für die übergebene Page-ID zurück.
   * @param pageId die Page-ID
   * @return der Content der Age
   * @throws IOException   Fehler beim Zugriff auf das JSON-Objekt
   * @throws JSONException Fehler beim Zugriff auf das JSON-Objekt
   */
  public String getPageContent(final long pageId) throws IOException, JSONException {
    // The updated value must be Confluence Storage Format (https://confluence.atlassian.com/display/DOC/Confluence+Storage+Format), NOT HTML.
    return getJSONObject(pageId).getJSONObject("body").getJSONObject("storage").getString("value");
  }


}
