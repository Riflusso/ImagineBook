package io.github.jumperonjava.imaginebook.resolvers;

import io.github.jumperonjava.imaginebook.AsyncImageDownloader;
import io.github.jumperonjava.imaginebook.Image;

public class UrlResolver implements Resolver {
    public static AsyncImageDownloader DOWNLOADER = new AsyncImageDownloader();

    public static final UrlResolver INSTANCE = new UrlResolver("http:");
    public static final UrlResolver INSTANCE_S = new UrlResolver("https:");
    private final String protocol;

    public UrlResolver(String http) {
        this.protocol = http;
    }

    @Override
    public Image resolve(String path) {
        return DOWNLOADER.requestTexture(protocol+path);
    }
}
