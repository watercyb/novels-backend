package com.blueberryjoy.novel.admin;

import com.blueberryjoy.novel.novel.TypeCount;
import com.blueberryjoy.novel.user.RoleCount;

import java.util.List;

public class Counts {
    public long userCount = 0;
    public long authorCount = 0;
    public long adminCount = 0;
    public long superAdminCount = 0;
    public long originalCount = 0;
    public long translationCount = 0;
    public final long chapterCount;

    public Counts(List<RoleCount> roleCounts, List<TypeCount> typeCounts, long chapterCount) {
        for (RoleCount roleCount : roleCounts) {
            if (roleCount.getRole().toString().equals("USER")) {
                userCount += roleCount.getCount();
            } else if (roleCount.getRole().toString().equals("AUTHOR")) {
                authorCount += roleCount.getCount();
            } else if (roleCount.getRole().toString().equals("ADMIN")) {
                adminCount += roleCount.getCount();
            } else if (roleCount.getRole().toString().equals("SUPERADMIN")) {
                superAdminCount += roleCount.getCount();
            }
        }
        for (TypeCount typeCount : typeCounts) {
            if (typeCount.getType().toString().equals("ORIGINAL")) {
                originalCount += typeCount.getCount();
            } else if (typeCount.getType().toString().equals("TRANSLATION")) {
                translationCount += typeCount.getCount();
            }
        }
        this.chapterCount = chapterCount;
    }
}
