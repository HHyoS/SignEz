package com.signez.signageproblemshooting.ui.tutorial

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.signez.signageproblemshooting.R
import com.signez.signageproblemshooting.data.datastore.StoreInitialLaunch
import com.signez.signageproblemshooting.ui.components.*
import com.signez.signageproblemshooting.ui.signage.*
import kotlinx.coroutines.launch
import java.util.*

//object TutorialDestination : NavigationDestination {
//    override val route = "ResultsHistoryScreen"
//    override val titleRes = "ResultsHistory"
//}

@OptIn(ExperimentalPagerApi::class, ExperimentalGlideComposeApi::class)
@Composable
fun TutorialView(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreInitialLaunch(context)
    val state = rememberPagerState()

    val tutorialimage = listOf<Int>(R.drawable.tutorial_1,R.drawable.tutorial_2,R.drawable.tutorial_3,R.drawable.tutorial_4,R.drawable.tutorial_5,R.drawable.tutorial_6)

    val tutorialtext = listOf<String>(
        "사이니지 스펙 입력 버튼을 눌러 분석할 사이니지를 선택 해 주세요.",
        "기존 설치된 사이니지를 선택하거나 사이니지를 새로 추가 할 수 있습니다.\n사이니지 상세 페이지에서는 캐비닛 정보를 설정 할 수 있습니다.",
        "오류 모듈을 분석할 사이니지 영상 또는 사진을 입력 해 주세요.",
        "분석 시작 전 사이니지의 세부 위치를 조정해주세요",
        "분석 후 오류 캐비닛과 모듈의 행과 열을 확인 하실 수 있습니다.",
        "사진 보기를 클릭시 근거 사진을 확인 할 수 있습니다.")

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colors.background),
        bottomBar = {
            if (state.currentPage == (state.pageCount - 1)) {
                BottomTutorialFlatButton(
                    leftTitle = "",
                    rightTitle = "",
                    isLeftUsable = false,
                    isRightUsable = false,
                    totalDots = state.pageCount,
                    selectedIndex = state.currentPage,
                    leftOnClickEvent = {
                    },
                    rightOnClickEvent = {
                    }
                )
            } else {
                BottomTutorialFlatButton(
                    leftTitle = "건너뛰기",
                    rightTitle = "다음",
                    isLeftUsable = true,
                    isRightUsable = true,
                    totalDots = state.pageCount,
                    selectedIndex = state.currentPage,
                    leftOnClickEvent = {
                        scope.launch {
                            dataStore.saveInitialLaunch(false)
                        }
                        (context as Activity).finish()
                    },
                    rightOnClickEvent = {
                        scope.launch {
                            dataStore.saveInitialLaunch(false)
                            state.animateScrollToPage(state.currentPage + 1)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Spacer(modifier = modifier.padding(innerPadding))

        Box(
            modifier = Modifier
                .padding(start = 24.dp, end = 24.dp)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SignEz Tutorial",
                    style = MaterialTheme.typography.h1,
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.padding(vertical = 50.dp)
                )
                HorizontalPager(
                    state = state,
                    count = 6,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.89f)
                ) { page ->
                    androidx.compose.material3.Card(
                        modifier = Modifier,
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colors.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            GlideImage(
                                model = tutorialimage.get(page),
                                contentDescription = "글라이드",
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .fillMaxHeight(0.7f)
                                    .clip(RoundedCornerShape(15.dp))
                                    .background(color = MaterialTheme.colors.surface)
                                    .padding(vertical = 10.dp)
                            )
                            Divider()
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(vertical = 16.dp, horizontal = 24.dp),
                                    text = tutorialtext[page],
                                    style = MaterialTheme.typography.h4,
                                    color = MaterialTheme.colors.onSurface,
                                )
                                if (page == 5) {
                                    Row(
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .align(alignment = Alignment.BottomCenter)
                                    ) {
                                        TutorialStartButton(title = "시작하기") {
                                            (context as Activity).finish()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

//    Column(){
//
//    Button(onClick = {
//        scope.launch {
//            dataStore.saveInitialLaunch(false)
//        }
//    }) {
//        Text("최초실행 버튼")
//    }
//
//    Button(onClick = {
//        scope.launch {
//            dataStore.saveInitialLaunch(true)
//        }
//    }) {
//        Text("최초실행취소 버튼")
//    }
//
//    Text("튜토리얼 페이지 입니다 ")
//    }
//}



