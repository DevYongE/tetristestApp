# 🎮 Tetris Android Game

완전한 기능을 갖춘 Android 테트리스 게임입니다.

## ✨ 기능
- 7가지 테트로미노 조각 (I, O, T, S, Z, J, L)
- 부드러운 조작 및 회전
- 라인 클리어 및 점수 시스템
- 레벨업에 따른 속도 증가
- 터치 제스처 지원

## 🎯 조작법
- **좌/우 스와이프**: 조각 이동
- **탭**: 회전
- **아래 스와이프**: 즉시 낙하
- **버튼**: 모든 조작 가능

## 🚀 APK 다운로드

GitHub Actions가 자동으로 APK를 빌드합니다:

1. 이 저장소의 **Actions** 탭으로 이동
2. 최신 빌드 선택
3. **Artifacts** 섹션에서 `tetris-debug-apk` 다운로드
4. APK 파일을 Android 기기에 설치

또는 **Releases** 섹션에서 최신 버전 다운로드

## 📱 설치 방법

1. Android 기기에서 **설정 → 보안 → 알 수 없는 소스** 허용
2. 다운로드한 APK 파일 실행
3. 설치 완료 후 게임 실행

## 🔧 빌드하기

```bash
git clone https://github.com/[username]/tetris-android.git
cd tetris-android
./gradlew assembleDebug
```

APK 위치: `app/build/outputs/apk/debug/app-debug.apk`