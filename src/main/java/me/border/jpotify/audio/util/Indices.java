package me.border.jpotify.audio.util;

import me.border.jpotify.audio.Song;
import me.border.jpotify.file.IndexFile;
import me.border.utilities.utils.AsyncScheduler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Indices {

    private Map<File, String> index;
    private Map<Song, String> modernIndices = new HashMap<>();
    private IndexFile indexFile;

    public Indices(IndexFile indexFile){
        this.indexFile = indexFile;
        this.index = indexFile.getItem();
    }

    public void put(File song, String info){
        index.put(song, info);
        save();
    }

    public void remove(Song song){
        index.remove(song.getDir());
        modernIndices.remove(song);
        save();
    }

    public boolean hasIndex(File file) {
        return index.containsKey(file);
    }

    public void put(Song song) {
        String info = index.get(song.getDir());
        if (info == null)
            return;
        modernIndices.put(song, info);
    }

    public String get(Song key){
        return modernIndices.get(key);
    }

    public void delete(){
        try {
            // DELETE apache io commons LATER
            index = null;
            modernIndices = null;
            FileUtils.forceDelete(indexFile.getFile());
            indexFile = null;
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void save(){
        AsyncScheduler.runTaskAsync(() -> indexFile.save());
    }

    @Override
    public String toString() {
        return "Indices{" +
                "index=" + index +
                ", modernIndices=" + modernIndices + '}';
    }
}
