package me.border.jpotify.audio;

import me.border.jpotify.util.Utils;
import me.border.utilities.file.watcher.FileEvent;
import me.border.utilities.file.watcher.FileListener;
import me.border.utilities.file.watcher.FileWatcher;

import java.io.File;
import java.util.*;

public class Playlist {

    private List<Song> songs = new ArrayList<>();
    private Map<String, Integer> indexMap = new HashMap<>();

    private String name;
    private File dir;
    private int index = 0;

    public Playlist(File dir) {
        this.dir = dir;
        this.name = dir.getName();
        File[] children = dir.listFiles();
        if (children == null){
            return;
        }
        for (File file : children){
            if (file.getName().endsWith(".mp3") || file.getName().endsWith(".m4a")) {
                Song song = new Song(file);
                songs.add(song);
                indexMap.put(song.getName(), index);
                this.index++;
            } else {
                System.out.println("Ignoring file " + file.getName() + " since it is not mp3.");
            }
        }
        initWatchService();
    }

    public void refreshPlaylistIndex(){
        index = 0;
        indexMap.clear();
        for (Song song : songs){
            int sIndex = songs.indexOf(song);
            String sName = song.getName();
            indexMap.put(sName, sIndex);
        }
    }

    public boolean hasSong(String song){
        return indexMap.containsKey(song);
    }

    public String getName(){
        return name;
    }

    public Map<String, Integer> getIndexMap(){
        return indexMap;
    }

    public List<Song> getSongs(){
        return songs;
    }

    public File getDir(){
        return dir;
    }

    private void initWatchService(){
        FileWatcher watcher = new FileWatcher(dir);
        watcher.addListener(new FileListener() {
            @Override
            public void onCreated(FileEvent event) {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        File file = event.getFile();
                        if (file.getName().endsWith(".mp3") || file.getName().endsWith(".m4a")) {
                            Song song = new Song(file);
                            songs.add(song);
                            indexMap.put(song.getName(), index);
                            index++;
                        } else {
                            System.out.println("Ignoring file " + file.getName() + " since it is not audio.");
                        }
                    }
                }, 500L);
            }

            @Override
            public void onModified(FileEvent event) {
                // nothing need to be done
            }

            @Override
            public void onDeleted(FileEvent event) {
                File file = event.getFile();
                if (file.getName().endsWith(".mp3") || file.getName().endsWith(".m4a")) {
                    String name = Utils.stripExtension(file.getName());
                    int index = indexMap.get(name);
                    songs.remove(index);
                    refreshPlaylistIndex();
                } else {
                    System.out.println("Ignoring file " + file.getName() + " since it is not audio.");
                }
            }
        });
        watcher.watch();
    }
}
