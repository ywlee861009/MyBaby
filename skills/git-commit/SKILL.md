---
name: git-commit
description: Stage 변경 사항을 분석하여 Conventional Commits 스타일의 한글 메시지를 제안하고 사용자 승인을 받아 커밋을 수행합니다. "커밋해줘", "git-commit" 등의 요청에 트리거됩니다.
---

# Git Commit Skill (Conventional Commits in Korean)

이 스킬은 현재 Git의 `staged`된 변경 사항을 분석하여 체계적인 커밋 메시지를 작성하고 실제 커밋을 도와줍니다.

## 워크플로우

1. **Staged 변경 확인**: 
   - `git status`와 `git diff --staged`를 실행하여 커밋할 내용을 확인합니다.
   - 만약 `staged`된 파일이 없다면, 사용자에게 `git add`를 먼저 하도록 안내하거나 "모두 추가하고 커밋할까요?"라고 묻습니다.

2. **메시지 생성 가이드**:
   - 변경 사항의 본질을 파악하여 다음 타입을 선택합니다:
     - `feat`: 새로운 기능 추가
     - `fix`: 버그 수정
     - `docs`: 문서 수정
     - `style`: 코드 포맷팅, 세미콜론 누락 등 (코드 변경 없음)
     - `refactor`: 코드 리팩토링
     - `test`: 테스트 코드 추가/수정
     - `chore`: 빌드 업무 수정, 패키지 매니저 설정 등
   - **제목**: `<type>: <한글 요약>` (50자 이내)
   - **본문 (필요시)**: 왜(Why)와 무엇을(What) 변경했는지 한글로 상세히 설명합니다.

3. **사용자 승인 (`ask_user` 필수)**:
   - 생성된 메시지를 사용자에게 보여주고 승인을 받습니다.
   - "이 메시지로 커밋할까요?" (예/아니오/수정)

4. **커밋 실행**:
   - 승인 시 `git commit -m "메시지"`를 실행합니다.
   - 커밋 성공 후 `git status`로 결과를 공유합니다.

## 예시

**변경 내용**: `AppNavigation.kt`에 패딩 추가 및 `Theme.kt` 색상 수정
**제안 메시지**: 
```text
feat: 시스템 바 패딩 적용 및 테마 색상 최적화

- Scaffold와 BottomNavBar에 safeDrawingPadding 적용
- iPhone 홈 인디케이터 대응을 위한 navigationBarsPadding 추가
- Pink/Lavender 브랜드 컬러 상숫값 정밀 조정
```
