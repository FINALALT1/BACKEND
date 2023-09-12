![Generic badge](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Generic badge](https://img.shields.io/github/issues-pr-closed/FINALALT1/BACKEND)
<a href="https://github.com/FINALALT1/BACKEND/blob/develop/LICENSE" target="_blank">
    ![Generic badge](https://img.shields.io/github/license/FINALALT1/BACKEND)
</a>

# 💰 Money-Bridge 위치 기반 PB 매칭 플랫폼 BackEnd 💰
> PB와 일반 사용자 간에 유선 및 방문 상담을 연결 시스템을 구축한 백엔드 프로젝트로 <br>
> 고객이 사전에 자신이 어떤 상품에 대해 관심있는지, 어떤 상품과 PB가 매칭 될 수 있는지를 파악하고,<br>
> PB들은 ‘라운지’라는 공간에서 블로그처럼 시장의 사항들을 공유하며 실제 투자 포트폴리오 및 후기를 제공하여 신뢰도를 높입니다.<br>

![화면](https://github.com/FINALALT1/BACKEND/assets/33537820/dbc4aa45-fe5d-402b-bd46-f9b3bde34f58)

## 🕘 개발 기간
2023.06.08 ~ 2023. 06. 28
</br>
</br>
## :page_facing_up: 관련 문서 
### :books: [프로젝트 노션](https://yousunzoo.notion.site/0e436500b0bc459f9bcf00dbf259724c?pvs=4)
### :books: [API Docs(Swagger)](https://api.moneybridge.co.kr/swagger-ui.html)
</br>

## :bulb: 배포 사이트
### 📈 [Money Bridge](https://www.moneybridge.co.kr/)
</br>

## :bar_chart: ERD
![ERD](https://github.com/FINALALT1/BACKEND/assets/33537820/ac247587-5d09-459d-a3e8-f389419f89ac)
</br>
</br>

## :hammer: 아키텍처 구성도
<img width="850" alt="시스템구성도" src="https://github-production-user-asset-6210df.s3.amazonaws.com/90882909/258372817-323b6b0b-47aa-4faf-bde1-df4f45c97ff5.png">
</br>

## 👥 팀원 소개 및 개발 일지
| 변창우(팀장)                                                                                        | 김지수(팀원)                                                                                 | 이승민(팀원)                                                                                |
| --------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------- |
| [@wuchangb](https://github.com/wuchangb)                                                          | [@CHITSOO](https://github.com/CHITSOO)                                                   | [@berrypicker777](https://github.com/berrypicker777)                                                    |
|📍<b>서버 관리</b><br />- CI/CD 구축<br />- https 연결<br /><br />📍<b>메인페이지</b><br />- 맞춤 PB 리스트<br />- 성향 맞춤 컨텐츠 <br />- 증권사별 PB 리스트<br />- 전문분야별 PB 리스트<br />- 비회원 컨텐츠 제공<br /><br />📍<b>PB 리스트</b><br />전문분야 필터된 PB 리스트<br />거리순/경력순 정렬 PB 리스트<br /><br />📍<b>컨텐츠 상세 페이지</b><br />- 컨텐츠 저장/임시저장하기<br />- 컨텐츠 댓글/대댓글 기능<br />- 임시저장 컨텐츠 목록보기<br />- 컨텐츠 수정하기<br />- 컨텐츠 삭제하기<br /><br />📍<b>북마크 페이지</b><br />- 북마크한 컨텐츠 목록보기<br />- 북마크한 PB  목록보기<br /><br />📍<b>PB 상세 페이지</b><br />- PB 프로필 조회<br />- PB 프로필 수정, 포트폴리오 관리<br />- PB의 컨텐츠 리스트 조회<br />- 유사한 PB 추천<br />|📍<b>회원가입, 탈퇴, 로그인, 로그아웃</b><br />- User, PB 각각 회원가입<br />- 탈퇴시 연관데이터 즉시 삭제<br />- RTR방식으로 리프리세 토큰 1회만 사용<br />- 로그아웃시 레디스 블랙리스트에 등록<br /><br />📍<b>이메일 인증 및 알림</b><br />- 회원가입시 인증코드를 보내 이메일 인증<br />- 백오피스에서 로그인 시 이메일 인증<br />- PB 승인 및 거절시 이메일로 알림<br /><br />📍<b>마이 페이지 및 투자 성향 분석</b><br />- 설문조사에 근거해 투자성향을 분석<br />- 마이페이지에서 본인의 계정 관리<br />- s3와 cloudfront로 파일 등록및 가져오기<br />- 이미지 등록시 marvin 라이브러리로 압축<br /><br />📍<b>백오피스</b><br />- 관리자 승격, 회원가입 승인<br />- 회원 강제 탈퇴, 게시글 강제 삭제<br />- 공지사항, 자주묻는 질문 추가<br />- 회원정보, 게시글, 예약 정보 조회<br /><br />📍<b>지점목록 추가</b><br /> - 네이버 지도 API로 각 지점의 지번 주소, <br />도로명 주소, 위도 경도 정보 추가<br /> | 📍<b>상담 예약</b><br /> - 투자자 상담 예약 신청<br /> - 상담 대상 PB 사전 정보 조회<br /><br />📍<b>고객 관리 페이지(PB)</b><br /> - 상담 목록, 현황 조회<br /> - 예약 CRUD, 상태 변경 처리<br /> - 상담 후기 목록 조회<br /><br />📍<b>나의 예약 페이지(투자자)</b><br /> - 상담 목록, 현황 조회<br /> - 상담 후기 작성<br /> - 작성한 상담 후기 조회 <br /><br />📍<b>일정 관리 페이지(PB)</b><br /> - 월/일 단위 예약 조회<br /> - 상담 시간 및 메시지 변경<br /><br />📍<b>백오피스</b><br />- 공지사항 CRUD<br />- 자주 묻는 질문 CRUD<br /><br />📍<b>카카오 알림톡 기능</b><br /> - 예약 관련 알림<br /><br />📍<b>Swagger 세팅</b><br /> <br />📍<b>개발 종료 후 유지보수</b><br /> | 
| [변창우 개발 일지](https://github.com/FINALALT1/BACKEND/issues?q=author%3Awuchangb+) | [김지수 개발 일지](https://github.com/FINALALT1/BACKEND/issues?q=author%3ACHITSOO+) | [이승민 개발 일지](https://github.com/FINALALT1/BACKEND/issues?q=author%3Aberrypicker777+) |

## 📖 프로젝트 컨벤션
프로젝트 [위키](https://github.com/FINALALT1/BACKEND/wiki/%F0%9F%94%88%ED%8C%80-%EC%BB%A8%EB%B2%A4%EC%85%98) 참조.<br>
그라운드룰 FE+BE [노션](https://www.notion.so/a62615b3a03f481980b0bcc863c8d41c) 참조
</br>

## 프로젝트 페이지별 소개 및 정리
:star: [프로젝트 소개 노션](https://flaxen-cornucopia-9a5.notion.site/68b5ea9211ee47d5a089109a6ed80dd7?pvs=4)
