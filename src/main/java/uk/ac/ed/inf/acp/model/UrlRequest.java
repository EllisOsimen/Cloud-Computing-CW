package uk.ac.ed.inf.acp.model;

public class UrlRequest {
    private String urlPath;

    public String getUrlPath() {
        return urlPath;
    }
    UrlRequest(String url) {
        this.urlPath = url;
    }
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
}
