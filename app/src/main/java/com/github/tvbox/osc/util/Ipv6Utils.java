package com.github.tvbox.osc.util;

import java.util.ArrayList;
import java.util.List;

public class Ipv6Utils {

    public static boolean isIpv6Url(String url) {
        if (url == null) return false;
        int schemeEnd = url.indexOf("://");
        String hostPart = schemeEnd >= 0 ? url.substring(schemeEnd + 3) : url;
        return hostPart.startsWith("[");
    }

    public static void sortUrlsIpv6First(List<String> urls, List<String> names) {
        if (urls == null || urls.size() <= 1) return;
        int size = urls.size();
        boolean hasNames = names != null && names.size() == size;
        List<String> urlsCopy = new ArrayList<>(urls);
        List<String> namesCopy = hasNames ? new ArrayList<>(names) : null;
        urls.clear();
        if (hasNames) names.clear();
        for (int i = 0; i < size; i++) {
            if (isIpv6Url(urlsCopy.get(i))) {
                urls.add(urlsCopy.get(i));
                if (namesCopy != null) names.add(namesCopy.get(i));
            }
        }
        for (int i = 0; i < size; i++) {
            if (!isIpv6Url(urlsCopy.get(i))) {
                urls.add(urlsCopy.get(i));
                if (namesCopy != null) names.add(namesCopy.get(i));
            }
        }
    }
}