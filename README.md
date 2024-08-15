# Kotlin_AdventureRunner

## Why
이 프로젝트는 게임 개발에 대한 이해를 넓히고, 코틀린과 LibGDX를 활용한 2D 게임 개발 기술을 익히기 위해 시작되었습니다. 간단한 런앤점프 게임을 통해 게임 개발의 기본적인 흐름을 경험하고, 실제로 작동하는 게임을 완성하는 것을 목표로 하였습니다. 또한, 이 프로젝트는 향후 게임 개발자로서의 성장 가능성을 탐색하고, 팀 프로젝트에 필요한 협업 및 문제 해결 능력을 키우기 위한 발판이 되었습니다.

## What
`Kotlin_AdventureRunner`는 간단하지만 중독성 있는 런앤점프 게임으로, 플레이어가 장애물을 피하면서 캐릭터를 이동시키는 것을 목표로 합니다. 주요 기능은 다음과 같습니다:
- **Character Movement**: 캐릭터가 점프와 슬라이드를 통해 장애물을 피할 수 있습니다.
- **Obstacle and Jelly Management**: 다양한 장애물과 젤리를 생성하여 난이도를 조절합니다.
- **Collision Detection**: 충돌 감지와 함께 게임 오버 및 점수 관리를 처리합니다.
- **Game State Management**: 게임 오버 후 재시작 기능을 통해 게임의 상태를 쉽게 리셋할 수 있습니다.

## How

### Character Movement
- **구현 방식**: `TouchListener`를 사용해 버튼 클릭 시 캐릭터의 상태(걷기, 점프, 슬라이드)를 업데이트하고, `enum class`를 활용해 각 상태를 효율적으로 관리하였습니다.
- **배운 점**: 코틀린의 `enum class`와 `when` 구문을 활용하여 상태 전환을 깔끔하게 처리하는 방법을 배웠습니다. 또한, `TouchListener`를 통해 사용자 입력을 효과적으로 처리하는 기술을 익혔습니다.

### Obstacle and Jelly Management
- **구현 방식**: `createObstaclesAndJellies()` 메서드를 통해 주기적으로 장애물과 젤리를 생성하고, `Rectangle` 클래스를 사용해 충돌 영역을 관리하였습니다.
- **배운 점**: `ArrayList`와 `Rectangle`을 활용해 동적으로 오브젝트를 생성하고 관리하는 방법을 익혔으며, 충돌 감지 로직을 통해 게임의 핵심 메커니즘을 구현하는 법을 배웠습니다.

### Game State Management and UI
- **구현 방식**: 게임 오버 시 `Retry` 버튼을 생성하여 게임을 초기 상태로 재설정할 수 있도록 하였습니다. 이를 위해 `stage.clear()`와 필요한 UI 요소들을 다시 생성하는 코드를 리팩토링하였습니다.
- **배운 점**: 게임의 상태를 효율적으로 관리하는 방법과 UI 초기화의 중요성을 경험하였으며, 이를 통해 사용자 경험을 개선할 수 있었습니다.

### Troubleshooting
1. **배경 이미지 해상도 문제**: 다양한 해상도에서 배경 이미지가 올바르게 표시되지 않는 문제가 발생했으나, `TextureRegion`과 `Viewport`를 사용해 화면 크기에 맞춰 이미지를 동적으로 조정하였습니다.
2. **UI 초기화 문제**: `Retry` 버튼을 눌렀을 때 UI가 올바르게 초기화되지 않는 문제를 `stage.clear()`와 UI 요소 재생성을 통해 해결하였습니다.

## Conclusion
이 프로젝트를 통해 게임 개발의 전반적인 흐름을 이해할 수 있었으며, 코틀린과 LibGDX의 시너지 효과를 체감하였습니다. 게임의 구조적 설계부터 문제 해결, UI 관리에 이르기까지의 전 과정을 경험하며 실무에 필요한 기본적인 역량을 키웠습니다. 

이 프로젝트는 단순한 게임이지만, 신입 개발자로서 기초적인 게임 개발 능력을 입증하기에 충분한 사례라고 생각합니다. 앞으로 더 복잡한 게임을 개발하거나, 다양한 플랫폼에 게임을 배포하는 프로젝트에 참여할 수 있는 역량을 갖추기 위해 계속해서 학습하고 발전해 나가겠습니다.
