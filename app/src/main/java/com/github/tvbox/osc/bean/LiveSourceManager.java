package com.github.tvbox.osc.bean;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LiveSourceManager {
    private static final String TAG = "LiveSourceManager";
    private static final String KEY_LIVE_SOURCE_LIST = "live_source_list";
    private static final String KEY_CURRENT_LIVE_SOURCE = "current_live_source_name";
    private static LiveSourceManager instance;
    private final Gson gson = new Gson();

    private LiveSourceManager() {
    }

    public static LiveSourceManager get() {
        if (instance == null) {
            instance = new LiveSourceManager();
        }
        return instance;
    }

    public List<LiveSourceItem> getSourceList() {
        String json = Hawk.get(KEY_LIVE_SOURCE_LIST, "");
        Log.d(TAG, "getSourceList: json length=" + json.length() + " json=" + (json.length() > 100 ? json.substring(0, 100) + "..." : json));
        if (json.isEmpty()) {
            List<LiveSourceItem> defaultList = getDefaultSources();
            saveSourceList(defaultList);
            Log.d(TAG, "getSourceList: json was empty, returning " + defaultList.size() + " default sources");
            return defaultList;
        }
        List<LiveSourceItem> list = null;
        try {
            Type listType = new TypeToken<ArrayList<LiveSourceItem>>() {}.getType();
            list = gson.fromJson(json, listType);
        } catch (Exception e) {
            Log.e(TAG, "getSourceList: parse error", e);
        }
        if (list == null || list.isEmpty()) {
            List<LiveSourceItem> defaultList = getDefaultSources();
            saveSourceList(defaultList);
            Log.d(TAG, "getSourceList: parsed list was null/empty, returning " + defaultList.size() + " default sources");
            return defaultList;
        }
        Log.d(TAG, "getSourceList: returning " + list.size() + " sources from storage");
        return list;
    }

    public void saveSourceList(List<LiveSourceItem> list) {
        Collections.sort(list);
        String json = gson.toJson(list);
        Hawk.put(KEY_LIVE_SOURCE_LIST, json);
    }

    public void addSource(LiveSourceItem item) {
        List<LiveSourceItem> list = getSourceList();
        for (LiveSourceItem existing : list) {
            if (existing.getName().equals(item.getName())) {
                existing.setUrl(item.getUrl());
                saveSourceList(list);
                return;
            }
        }
        list.add(item);
        saveSourceList(list);
    }

    public void removeSource(LiveSourceItem item) {
        List<LiveSourceItem> list = getSourceList();
        LiveSourceItem toRemove = null;
        for (LiveSourceItem s : list) {
            if (s.getName().equals(item.getName()) && s.getUrl().equals(item.getUrl())) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) {
            list.remove(toRemove);
            saveSourceList(list);
        }
    }

    public void updateSource(LiveSourceItem oldItem, LiveSourceItem newItem) {
        List<LiveSourceItem> list = getSourceList();
        for (int i = 0; i < list.size(); i++) {
            LiveSourceItem s = list.get(i);
            if (s.getName().equals(oldItem.getName()) && s.getUrl().equals(oldItem.getUrl())) {
                list.set(i, newItem);
                saveSourceList(list);
                return;
            }
        }
    }

    public void clearAll() {
        Hawk.put(KEY_LIVE_SOURCE_LIST, "");
    }

    public String getCurrentSourceUrl() {
        List<LiveSourceItem> list = getSourceList();
        if (list.isEmpty()) return "";
        String currentName = Hawk.get(KEY_CURRENT_LIVE_SOURCE, "");
        if (currentName.isEmpty()) {
            setCurrentSource(list.get(0));
            return list.get(0).getUrl();
        }
        for (LiveSourceItem item : list) {
            if (item.getName().equals(currentName)) {
                return item.getUrl();
            }
        }
        setCurrentSource(list.get(0));
        return list.get(0).getUrl();
    }

    public void setCurrentSource(LiveSourceItem item) {
        Hawk.put(KEY_CURRENT_LIVE_SOURCE, item.getName());
    }

    public LiveSourceItem getCurrentSource() {
        List<LiveSourceItem> list = getSourceList();
        if (list.isEmpty()) return null;
        String currentName = Hawk.get(KEY_CURRENT_LIVE_SOURCE, "");
        if (currentName.isEmpty()) {
            setCurrentSource(list.get(0));
            return list.get(0);
        }
        for (LiveSourceItem item : list) {
            if (item.getName().equals(currentName)) {
                return item;
            }
        }
        setCurrentSource(list.get(0));
        return list.get(0);
    }

    public String getCurrentSourceName() {
        LiveSourceItem current = getCurrentSource();
        return current != null ? current.getName() : "";
    }

    public List<LiveSourceItem> getDefaultSources() {
        List<LiveSourceItem> list = new ArrayList<>();
        list.add(new LiveSourceItem("Guovin(IPv4)", "https://gh-proxy.com/https://raw.githubusercontent.com/Guovin/iptv-api/gd/output/ipv4/result.m3u"));
        list.add(new LiveSourceItem("Kimentanm", "https://gh-proxy.com/https://raw.githubusercontent.com/Kimentanm/aptv/master/m3u/iptv.m3u"));
        list.add(new LiveSourceItem("ChinaIPTV", "https://gh-proxy.com/https://raw.githubusercontent.com/hujingguang/ChinaIPTV/main/cnTV_AutoUpdate.m3u8"));
        list.add(new LiveSourceItem("zwc456baby", "https://gh-proxy.com/https://raw.githubusercontent.com/zwc456baby/iptv_alive/refs/heads/master/live.m3u"));
        list.add(new LiveSourceItem("CandyMu", "https://gitlab.com/noimank/tvbox/-/raw/main/tvbox1.json"));
        list.add(new LiveSourceItem("yingshicang", "http://影视仓.com/"));
        list.add(new LiveSourceItem("Clun在线", "https://clun.top/box.json"));
        list.add(new LiveSourceItem("饭太硬", "http://fty.xxooo.cf/tv"));
        list.add(new LiveSourceItem("饭太硬2", "http://www.饭太硬.net/tv"));
        list.add(new LiveSourceItem("饭太硬3", "http://www.饭太硬.art/tv"));
        list.add(new LiveSourceItem("饭太硬4", "http://fty.888484.xyz/tv"));
        list.add(new LiveSourceItem("王二小（网盘4K）", "http://tvbox.王二小放牛娃.top"));
        list.add(new LiveSourceItem("王二小备用", "http://tvbox.xn--4kq62z5rby2qupq9ub.top/"));
        list.add(new LiveSourceItem("王二小（新）", "https://9280.kstore.vip/newwex.json"));
        list.add(new LiveSourceItem("嗷呜备用", "https://9763.kstore.vip/aowu.json"));
        list.add(new LiveSourceItem("牛二线路", "https://9280.kstore.space/wex.json"));
        list.add(new LiveSourceItem("南风线路", "https://gh-proxy.com/https://raw.githubusercontent.com/yoursmile66/TVBox/refs/heads/main/XC.json"));
        list.add(new LiveSourceItem("高天流云", "https://gh-proxy.com/https://raw.githubusercontent.com/gaotianliuyun/gao/master/js.json"));
        list.add(new LiveSourceItem("D佬线路", "http://rihou.cc:555/nzk/nzk0722.json"));   
        list.add(new LiveSourceItem("金鱼box接口", "http://mzrjk.top/VIP"));
        list.add(new LiveSourceItem("小盒子单仓", "http://xhztv.top/xhz"));
        list.add(new LiveSourceItem("英格里希嗷呜", "http://www.英格里希嗷呜.top/tv"));
        list.add(new LiveSourceItem("宝盒接口", "http://宝盒接口.top"));
        Collections.sort(list);
        return list;
    }
}