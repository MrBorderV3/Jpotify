package me.border.jpotify.audio.util;

import me.border.jpotify.audio.Song;

import java.util.LinkedList;
import java.util.List;

public class History {

    private List<Song> list = new LinkedList<>();

    public void add(Song song){
        list.add(song);
    }

    public Song poll(){
        if (list.isEmpty()){
            return null;
        }
        int index = list.size()-1;
        Song song = list.get(index);
        list.remove(index);

        return song;
    }

    public void reset(){
        list.clear();
    }
}
