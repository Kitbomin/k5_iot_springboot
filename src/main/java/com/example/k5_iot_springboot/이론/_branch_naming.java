package com.example.k5_iot_springboot.이론;

/**
 * === 깃 브랜치 전략 ===
 * 1) 메인 브랜치 보호: main << 보호 >>
 *
 * 2) 개발 과정 통합 브랜치: develop
 *
 * 3) 작은 단위 기능 브랜치: 패턴[type/scope-short-desc]
 *
 * - 깃 작업 흐름(*로컬엣 ㅓ브랜치 만들면 안됨)
 *  : 이슈 생성 -> 기능 브랜치 생성 -> 커밋/푸쉬(로컬)
 *          -> PR(pull request) 생성 -> (리뷰/체크) 통과 -> develop에 merge
 *----------------
 * === 브랜치 네이밍 규칙 ===
 * [패턴]: type/scope-short-desc(describe)
 *--------------
 * 1) type: feature | fix | refactor | chore | docs | test
 *      feature     - 새로운 기능 추가 (실질적인 개발)
 *      fix         - 버그 수정
 *      refactor    - 기능 변경 없는 코드 리팩토링
 *      --- 밑에껀 앵간해선 안씀 ---
 *      chore       - 빌드 업무 및 설정 변경 (초기단계에서 많이 쓰임)
 *      docs        - 문서 수정 및 추가 (README.md 수정)
 *      test        - 테스트 코드 관련 작업
 *----------------
 * 2) scope: user | auth | task | project | infra | api (필요시 추가 가능)
 *      user        - 사용자 정보
 *      auth        - 인증/권한
 *      task        - 할 일 정보
 *      project     - 할 일 범주 관리
 *      ---- 프로젝트마다 도메인에 따라 달라질 ㅅ ㅜ있음 ---
 *      infra       - 서버, 데이터 베이스, 네트워크 등 서비스 기반이 되는 인프라 접근/관리
 *      api         - API 자체에 대한 접근 권한이나 API 호출에 필요한 범위 지정 (API 매핑 패턴이라던지 컨트롤러 매핑 수정이라던가...)
 * ----------------
 * 3) short-desc: 영문 소문자만 사용가능함 그리고 숫자 사용 가능함 근데 특수문자는 - 만 사용하는게 좋음 (권장)
 * [ 예시 ]
 * feature/task-create
 * fix/auth-token-expiry
 * refactor/project-service-layer
 * feature/task-search
 *...
 *
 * == 브랜치 로컬 생성 + 원격 연결 ==
 * 1) 새 브랜치 연결 및 전환
 * git checkout -b feature/task-create-deadline
 * ----------
 * 2) 원격 연결(최초 1회만: -u)
 * git push -u origin feature/task-create-deadline
 * ----------
 * 3) 이후에는 간단하게
 *      git push
 *      git pull 만으로 가능함
 * -----------
 * */





public class _branch_naming {
}
