package com.jabiseo.domain.notification.domain;

public enum PushType {

    PLAN("PLAN","학습 플랜 알림", "공부중인 학습 플랜의 진행 상황을 확인해보세요!");

    private String type;
    private String title;
    private String message;

    PushType(String type, String title, String message) {
        this.type = type;
        this.title = title;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}
