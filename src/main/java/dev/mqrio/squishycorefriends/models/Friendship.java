package dev.mqrio.squishycorefriends.models;

public class Friendship {
    /*
    firstPlayer is always the player who started the friendship (the player who sent the friend request)
     */

    public Integer friendshipID;
    public String firstPlayerUUID;
    public String firstPlayerUsername;
    public String secondPlayerUUID;
    public String secondPlayerUsername;

    public Integer HugsCount;

    public double HomePosX;
    public double HomePosY;
    public double HomePosZ;
    public String HomeWorld;

    public Long ActiveSince;
}
