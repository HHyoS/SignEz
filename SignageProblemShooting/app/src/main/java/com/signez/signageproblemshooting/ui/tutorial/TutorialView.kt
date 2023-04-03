package com.signez.signageproblemshooting.ui.tutorial

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TutorialView(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreInitialLaunch(context)
    val state = rememberPagerState()


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
                    count = 5,
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(0.89f)
                ) { page ->
                    androidx.compose.material3.Card(
                        modifier = Modifier,
//                            .padding(top = 8.dp, bottom = 8.dp)
//                            .fillMaxWidth()
//                            .fillMaxHeight(0.89f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colors.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Image(
                                modifier = Modifier
                                    .fillMaxHeight(0.7f)
                                    .padding(vertical = 8.dp),
                                painter = painterResource(id = R.drawable.bluesign),
                                contentDescription = "Tutorial"
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
                                    text = "설명설명",
                                    style = MaterialTheme.typography.h4,
                                    color = MaterialTheme.colors.onSurface,
                                )
                                if (page == 4) {
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



