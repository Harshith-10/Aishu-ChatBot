package com.github.harshith;

import org.javacord.api.entity.channel.Channel;

import java.util.ArrayList;

public class ChatsManager {
    private ArrayList<Channel> channels = new ArrayList<>();
    private ArrayList<Channel> bfchannels = new ArrayList<>();

    public ChatsManager(){
    }

    public void addLock(Channel channel){
        channels.add(channel);
    }

    public void addBFLock(Channel channel){
        addLock(channel);
        bfchannels.add(channel);
    }

    public void removeLock(Channel channel){
        channels.remove(channel);
        bfchannels.remove(channel);
    }

    public boolean getBFLock(Channel channel){
        return (bfchannels.indexOf(channel) > -1);
    }

    public boolean getLock(Channel channel){
        return (channels.indexOf(channel) > -1);
    }
}
