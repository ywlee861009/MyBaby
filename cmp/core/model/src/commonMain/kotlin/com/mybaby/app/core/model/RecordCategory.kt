package com.mybaby.app.core.model

enum class RecordCategory {
    ALL,             // 목록 필터용 (DB 저장 안함)
    WEIGHT,          // 체중
    BLOOD_PRESSURE,  // 혈압
    KICK,            // 태동
    PHOTO,           // 초음파 사진
    MEMO             // 메모
}
