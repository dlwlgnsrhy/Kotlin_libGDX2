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
import kotlin.random.Random

class MainGameScreen : Screen {

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
    private val gravity = -10f
    private val jumpVelocity = 600f // 점프 높이를 2배로 설정
    private var jumpCount = 0 // 이단 점프 카운트
    private var isSliding = false
    private var slideTimer = 0f
    private val slideDuration = 1f // 슬라이드 지속 시간

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

        // 젤리 초기 위치 설정 (여러 개의 젤리 추가)
        createJellies()
    }
    private fun updateGround(delta: Float){
        groundX1 -= groundSpeed * delta

        //바닥 텍스처가 화면을 벗어나면 오른쪽 끝으로 이동
        if(groundX1 + groundTexture.width <=0){
            groundX1 += groundTexture.width
        }
    }
    private fun createJellies(){
        //2단 점프와 1단점프 동선에 계단형으로 젤리 배치
        val jellyHeightGap = 100f
        val startX = Gdx.graphics.width.toFloat()

        //젤리 배치(위로 올라가는 부분)
        for(i in 0 until 3){
            val xPos = startX + i * jellGap
            jellies.add(Rectangle(xPos, 150f + i *jellyHeightGap, jellyTexture.width / 4f, jellyTexture.height /4f))
        }
        //젤리 배치(내려가는 부분)
        for(i in 3 until 6){
            val xPos = startX + i *jellGap
            jellies.add(Rectangle(xPos, 150f + (5-i) *jellyHeightGap, jellyTexture.width / 4f, jellyTexture.height /4f))
        }
    }
    private fun checkCollision(){
        val characterRect = Rectangle(characterX, characterY, currentCharacterTexture.width.toFloat(),currentCharacterTexture.height.toFloat())
        val iterator = jellies.iterator()
        while(iterator.hasNext()){
            val jelly = iterator.next()
            if(characterRect.overlaps(jelly)){
                score +=10
                iterator.remove()
                if(score>=100){
                    gameCleared =true
                    break
                }
            }
        }
    }
    private fun updateJellies(delta :Float){
        jellies.forEach{it.x -=jellySpeed * delta}
        //젤리가 화면 왼쪽 끝으로 사라지면 다시 오른쪽에서 등장
        if(jellies.isEmpty()){
            createJellies()
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

        //바닥 업데이트
        updateGround(delta)

        // 젤리 업데이트
        updateJellies(delta)

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

        // 점수 표시
        font.draw(batch, "Score: $score", 20f, Gdx.graphics.height - 20f)

        batch.end()

        // UI 렌더링
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
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
