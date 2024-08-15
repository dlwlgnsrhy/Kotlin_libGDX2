package com.example.mygame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.Timer
import com.badlogic.gdx.utils.viewport.ScreenViewport

class MainGameScreen : Screen {
    //게임이 일시 중지 상태인지 확인하기 위한 변수
    private var gamePaused = false

    // 바닥 텍스처 및 위치 변수
    private val groundTexture = Texture("ground.png")
    private val groundRegion = TextureRegion(groundTexture)

    private val groundSpeed = 300f
    private var groundX1 = 0f

    // 젤리 텍스처 및 위치 변수
    private val jellyTexture = Texture("jelly.png")
    private val jellies = mutableListOf<Rectangle>()
    private val jellySpeed = 200f // 젤리의 이동 속도
    private var jellGap = 100f //젤리 간의 간격
    private var gameCleared = false //게임 클리어 상태

    // 장애물 텍스처 및 위치 변수
    private val groundObstacleTexture = Texture("ground_obstacle.png") // 첫 번째 이미지 사용
    private val ceilingObstacleTexture = Texture("ceiling_obstacle.png") // 두 번째 이미지 사용
    private val obstacles = mutableListOf<Rectangle>()
    private val obstacleSpeed = 200f // 장애물 이동 속도
    private val obstacleHeightAdjustment = 200f // 장애물 높이 조정

    // 점수 변수 추가
    private var score = 0
    private val font = BitmapFont()

    // 캐릭터 이미지 출력
    private val batch = SpriteBatch()
    private val characterTextureNormal = Texture("character.png")
    private val characterTextureJump = Texture("character_jump.png")
    private val characterTextureSlide = Texture("character_slide.png")
    private val characterTextureWalk1 = Texture("character.png")
    private val characterTextureWalk2 = Texture("character_walk2.png")
    private var currentCharacterTexture = characterTextureNormal

    // 배경 이미지 관련 변수
    private val backgroundTexture = Texture("main_back.png")
    private val backgroundRegion1: TextureRegion
    private val backgroundRegion2: TextureRegion
    private var backgroundX1 = 0f
    private var backgroundX2: Float
    private var backgroundSpeed = 100f // 배경이 움직이는 속도

    // 캐릭터 상태 관련 변수
    private enum class CharacterState {
        WALKING, JUMPING, SLIDING, FALLING
    }
    private var characterState = CharacterState.WALKING

    // 캐릭터 위치 변수 추가
    private val characterX = 100f // X값 고정
    private var characterY = 150f
    private var velocityY = 0f

    // 점프와 슬라이드 관련 변수
    private val gravity = -8f
    private val jumpVelocity = 700f // 점프 높이를 2배로 설정
    private var jumpCount = 0 // 이단 점프 카운트
    private var isSliding = false
    private var slideTimer = 0f
    private val slideDuration = 1.5f // 슬라이드 지속 시간

    // 걷기 애니메이션 관련 변수
    private var walkTimer: Timer.Task? = null

    private val stage = Stage(ScreenViewport())

