# Kotlin_AdventureRunner

## Project Overview
`Kotlin_AdventureRunner`는 코틀린과 LibGDX를 사용해 개발한 2D 런앤점프 게임입니다. 이 프로젝트를 통해 코틀린의 문법과 LibGDX의 기본 기능을 익히고, 게임 개발의 전체 흐름을 경험하는 것을 목표로 삼았습니다.

## Key Features
- **Character Movement**: 캐릭터는 점프와 슬라이드 동작을 통해 장애물을 회피할 수 있습니다.
- **Dynamic Obstacle Generation**: 게임 플레이 중 다양한 장애물이 생성되어 난이도가 점진적으로 증가합니다.
- **Collision Handling**: 캐릭터와 장애물 및 젤리 간의 충돌을 정확하게 감지하고 처리합니다.
- **Game State Management**: 게임 오버 및 재시작 기능을 통해 게임 상태를 관리합니다.

## Development Process
### Character Movement
- **코드 구현**: 캐릭터의 상태에 따라 움직임을 결정하는 `CharacterState` 열거형을 정의하고, `TouchListener`를 통해 점프 및 슬라이드 동작을 처리했습니다.
- **학습 포인트**: 코틀린의 `enum class`와 `when` 구문을 활용해 캐릭터의 상태를 효율적으로 관리하는 방법을 배웠습니다.

### Obstacle and Jelly Management
- **코드 구현**: `createObstaclesAndJellies()` 메서드를 통해 장애물과 젤리를 주기적으로 생성하고, `Rectangle` 클래스를 사용해 충돌 영역을 관리했습니다.
- **학습 포인트**: `ArrayList`를 활용해 동적으로 게임 오브젝트를 관리하는 방법과 `Rectangle`의 `overlaps` 메서드를 통한 충돌 감지 기법을 익혔습니다.

### UI/UX Enhancements
- **코드 구현**: 게임 오버 시 재시작 버튼을 추가해 게임을 초기 상태로 쉽게 리셋할 수 있도록 했습니다.
- **학습 포인트**: `stage.clear()`를 활용해 UI 요소를 재구성하고, `TextButton`의 스타일링을 통해 버튼의 외형을 커스터마이즈하는 방법을 배웠습니다.

### Troubleshooting & Challenges
#### 화면 비율 및 해상도 문제
- **문제**: 다양한 해상도에서 배경 이미지가 올바르게 표시되지 않는 이슈가 발생했습니다.
- **해결 방법**: `TextureRegion`과 `Viewport`를 사용해 화면 크기에 맞춰 이미지를 동적으로 조정했습니다.

#### UI 초기화 문제
- **문제**: 게임 재시작 시 버튼이 제대로 초기화되지 않는 문제가 발생했습니다.
- **해결 방법**: `stage.clear()` 후 필요한 UI 요소를 재생성하는 코드로 리팩토링하여 문제를 해결했습니다.

## Lessons Learned
- **Kotlin과 LibGDX의 시너지**: 코틀린의 간결한 문법과 LibGDX의 강력한 기능을 조합하여 효율적인 게임 개발을 경험했습니다.
- **게임 개발의 전반적인 흐름**: 아이디어 구상부터 구현, 문제 해결, 최적화에 이르기까지의 모든 과정을 경험하며 실무에 필요한 기본적인 역량을 키울 수 있었습니다.
- **확장성과 유지보수성**: 모듈화된 코드와 객체지향적 설계의 중요성을 실감했습니다. 앞으로의 프로젝트에서 이와 같은 접근 방식을 적극 활용할 계획입니다.

## Future Work
- **레벨 디자인**: 다양한 레벨과 난이도를 추가해 게임의 깊이를 더할 계획입니다.
- **UI/UX 개선**: 더 나은 사용자 경험을 위해 다양한 버튼 디자인과 애니메이션 효과를 추가할 예정입니다.
- **플랫폼 확장**: 현재 Android 플랫폼에서 실행되는 게임을 iOS와 웹에서도 사용할 수 있도록 확장할 계획입니다.
