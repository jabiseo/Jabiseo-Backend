
![image](https://github.com/user-attachments/assets/9aa37224-5e14-4590-b186-2b965bb87ad1)

# 자비서<img src="https://github.com/user-attachments/assets/99c2707e-1034-4b5e-89a2-ed9d648bc883" align=left width=120>

---

나만을 위한 자격증 스마트 학습 비서

<br><br>


> **소프트웨어 마에스트로 15기에서 활동한 프로젝트입니다.**

<br>

## 📚 사용 스택
<div align="left">
<div>
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=Spring Boot&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=Gradle&logoColor=white">
</div>

<div>
<img src="https://img.shields.io/badge/MySQL-4479A1.svg?style=flat-square&logo=MySQL&logoColor=white">
<img src="https://img.shields.io/badge/Redis-DC382D?style=flat-square&logo=Redis&logoColor=white">
<img src="https://img.shields.io/badge/-OpenSearch-005EB8?style=flat&logo=opensearch&logoColor=white"/>
</div>

<div>
<img src="https://img.shields.io/badge/Amazon AWS-232F3E?style=flat-square&logo=Amazon AWS&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=Docker&logoColor=white">
<img src="https://img.shields.io/badge/JSON Web Tokens-000000?style=flat-square&logo=JSON Web Tokens&logoColor=white">
</div>

<div>
<img src="https://img.shields.io/badge/Slack-4A154B?style=flat-square&logo=slack&logoColor=white">
</div>

</div>

<br/>



## 📁 Project Structure
멀티모듈 구조 사용
```bash
├── jabiseo-api 
│       └── com.jabiseo.api
│           └── <각 usecase 별 패키지> # ex : certificate, problem 등
│               └── application.usecase # 파사드 패턴으로 다른 도메인 서비스들의 반환값을 모아 응답값 생성
│               └── controller
│               └── dto
├── jabiseo-domain
│       └── com.jabiseo.domain
│           └── <도메인>  # ex : certificate , problem
│               └── domain # 도메인 오브젝트 및 레포지토리 인터페이스
│               └── dto 
│               └── exception # 도메인 별 에러 정의
│               └── service # 도메인 서비스
├── jabiseo-infrastructure # Redis, RestClient, Kafka, OpenSearch, S3 등 외부 서비스 연동
│       └── com.jabiseo.infrastructure
│           └── <외부 서비스>  # ex : Redis 등
└── jabiseo-notification # 알림 관련 로직
```


## 💻 Developers
<table>
    <tr align="center">
        <td><B>Lead•Backend</B></td>
        <td><B>Backend</B></td>
    </tr>
    <tr align="center">
        <td><B>장우석</B></td>
        <td><B>조인혁</B></td>
    </tr>
    <tr align="center">
        <td>
            <img src="https://github.com/morenow98.png?size=100">
            <br>
            <a href="https://github.com/morenow98"><I>morenow98</I></a>
        </td>
        <td>
            <img src="https://github.com/InHyeok-J.png?size=100" width="100">
            <br>
            <a href="https://github.com/InHyeok-J"><I>InHyeok-J</I></a>
        </td>
    </tr>
</table>