    init {
        //바닥의 두 번째 부분 시작 위치 설정
        groundX1 = 0f

        // 배경 이미지를 화면 크기에 맞게 조정
        backgroundRegion1 = TextureRegion(backgroundTexture)
        backgroundRegion1.setRegionWidth(Gdx.graphics.width)
        backgroundRegion1.setRegionHeight(Gdx.graphics.height)

        backgroundRegion2 = TextureRegion(backgroundTexture)
        backgroundRegion2.setRegionWidth(Gdx.graphics.width)
        backgroundRegion2.setRegionHeight(Gdx.graphics.height)

        backgroundX2 = backgroundRegion1.regionWidth.toFloat()

        // 기본적인 Drawable을 사용하여 TextButton 스타일 설정
        val upDrawable = BaseDrawable()
        val downDrawable = BaseDrawable()

        // 폰트 스케일 조정
        font.data.setScale(4f) // 폰트 크기 조절 (4배로 설정)

        val textButtonStyle = TextButtonStyle()
        textButtonStyle.font = font // 스케일된 폰트를 사용
        textButtonStyle.fontColor = Color.WHITE // 텍스트 색상 설정
        textButtonStyle.up = upDrawable
        textButtonStyle.down = downDrawable

        // 점프 버튼 생성
        val jumpButton = TextButton("Jump", textButtonStyle)
        jumpButton.setSize(300f, 150f) // 버튼의 크기를 명시적으로 설정
        jumpButton.setPosition(100f, 50f) // X 위치를 100f로 조정하여 약간 왼쪽으로 이동
        jumpButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                if (jumpCount < 2 && characterState != CharacterState.SLIDING) {
                    velocityY = jumpVelocity
                    characterState = CharacterState.JUMPING
                    currentCharacterTexture = characterTextureJump
                    jumpCount++
                    Gdx.app.log("CharacterState", "Jumping: Jump texture applied. Jump count: $jumpCount")
                    stopWalkingAnimation() // 점프 중에는 걷기 애니메이션 중지
                }
            }
        })

        // 슬라이드 버튼 생성
        val slideButton = TextButton("Slide", textButtonStyle)
        slideButton.setSize(300f, 150f) // 버튼의 크기를 명시적으로 설정
        slideButton.setPosition(1200f, 50f) // X 위치를 1200f로 조정하여 약간 오른쪽으로 이동
        slideButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                if (!isSliding && characterState == CharacterState.WALKING) {
                    isSliding = true
                    slideTimer = 0f
                    characterState = CharacterState.SLIDING
                    currentCharacterTexture = characterTextureSlide
                    Gdx.app.log("CharacterState", "Sliding: Slide texture applied.")
                    stopWalkingAnimation() // 슬라이드 중에는 걷기 애니메이션 중지
                }
            }
        })

        // 스테이지에 버튼 추가
        stage.addActor(jumpButton)
        stage.addActor(slideButton)
        Gdx.input.inputProcessor = stage

        // 걷기 애니메이션 시작
        startWalkingAnimation()

        // 젤리 및 장애물 초기 위치 설정
        createObstaclesAndJellies()
    }

    private fun updateGround(delta: Float){
        groundX1 -= groundSpeed * delta
        //바닥 텍스처가 화면을 벗어나면 오른쪽 끝으로 이동
        if(groundX1 + groundTexture.width <=0){
            groundX1 += groundTexture.width
        }
    }

    private fun createObstaclesAndJellies() {
        val jellyHeightGap = 100f // 각 층 사이의 간격
        val startX = Gdx.graphics.width.toFloat()
        val jellyWidth = jellyTexture.width / 4f
        val jellyHeight = jellyTexture.height / 4f
        val cycleGap = 2000f // 한 사이클이 끝난 후 다음 사이클의 시작 간격
        val obstacleStartOffsetX = 600f

        // 기본 장애물 추가
        obstacles.add(Rectangle(startX + obstacleStartOffsetX, 150f, jellyWidth * 2, jellyHeight + obstacleHeightAdjustment)) // x축을 2배로 확장

        // 젤리 생성 사이클 함수
        fun addJellyCycle(startOffsetX: Float) {
            // 1층 → 2층 → 3층 → 2층 → 1층
            val firstPhase = listOf(1, 2, 3, 2, 1)
            for (i in firstPhase.indices) {
                val xPos = startOffsetX + i * jellGap + obstacleStartOffsetX * 2 // 장애물과의 간격 유지
                val yPos = 150f + (firstPhase[i] - 1) * jellyHeightGap
                jellies.add(Rectangle(xPos, yPos, jellyWidth, jellyHeight))
            }

            // 4층 → 5층 → 6층 → 5층 → 4층
            val secondPhase = listOf(4, 5, 6, 5, 4)
            val offsetX = firstPhase.size * jellGap // 첫 번째 단계 이후에 시작

            for (i in secondPhase.indices) {
                val xPos = startOffsetX + offsetX + i * jellGap + obstacleStartOffsetX * 2 // 장애물과의 간격 유지
                val yPos = 150f + (secondPhase[i] - 1) * jellyHeightGap
                jellies.add(Rectangle(xPos, yPos, jellyWidth, jellyHeight))
            }
        }

        // 첫 번째 사이클 생성 (기본 장애물 뒤에 젤리 생성)
        addJellyCycle(startX)

        // 천장 장애물 추가 (기본 장애물, 젤리 뒤에 생성)
        obstacles.add(Rectangle(startX + obstacleStartOffsetX * 3, Gdx.graphics.height - obstacleHeightAdjustment - jellyHeight, jellyWidth, (jellyHeight + obstacleHeightAdjustment) * 1.5f)) // y축을 1.5배로 확장

        // 두 번째 사이클 생성 (첫 번째 사이클 끝난 후 시작)
        addJellyCycle(startX + cycleGap)
    }

    private fun checkCollision() {
        val characterRect = Rectangle(characterX, characterY, currentCharacterTexture.width.toFloat(), currentCharacterTexture.height.toFloat())

        // 장애물과의 충돌 체크
        for (obstacle in obstacles) {
            if (characterRect.overlaps(obstacle)) {
                gameOver()
                return
            }
        }

        // 젤리와의 충돌 체크
        val iterator = jellies.iterator()
        while (iterator.hasNext()) {
            val jelly = iterator.next()
            if (characterRect.overlaps(jelly)) {
                score += 10
                iterator.remove()
                if (score >= 100) {
                    gameCleared = true
                    break
                }
            }
        }
    }

    private fun gameOver() {
        // 게임 오버 상태로 전환하고 재시작 버튼을 활성화
        Gdx.app.log("GameStatus", "Game Over!")
        characterState = CharacterState.FALLING
        gamePaused = true  //게임 멈춤
        createRetryButton()
    }

    private fun createRetryButton() {
        val retryButtonStyle = TextButtonStyle().apply {
            font = BitmapFont().apply { data.setScale(4f) }
            fontColor = Color.GOLD
            up = BaseDrawable()
            down = BaseDrawable()
        }

        val retryButton = TextButton("Retry", retryButtonStyle).apply {
            setSize(400f, 200f)
            setPosition(Gdx.graphics.width / 2f - 200f, Gdx.graphics.height / 2f - 100f)
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                    resetGame()
                }
            })
        }

        stage.addActor(retryButton)
    }

    private fun resetGame() {
        // 게임 상태 초기화
        score = 0
        characterY = 150f
        velocityY = 0f
        jumpCount = 0
        isSliding = false
        characterState = CharacterState.WALKING
        jellies.clear()
        obstacles.clear()
        createObstaclesAndJellies()

        stage.clear()  // 스테이지 초기화

        // 점프와 슬라이드 버튼을 다시 추가
        val jumpButton = TextButton("Jump", createTextButtonStyle()).apply {
            setSize(300f, 150f)
            setPosition(100f, 50f)
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                    if (jumpCount < 2 && characterState != CharacterState.SLIDING) {
                        velocityY = jumpVelocity
                        characterState = CharacterState.JUMPING
                        currentCharacterTexture = characterTextureJump
                        jumpCount++
                        Gdx.app.log("CharacterState", "Jumping: Jump texture applied. Jump count: $jumpCount")
                        stopWalkingAnimation()
                    }
                }
            })
        }
        stage.addActor(jumpButton)

        val slideButton = TextButton("Slide", createTextButtonStyle()).apply {
            setSize(300f, 150f)
            setPosition(1200f, 50f)
            addListener(object : ChangeListener() {
                override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                    if (!isSliding && characterState == CharacterState.WALKING) {
                        isSliding = true
                        slideTimer = 0f
                        characterState = CharacterState.SLIDING
                        currentCharacterTexture = characterTextureSlide
                        Gdx.app.log("CharacterState", "Sliding: Slide texture applied.")
                        stopWalkingAnimation()
                    }
                }
            })
        }
        stage.addActor(slideButton)

        gamePaused = false // 게임 재개
    }
    // TextButtonStyle을 생성하는 메서드
    private fun createTextButtonStyle(): TextButtonStyle {
        return TextButtonStyle().apply {
            font = BitmapFont().apply { data.setScale(4f) }
            fontColor = Color.WHITE
            up = BaseDrawable()
            down = BaseDrawable()
        }
    }

    private fun startWalkingAnimation() {
        stopWalkingAnimation() // 중복 실행 방지
        walkTimer = Timer.schedule(object : Timer.Task() {
            private var toggle = true

            override fun run() {
                if (characterState == CharacterState.WALKING) {
                    currentCharacterTexture = if (toggle) characterTextureWalk1 else characterTextureWalk2
                    toggle = !toggle
                    Gdx.app.log("CharacterState", "Walking: Animation frame toggled.")
                }
            }
        }, 0f, 0.25f) // 0.25초마다 이미지 변경
    }

    private fun stopWalkingAnimation() {
        walkTimer?.cancel()
        walkTimer = null
    }

    private fun handleSlide(delta: Float) {
        if (isSliding) {
            slideTimer += delta
            if (slideTimer > slideDuration) {
                isSliding = false
                characterY = 150f // 원래 높이로 복귀
                characterState = CharacterState.WALKING
                currentCharacterTexture = characterTextureNormal
                startWalkingAnimation() // 슬라이드가 끝난 후 걷기 애니메이션 시작
            } else {
                characterY = 120f // 슬라이드할 때 낮아진 높이
                currentCharacterTexture = characterTextureSlide
                stopWalkingAnimation() // 슬라이드 중에는 걷기 애니메이션 중지
            }
        }
    }

    private fun updateBackground(delta: Float) {
        backgroundX1 -= backgroundSpeed * delta
        backgroundX2 -= backgroundSpeed * delta
        // 배경 이미지가 화면 왼쪽으로 완전히 사라지면 위치를 초기화하여 반복되도록 설정
        if (backgroundX1 + backgroundRegion1.regionWidth < 0) {
            backgroundX1 = backgroundX2 + backgroundRegion2.regionWidth
        }
        if (backgroundX2 + backgroundRegion2.regionWidth < 0) {
            backgroundX2 = backgroundX1 + backgroundRegion1.regionWidth
        }
    }

    override fun render(delta: Float) {
        if(gamePaused) return
        // 화면 지우기
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // 배치 시작
        batch.begin()

        // 배경 그리기
        batch.draw(backgroundRegion1, backgroundX1, 0f)
        batch.draw(backgroundRegion2, backgroundX2, 0f)

        // 바닥 그리기
        var groundX = groundX1
        while (groundX < Gdx.graphics.width) {
            batch.draw(groundRegion, groundX, 0f) // 바닥을 0 높이에 그리기
            groundX += groundTexture.width
        }

        // 게임이 클리어되었을 때의 처리
        if (gameCleared) {
            // 게임 클리어 메시지 표시
            font.draw(batch, "Game Cleared!", Gdx.graphics.width / 2f - 100, Gdx.graphics.height / 2f)
            // 점수 표시
            font.draw(batch, "Score: $score", 20f, Gdx.graphics.height - 20f)
            batch.end()
            return // 게임이 클리어된 경우, 더 이상 업데이트 로직을 실행하지 않음
        }

        // 배경 업데이트
        updateBackground(delta)

        // 바닥 업데이트
        updateGround(delta)

        // 젤리와 장애물 업데이트
        updateJelliesAndObstacles(delta)

        // 슬라이드 처리
        handleSlide(delta)

        // 충돌 체크
        checkCollision()

        // 중력 적용
        velocityY += gravity
        characterY += velocityY * delta

        // 바닥에 닿으면 다시 설정
        if (characterY < 150f) {
            characterY = 150f
            velocityY = 0f
            jumpCount = 0 // 점프 카운트 리셋
            if (!isSliding) {
                characterState = CharacterState.WALKING
                startWalkingAnimation() // 바닥에 닿으면 걷기 애니메이션 시작
            }
        } else if (velocityY < 0 && characterState == CharacterState.JUMPING) {
            characterState = CharacterState.FALLING
            currentCharacterTexture = characterTextureNormal
            Gdx.app.log("CharacterState", "Falling: Returning to normal texture.")
        }
        // 캐릭터의 상태에 따라 텍스처를 결정
        when (characterState) {
            CharacterState.WALKING -> {
                val time = (System.currentTimeMillis() / 250) % 2
                currentCharacterTexture = if (time == 0L) characterTextureWalk1 else characterTextureWalk2
            }
            CharacterState.JUMPING -> currentCharacterTexture = characterTextureJump
            CharacterState.SLIDING -> currentCharacterTexture = characterTextureSlide
            CharacterState.FALLING -> currentCharacterTexture = characterTextureNormal
        }
        // 캐릭터 그리기
        batch.draw(currentCharacterTexture, characterX, characterY)

        // 젤리 그리기
        jellies.forEach { jelly ->
            batch.draw(jellyTexture, jelly.x, jelly.y, jelly.width, jelly.height)
        }

        // 장애물 그리기
        obstacles.forEach { obstacle ->
            if (obstacle.y < Gdx.graphics.height / 2) {
                // groundObstacle의 x축 크기 늘리기 (1.4배)
                batch.draw(groundObstacleTexture, obstacle.x, obstacle.y, obstacle.width * 1.4f, obstacle.height)
            } else {
                // ceilingObstacle의 y축 위치와 크기 조정
                val adjustedY = obstacle.y - obstacle.height   // 장애물이 더 내려오도록 Y 위치 조정
                val adjustedHeight = obstacle.height * 2f // Y축 크기 1.5배로 확대
                batch.draw(ceilingObstacleTexture, obstacle.x, adjustedY, obstacle.width, adjustedHeight)
            }
        }

        // 점수 표시
        font.draw(batch, "Score: $score", 20f, Gdx.graphics.height - 20f)

        batch.end()

        // UI 렌더링
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    private fun updateJelliesAndObstacles(delta: Float) {
        // 젤리와 장애물 이동
        jellies.forEach { it.x -= jellySpeed * delta }
        obstacles.forEach { it.x -= obstacleSpeed * delta }

        // 장애물과 젤리가 화면 왼쪽 끝으로 사라지면 다시 생성
        if (jellies.isEmpty() && obstacles.isEmpty()) {
            createObstaclesAndJellies()
        }
    }

    // dispose 메서드
    override fun dispose() {
        batch.dispose()
        characterTextureNormal.dispose()
        characterTextureJump.dispose()
        characterTextureSlide.dispose()
        characterTextureWalk1.dispose()
        characterTextureWalk2.dispose()
        backgroundTexture.dispose()
        jellyTexture.dispose()
        groundTexture.dispose()
        groundObstacleTexture.dispose()
        ceilingObstacleTexture.dispose()
        stage.dispose()
    }

    override fun resize(width: Int, height: Int) {}
    override fun pause() {}
    override fun resume() {}
    override fun show() {
        Gdx.input.inputProcessor = stage
    }

    override fun hide() {}
}
