package uk.ac.ed.inf.acp.model;

public class UrlRequest {
    private String urlPath;

    public UrlRequest(){

    }
    public String getUrlPath() {
        return urlPath;
    }

    public UrlRequest(String url) {
        this.urlPath = url;
    }
    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }
}
