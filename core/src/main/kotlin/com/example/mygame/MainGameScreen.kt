package com.example.mygame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ScreenViewport

class MainGameScreen : Screen {

    // 캐릭터 이미지 출력
    private val batch = SpriteBatch()
    private val characterTexture = Texture("character.png")

    // 캐릭터 위치 변수 추가
    private var characterX = 100f
    private var characterY = 150f
    private var velocityY = 0f

    // 점프와 슬라이드 관련 변수
    private val speedX = 200f
    private val gravity = -10f
    private val jumpVelocity = 300f
    private var isSliding = false
    private var slideTimer = 0f
    private val slideDuration = 1f // 지속 시간

    private val stage = Stage(ScreenViewport())

    // 슬라이드 처리 메서드
    private fun handleSlide(delta: Float) {
        if (isSliding) {
            slideTimer += delta
            if (slideTimer > slideDuration) {
                isSliding = false
                characterY = 150f // 원래 높이로 복귀
            } else {
                characterY = 120f // 슬라이드할 때 낮아진 높이
            }
        }
    }

    init {
        // 기본적인 Drawable을 사용하여 TextButton 스타일 설정
        val upDrawable = BaseDrawable()
        upDrawable.minWidth = 100f
        upDrawable.minHeight = 50f
//        upDrawable.backgroundColor = Color.DARK_GRAY

        val downDrawable = BaseDrawable()
        downDrawable.minWidth = 100f
        downDrawable.minHeight = 50f
//        downDrawable.backgroundColor = Color.GRAY

        val textButtonStyle = TextButtonStyle()
        textButtonStyle.font = BitmapFont() // 기본 폰트 설정
        textButtonStyle.fontColor = Color.WHITE // 텍스트 색상 설정
        textButtonStyle.up = upDrawable
        textButtonStyle.down = downDrawable

        // 점프 버튼 생성
        val jumpButton = TextButton("Jump", textButtonStyle)
        jumpButton.setPosition(50f, 50f)
        jumpButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                if (characterY == 150f) {
                    velocityY = jumpVelocity
                }
            }
        })

        // 슬라이드 버튼 생성
        val slideButton = TextButton("Slide", textButtonStyle)
        slideButton.setPosition(200f, 50f)
        slideButton.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: com.badlogic.gdx.scenes.scene2d.Actor?) {
                if (!isSliding && characterY == 150f) {
                    isSliding = true
                    slideTimer = 0f
                }
            }
        })

        // 스테이지에 버튼 추가
        stage.addActor(jumpButton)
        stage.addActor(slideButton)
        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {
        // 슬라이드 처리
        handleSlide(delta)

        // 캐릭터가 자동으로 앞으로 이동
        characterX += speedX * delta

        // 중력 적용
        velocityY += gravity
        characterY += velocityY * delta

        // 바닥에 닿으면 다시 설정
        if (characterY < 150f) {
            characterY = 150f
            velocityY = 0f
        }

        // 캐릭터 이동 처리
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        // 화면에 그리기
        batch.begin()
        batch.draw(characterTexture, characterX, characterY)
        batch.end()

        // 화면 경계 설정
        if (characterX > Gdx.graphics.width - characterTexture.width) {
            characterX = 0f // 화면을 벗어날 시 원위치
        }

        // UI 렌더링
        stage.act(Gdx.graphics.deltaTime)
        stage.draw()
    }

    // dispose 메서드
    override fun dispose() {
        batch.dispose()
        characterTexture.dispose()
        stage.dispose()
    }

    override fun resize(width: Int, height: Int) {}
    override fun pause() {}
    override fun resume() {}
    override fun show() {
        // 파일 경로와 존재 여부 확인
        Gdx.app.log("FileCheck", Gdx.files.internal("character.png").file().path)
        Gdx.input.inputProcessor = stage
    }

    override fun hide() {}
}
