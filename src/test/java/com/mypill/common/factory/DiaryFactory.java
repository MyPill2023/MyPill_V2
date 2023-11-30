package com.mypill.common.factory;

import com.mypill.domain.diary.dto.request.DiaryRequest;
import com.mypill.domain.diary.entity.Diary;
import com.mypill.domain.diary.entity.DiaryCheckLog;
import com.mypill.domain.member.entity.Member;

import java.time.LocalDate;
import java.time.LocalTime;

public class DiaryFactory {

    private DiaryFactory(){}

    public static Diary diary(Member member){
        return createDiary(member);
    }

    private static Diary createDiary(Member member){
        return Diary.of(member, "name", LocalTime.now());
    }

    public static DiaryCheckLog diaryCheckLog(Diary diary){
        return createDiaryCheckLog(diary);
    }

    private static DiaryCheckLog createDiaryCheckLog(Diary diary) {
        return new DiaryCheckLog(diary.getMember(), diary, "name", LocalDate.now());
    }

    public static DiaryRequest diaryRequest(){
        return new DiaryRequest("name", "09:00");
    }

}
