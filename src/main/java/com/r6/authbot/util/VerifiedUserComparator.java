package com.r6.authbot.util;

import java.util.Comparator;

import com.r6.authbot.domain.VerifiedUser;

public class VerifiedUserComparator implements Comparator<VerifiedUser> {

    @Override
    public int compare(VerifiedUser v1, VerifiedUser v2) {
        int v1MMR = v1.getCurrentMMR();
        int v2MMR = v2.getCurrentMMR();

        if (v1MMR > v2MMR) {
            return -1;
        } else if (v1MMR < v2MMR) {
            return 1;
        } else {
            return compareWins(v1, v2);
        }
    }

    public int compareWins(VerifiedUser v1, VerifiedUser v2) {
        int v1Wins = v1.getCurrentWins();
        int v2Wins = v2.getCurrentWins();

        if (v1Wins > v2Wins) {
            return -1;
        } else if (v1Wins < v2Wins) {
            return 1;
        } else {
            return compareKills(v1, v2);
        }
    }

    public int compareKills(VerifiedUser v1, VerifiedUser v2) {
        int v1Kills = v1.getCurrentKills();
        int v2Kills = v2.getCurrentKills();

        if (v1Kills > v2Kills) {
            return -1;
        } else if (v1Kills < v2Kills) {
            return 1;
        } else {
            return 0;
        }
    }
}
