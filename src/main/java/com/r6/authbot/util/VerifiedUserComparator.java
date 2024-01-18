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
            return 0;
        }
    }
}
